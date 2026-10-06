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
      <div className="py-12 px-4 text-center max-w-md mx-auto space-y-4 font-sans">
        <div className="w-11 h-11 rounded-lg bg-slate-100 text-slate-600 mx-auto flex items-center justify-center border border-slate-200">
          <Lock className="w-5 h-5" />
        </div>
        <div className="space-y-1.5">
          <h3 className="font-serif text-base font-semibold text-slate-950">
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
          className="px-4 py-2 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white rounded-lg text-xs font-medium transition-colors cursor-pointer shadow-xs"
        >
          Quay lại tổng quan điểm số
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-4 font-sans">
      {/* Subfilter Bar */}
      <div className="flex flex-wrap items-center justify-between gap-2 pb-2 border-b border-slate-200">
        <div className="flex items-center gap-1.5 flex-wrap">
          <button
            type="button"
            onClick={() => setQuestionFilter('all')}
            className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors cursor-pointer ${
              questionFilter === 'all'
                ? 'bg-blue-600 text-white shadow-xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-50'
            }`}
          >
            Tất cả ({resolvedTotal})
          </button>
          <button
            type="button"
            onClick={() => setQuestionFilter('wrong')}
            className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors flex items-center gap-1.5 cursor-pointer ${
              questionFilter === 'wrong'
                ? 'bg-blue-600 text-white shadow-xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-50'
            }`}
          >
            <span className="w-1.5 h-1.5 rounded-full bg-rose-600 shrink-0" />
            <span>Làm sai ({wrongAnswers})</span>
          </button>
          <button
            type="button"
            onClick={() => setQuestionFilter('correct')}
            className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors flex items-center gap-1.5 cursor-pointer ${
              questionFilter === 'correct'
                ? 'bg-blue-600 text-white shadow-xs'
                : 'bg-white border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-50'
            }`}
          >
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-600 shrink-0" />
            <span>Làm đúng ({correctAnswers})</span>
          </button>
          <button
            type="button"
            onClick={() => setQuestionFilter('skipped')}
            className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors flex items-center gap-1.5 cursor-pointer ${
              questionFilter === 'skipped'
                ? 'bg-blue-600 text-white shadow-xs'
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
            className="text-xs font-medium text-blue-600 hover:text-blue-700 flex items-center gap-1 transition-colors cursor-pointer"
          >
            <span>Mở toàn màn hình</span>
            <ExternalLink className="w-3.5 h-3.5" />
          </button>
        )}
      </div>

      {/* Questions List */}
      {loadingReview ? (
        <div className="py-12 text-center text-xs text-slate-500">
          Đang tải danh sách câu hỏi và lời giải chi tiết...
        </div>
      ) : filteredQuestions.length > 0 ? (
        <div className="space-y-3">
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
          Không có câu hỏi nào theo bộ lọc đã chọn.
        </div>
      )}
    </div>
  );
}
