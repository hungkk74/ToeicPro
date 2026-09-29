'use client';

import { useState, useEffect } from 'react';
import {
  ArrowRight,
  Clock,
  Award,
  Pause,
  Play,
  Layers,
  FileText,
  Lock,
  X,
} from 'lucide-react';
import { ExamResultDTO, ExamReviewDTO, QuestionReviewDTO } from '@/types/backend';
import { fetchExamReviewFromBackend } from '@/services/examService';
import { useScoreCountdown } from '@/hooks/useScoreCountdown';
import { getCefrBadge, formatTimeSpent } from './score-report/scoreReportUtils';
import { ScoreSummaryTab } from './score-report/ScoreSummaryTab';
import { ScorePartsTab } from './score-report/ScorePartsTab';
import { ScoreQuestionsTab } from './score-report/ScoreQuestionsTab';

interface ScoreReportModalProps {
  isOpen: boolean;
  onClose: () => void;
  examResult: ExamResultDTO;
  timeRemaining?: number;
  formatTime?: (sec: number) => string;
  answeredCount?: number;
  totalQuestions?: number;
  onViewDetailedReview?: () => void;
}

export default function ScoreReportModal({
  isOpen,
  onClose,
  examResult,
  answeredCount = 0,
  totalQuestions,
  onViewDetailedReview,
}: ScoreReportModalProps) {
  const [activeTab, setActiveTab] = useState<'summary' | 'parts' | 'questions'>('summary');
  const [reviewData, setReviewData] = useState<ExamReviewDTO | null>(null);
  const [loadingReview, setLoadingReview] = useState(false);
  const [questionFilter, setQuestionFilter] = useState<'all' | 'wrong' | 'correct' | 'skipped'>('all');
  const [expandedExplanation, setExpandedExplanation] = useState<Record<number, boolean>>({});

  const { countdown, isPaused, togglePause, pause, redirectToTarget } = useScoreCountdown({
    isOpen,
    activeTab,
  });

  useEffect(() => {
    if (!isOpen || !examResult?.attemptId) return;

    let isMounted = true;
    setLoadingReview(true);
    fetchExamReviewFromBackend(examResult.attemptId)
      .then((review) => {
        if (isMounted && review) {
          setReviewData(review);
        }
      })
      .catch((err) => {
        console.warn('Could not load detailed review:', err);
      })
      .finally(() => {
        if (isMounted) setLoadingReview(false);
      });

    return () => {
      isMounted = false;
    };
  }, [isOpen, examResult?.attemptId]);

  const handleTabChange = (tab: 'summary' | 'parts' | 'questions') => {
    setActiveTab(tab);
    if (tab !== 'summary') {
      pause();
    }
  };

  if (!isOpen) return null;

  const totalFromResults =
    (examResult.correctAnswers ?? 0) +
    (examResult.wrongAnswers ?? 0) +
    (examResult.skippedAnswers ?? 0);

  const resolvedTotal =
    totalFromResults > 0
      ? totalFromResults
      : totalQuestions && totalQuestions > 0
      ? totalQuestions
      : 200;

  const correctAnswers = examResult.correctAnswers ?? 0;
  const wrongAnswers = examResult.wrongAnswers ?? 0;

  const actualAnswered =
    examResult.correctAnswers != null && examResult.wrongAnswers != null
      ? correctAnswers + wrongAnswers
      : Math.min(resolvedTotal, answeredCount);

  const skippedAnswers =
    examResult.skippedAnswers ?? Math.max(0, resolvedTotal - actualAnswered);

  const completionPercentage =
    resolvedTotal > 0 ? Math.round((actualAnswered / resolvedTotal) * 100) : 0;

  const canViewAnswers =
    examResult.canViewAnswers !== undefined
      ? examResult.canViewAnswers
      : reviewData?.canViewAnswers !== undefined
      ? reviewData.canViewAnswers
      : resolvedTotal > 0
      ? actualAnswered * 100 >= resolvedTotal * 80
      : false;

  const requiredQuestionsToUnlock = Math.ceil(resolvedTotal * 0.8);
  const accuracyPercentage =
    actualAnswered > 0 ? Math.round((correctAnswers / actualAnswered) * 100) : 0;
  const actualTimeSpentStr = formatTimeSpent(examResult.timeSpentSeconds);

  const hasListeningQuestions = reviewData?.questions && reviewData.questions.length > 0
    ? reviewData.questions.some((q) => (q.partNumber || 0) <= 4)
    : resolvedTotal >= 100 || examResult.listeningScore !== undefined;

  const hasReadingQuestions = reviewData?.questions && reviewData.questions.length > 0
    ? reviewData.questions.some((q) => (q.partNumber || 0) >= 5)
    : resolvedTotal >= 100 || examResult.readingScore !== undefined;

  const hasListening = hasListeningQuestions || examResult.listeningScore !== undefined;
  const hasReading = hasReadingQuestions || examResult.readingScore !== undefined;
  const maxTotalScore = (hasListening && hasReading && resolvedTotal >= 100) || resolvedTotal >= 150 ? 990 : 495;
  const cefr = getCefrBadge(examResult.totalScore, maxTotalScore);

  const allReviewQuestions: QuestionReviewDTO[] = reviewData?.questions || [];
  const filteredQuestions = allReviewQuestions.filter((q) => {
    if (questionFilter === 'correct') return q.isCorrect;
    if (questionFilter === 'wrong') return !q.isCorrect && q.selectedOption != null;
    if (questionFilter === 'skipped') return q.selectedOption == null;
    return true;
  });

  const toggleExplanation = (qId: number) => {
    setExpandedExplanation((prev) => ({ ...prev, [qId]: !prev[qId] }));
  };

  return (
    <div
      className="fixed inset-0 z-[9999] bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-3 sm:p-4 overflow-y-auto animate-in fade-in duration-200"
      role="dialog"
      aria-modal="true"
    >
      <div className="relative w-full max-w-3xl bg-white rounded-2xl shadow-2xl border border-slate-200 overflow-hidden my-6 flex flex-col max-h-[92vh] animate-in zoom-in-95 duration-200">

        {/* Header */}
        <div className="bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between shrink-0">
          <div>
            <h2 className="text-base sm:text-lg font-bold text-slate-900 leading-tight">
              Nộp bài thành công!
            </h2>
            <p className="text-xs text-slate-500 mt-0.5">
              {examResult.examTitle || 'Đề thi TOEIC'} — Kết quả làm bài
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors"
            aria-label="Đóng"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Auto Redirect Countdown Bar */}
        <div className="bg-slate-50 border-b border-slate-200 px-6 py-2.5 flex items-center justify-between text-xs text-slate-600 shrink-0">
          <div className="flex items-center gap-2 font-medium">
            <Clock className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <span>
              {isPaused || activeTab !== 'summary' ? (
                <span>Đã tạm dừng tự động chuyển trang</span>
              ) : (
                <>
                  Tự động quay về danh sách đề thi sau{' '}
                  <strong className="text-slate-900 font-bold tabular-nums font-mono text-sm">
                    {countdown}s
                  </strong>
                </>
              )}
            </span>
          </div>

          <div className="flex items-center gap-1.5">
            <button
              type="button"
              onClick={togglePause}
              className="px-2.5 py-1 rounded-md text-[11px] font-medium border border-slate-200 bg-white hover:bg-slate-100 text-slate-700 transition-colors flex items-center gap-1"
              title={isPaused ? 'Tiếp tục đếm ngược' : 'Tạm dừng đếm ngược'}
            >
              {isPaused ? <Play className="w-3 h-3 text-slate-600" /> : <Pause className="w-3 h-3 text-slate-600" />}
              <span>{isPaused ? 'Tiếp tục' : 'Tạm dừng'}</span>
            </button>
            <button
              type="button"
              onClick={redirectToTarget}
              className="px-3 py-1 rounded-md bg-slate-900 hover:bg-slate-800 text-white font-medium text-[11px] transition-colors flex items-center gap-1 shadow-2xs"
            >
              <span>Về danh sách ngay</span>
              <ArrowRight className="w-3 h-3" />
            </button>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="flex items-center border-b border-slate-200 bg-white px-6 text-xs font-semibold shrink-0 gap-1">
          <button
            type="button"
            onClick={() => handleTabChange('summary')}
            className={`py-3 px-3.5 border-b-2 transition-colors flex items-center gap-1.5 ${
              activeTab === 'summary'
                ? 'border-slate-900 text-slate-900 -mb-px'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Award className="w-3.5 h-3.5" />
            <span>Tổng quan điểm số</span>
          </button>
          <button
            type="button"
            onClick={() => handleTabChange('parts')}
            className={`py-3 px-3.5 border-b-2 transition-colors flex items-center gap-1.5 ${
              activeTab === 'parts'
                ? 'border-slate-900 text-slate-900 -mb-px'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            <Layers className="w-3.5 h-3.5" />
            <span>Phân tích từng phần ({reviewData?.partSummaries?.length || 0} Parts)</span>
          </button>
          <button
            type="button"
            onClick={() => handleTabChange('questions')}
            className={`py-3 px-3.5 border-b-2 transition-colors flex items-center gap-1.5 ${
              activeTab === 'questions'
                ? 'border-slate-900 text-slate-900 -mb-px'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            {canViewAnswers ? (
              <FileText className="w-3.5 h-3.5" />
            ) : (
              <Lock className="w-3.5 h-3.5 text-slate-400" />
            )}
            <span>Xem lại câu hỏi & Lời giải ({resolvedTotal} câu)</span>
            {!canViewAnswers && (
              <span className="px-1.5 py-0.5 rounded text-[10px] font-semibold bg-slate-100 text-slate-600 border border-slate-200">
                Khóa (&lt;80%)
              </span>
            )}
          </button>
        </div>

        {/* Scrollable Body */}
        <div className="p-5 sm:p-6 overflow-y-auto space-y-5 flex-1">
          {activeTab === 'summary' && (
            <ScoreSummaryTab
              examResult={examResult}
              reviewData={reviewData}
              maxTotalScore={maxTotalScore}
              hasListening={hasListening}
              hasReading={hasReading}
              cefr={cefr}
              actualAnswered={actualAnswered}
              resolvedTotal={resolvedTotal}
              accuracyPercentage={accuracyPercentage}
              completionPercentage={completionPercentage}
              correctAnswers={correctAnswers}
              wrongAnswers={wrongAnswers}
              skippedAnswers={skippedAnswers}
              actualTimeSpentStr={actualTimeSpentStr}
              canViewAnswers={canViewAnswers}
              requiredQuestionsToUnlock={requiredQuestionsToUnlock}
              onNavigateToParts={() => handleTabChange('parts')}
            />
          )}

          {activeTab === 'parts' && (
            <ScorePartsTab
              reviewData={reviewData}
              loadingReview={loadingReview}
            />
          )}

          {activeTab === 'questions' && (
            <ScoreQuestionsTab
              canViewAnswers={canViewAnswers}
              completionPercentage={completionPercentage}
              requiredQuestionsToUnlock={requiredQuestionsToUnlock}
              actualAnswered={actualAnswered}
              resolvedTotal={resolvedTotal}
              questionFilter={questionFilter}
              setQuestionFilter={setQuestionFilter}
              correctAnswers={correctAnswers}
              wrongAnswers={wrongAnswers}
              skippedAnswers={skippedAnswers}
              loadingReview={loadingReview}
              filteredQuestions={filteredQuestions}
              expandedExplanation={expandedExplanation}
              onToggleExplanation={toggleExplanation}
              onViewDetailedReview={onViewDetailedReview}
              onClose={onClose}
              onNavigateToSummary={() => handleTabChange('summary')}
            />
          )}
        </div>
      </div>
    </div>
  );
}
