'use client';

import { Check, X, ChevronDown, ChevronUp } from 'lucide-react';
import { QuestionReviewDTO } from '@/types/backend';

interface QuestionReviewCardProps {
  question: QuestionReviewDTO;
  isExpanded: boolean;
  onToggleExpand: () => void;
  canViewAnswers: boolean;
}

export function QuestionReviewCard({
  question: q,
  isExpanded,
  onToggleExpand,
  canViewAnswers,
}: QuestionReviewCardProps) {
  const isSkipped = q.selectedOption == null;
  const isCorrect = q.isCorrect;
  const isWrong = !isSkipped && !isCorrect;

  return (
    <div className="p-4 rounded-xl border border-slate-200 bg-white space-y-3">
      {/* Question Meta Header */}
      <div className="flex items-center justify-between pb-2 mb-2 border-b border-slate-100">
        <div className="flex items-center gap-2">
          <span className="font-bold text-slate-900 text-sm">
            Câu {q.questionNumber}
          </span>
          <span className="px-2 py-0.5 rounded text-[11px] font-medium bg-slate-100 text-slate-600 border border-slate-200">
            Part {q.partNumber}
          </span>
        </div>

        <div>
          {isCorrect && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-xs font-medium bg-emerald-50/70 text-emerald-700 border border-emerald-200/80">
              <Check className="w-3 h-3 text-emerald-600" />
              <span>Chính xác</span>
            </span>
          )}
          {isWrong && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-xs font-medium bg-rose-50/70 text-rose-700 border border-rose-200/80">
              <X className="w-3 h-3 text-rose-600" />
              <span>Sai (Chọn {q.selectedOption})</span>
            </span>
          )}
          {isSkipped && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-xs font-medium bg-slate-100 text-slate-600 border border-slate-200">
              <span>Chưa làm</span>
            </span>
          )}
        </div>
      </div>

      {/* Content text */}
      {q.content && (
        <p className="text-xs sm:text-sm text-slate-800 font-medium">
          {q.content}
        </p>
      )}

      {/* Image preview if any */}
      {q.imageUrl && (
        <div className="my-2 rounded-lg overflow-hidden border border-slate-200 max-w-sm">
          <img
            src={q.imageUrl}
            alt={`Câu ${q.questionNumber}`}
            className="w-full object-contain"
          />
        </div>
      )}

      {/* Options A, B, C, D */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
        {(['A', 'B', 'C', 'D'] as const).map((optKey) => {
          const optText =
            optKey === 'A'
              ? q.optionA
              : optKey === 'B'
              ? q.optionB
              : optKey === 'C'
              ? q.optionC
              : q.optionD;

          const isUserChoice = q.selectedOption === optKey;
          const isCorrectChoice = q.correctOption === optKey;

          let optStyle = 'border-slate-200 bg-white text-slate-700';
          let circleStyle = 'bg-slate-100 text-slate-700';
          if (isCorrectChoice) {
            optStyle = 'border-emerald-200 bg-emerald-50/30 text-slate-800 font-medium';
            circleStyle = 'bg-emerald-100 text-emerald-800 font-bold';
          } else if (isUserChoice && !isCorrect) {
            optStyle = 'border-rose-200 bg-rose-50/30 text-slate-800 font-medium';
            circleStyle = 'bg-rose-100 text-rose-800 font-bold';
          }

          return (
            <div
              key={optKey}
              className={`p-2.5 rounded-lg border flex items-center justify-between ${optStyle}`}
            >
              <div className="flex items-center gap-2 truncate">
                <span className={`w-5 h-5 rounded-full flex items-center justify-center text-[11px] shrink-0 ${circleStyle}`}>
                  {optKey}
                </span>
                <span className="truncate">{optText || `Đáp án (${optKey})`}</span>
              </div>
              {isCorrectChoice && (
                <span className="text-[11px] text-emerald-700 font-medium shrink-0 ml-1">
                  ✓ Đúng
                </span>
              )}
              {isUserChoice && !isCorrect && (
                <span className="text-[11px] text-rose-700 font-medium shrink-0 ml-1">
                  ✗ Bạn chọn
                </span>
              )}
            </div>
          );
        })}
      </div>

      {/* Transcript for Listening */}
      {q.transcript && (
        <div className="mt-2.5 p-3 rounded-lg bg-slate-50 border border-slate-200 text-xs space-y-1">
          <span className="font-bold text-slate-700 block">Transcript / Bài đọc liên quan:</span>
          <p className="text-slate-600 whitespace-pre-line leading-relaxed italic">
            {q.transcript}
          </p>
        </div>
      )}

      {/* Explanation Toggle */}
      {canViewAnswers && q.explanation && (
        <div className="pt-1">
          <button
            type="button"
            onClick={onToggleExpand}
            className="text-xs font-semibold text-slate-700 hover:text-slate-900 inline-flex items-center gap-1 transition-colors"
          >
            <span>{isExpanded ? 'Thu gọn giải thích' : 'Xem giải thích chi tiết'}</span>
            {isExpanded ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
          </button>

          {isExpanded && (
            <div className="mt-2 p-3 rounded-lg bg-slate-50 border border-slate-200 text-xs text-slate-700 leading-relaxed whitespace-pre-line">
              <span className="font-bold text-slate-900 block mb-1">Giải thích chi tiết:</span>
              {q.explanation}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
