'use client';

import { Lock, ExternalLink } from 'lucide-react';
import { QuestionReviewDTO } from '@/types/backend';
import { QuestionReviewCard } from './QuestionReviewCard';

interface ScoreQuestionsTabProps {
  canViewAnswers: boolean;
  completionPercentage: number;
  requiredQuestionsToUnlock: number;
  actualAnswered: number;
  resolvedTotal: number;
  questionFilter: 'all' | 'wrong' | 'correct' | 'skipped';
  setQuestionFilter: (f: 'all' | 'wrong' | 'correct' | 'skipped') => void;
  correctAnswers: number;
  wrongAnswers: number;
  skippedAnswers: number;
  loadingReview: boolean;
  filteredQuestions: QuestionReviewDTO[];
  expandedExplanation: Record<number, boolean>;
  onToggleExplanation: (qId: number) => void;
  onViewDetailedReview?: () => void;
  onClose: () => void;
  onNavigateToSummary: () => void;
}

export function ScoreQuestionsTab({
  canViewAnswers,
  completionPercentage,
  requiredQuestionsToUnlock,
  actualAnswered,
  resolvedTotal,
  questionFilter,
  setQuestionFilter,
  correctAnswers,
  wrongAnswers,
  skippedAnswers,
  loadingReview,
  filteredQuestions,
  expandedExplanation,
  onToggleExplanation,
  onViewDetailedReview,
  onClose,
  onNavigateToSummary,
}: ScoreQuestionsTabProps) {
  if (!canViewAnswers) {
    return (
      <div className="py-12 px-4 text-center max-w-md mx-auto space-y-4">
        <div className="w-12 h-12 rounded-2xl bg-slate-100 text-slate-500 mx-auto flex items-center justify-center border border-slate-200">
          <Lock className="w-6 h-6" />
        </div>
        <div className="space-y-1">
          <h3 className="text-base font-bold text-slate-900">
            Tính năng xem lại câu hỏi bị khóa
          </h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            Bạn đã hoàn thành <strong>{actualAnswered}/{resolvedTotal} câu ({completionPercentage}%)</strong>.
            Cần đạt tối thiểu <strong>80% ({requiredQuestionsToUnlock}/{resolvedTotal} câu)</strong> để xem chi tiết đáp án đúng và giải thích.
          </p>
        </div>
        <button
          type="button"
          onClick={onNavigateToSummary}
          className="px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-semibold transition-colors"
        >
          Quay lại tổng quan điểm số
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Subfilter Bar */}
      <div className="flex flex-wrap items-center justify-between gap-2 pb-1 border-b border-slate-100">
        <div className="flex items-center gap-1.5 flex-wrap">
          <button
            type="button"
            onClick={() => setQuestionFilter('all')}
            className={`px-3 py-1 rounded-lg text-xs font-semibold transition-colors ${
              questionFilter === 'all'
                ? 'bg-slate-900 text-white shadow-2xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-50'
            }`}
          >
            Tất cả ({resolvedTotal})
          </button>
          <button
            type="button"
            onClick={() => setQuestionFilter('wrong')}
            className={`px-3 py-1 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1.5 ${
              questionFilter === 'wrong'
                ? 'bg-slate-900 text-white shadow-2xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-50'
            }`}
          >
            <span className="w-1.5 h-1.5 rounded-full bg-rose-500 shrink-0" />
            <span>Làm sai ({wrongAnswers})</span>
          </button>
          <button
            type="button"
            onClick={() => setQuestionFilter('correct')}
            className={`px-3 py-1 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1.5 ${
              questionFilter === 'correct'
                ? 'bg-slate-900 text-white shadow-2xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-50'
            }`}
          >
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 shrink-0" />
            <span>Làm đúng ({correctAnswers})</span>
          </button>
          <button
            type="button"
            onClick={() => setQuestionFilter('skipped')}
            className={`px-3 py-1 rounded-lg text-xs font-semibold transition-colors flex items-center gap-1.5 ${
              questionFilter === 'skipped'
                ? 'bg-slate-900 text-white shadow-2xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-50'
            }`}
          >
            <span className="w-1.5 h-1.5 rounded-full bg-slate-400 shrink-0" />
            <span>Chưa làm ({skippedAnswers})</span>
          </button>
        </div>

        {onViewDetailedReview && (
          <button
            type="button"
            onClick={() => {
              onClose();
              onViewDetailedReview();
            }}
            className="text-xs font-semibold text-slate-700 hover:text-slate-900 flex items-center gap-1"
          >
            <span>Mở toàn màn hình</span>
            <ExternalLink className="w-3 h-3" />
          </button>
        )}
      </div>

      {/* Questions List */}
      {loadingReview ? (
        <div className="py-12 text-center text-xs text-slate-500">
          Đang tải danh sách câu hỏi và lời giải chi tiết...
        </div>
      ) : filteredQuestions.length > 0 ? (
        <div className="space-y-4">
          {filteredQuestions.map((q) => (
            <QuestionReviewCard
              key={q.questionId}
              question={q}
              isExpanded={Boolean(expandedExplanation[q.questionId])}
              onToggleExpand={() => onToggleExplanation(q.questionId)}
              canViewAnswers={canViewAnswers}
            />
          ))}
        </div>
      ) : (
        <div className="py-12 text-center text-xs text-slate-500">
          Không có câu hỏi nào khớp với bộ lọc này.
        </div>
      )}
    </div>
  );
}
