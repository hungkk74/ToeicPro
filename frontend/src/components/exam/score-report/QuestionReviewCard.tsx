'use client';

import { useMemo } from 'react';
import { Check, X, ChevronDown, ChevronUp } from 'lucide-react';
import { QuestionReviewDTO } from '@/types/backend';
import {
  parseQuestionContent,
  cleanOptionText,
  isToeicPart2,
  isToeicAudioOnlyPart,
  formatOptionDisplay,
} from '@/lib/questionUtils';

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

  const isPart2 = isToeicPart2(q.partNumber, q.questionNumber);
  const isAudioOnly = isToeicAudioOnlyPart(q.partNumber, q.questionNumber);
  const { cleanContent, extractedOptions } = useMemo(() => {
    return parseQuestionContent(q.content);
  }, [q.content]);

  const optionKeys = isPart2 ? (['A', 'B', 'C'] as const) : (['A', 'B', 'C', 'D'] as const);

  return (
    <div className="p-4 sm:p-5 rounded-xl border border-slate-200 bg-white space-y-3.5 font-sans shadow-xs">
      {/* Question Meta Header */}
      <div className="flex items-center justify-between pb-2 mb-2 border-b border-slate-100">
        <div className="flex items-center gap-2">
          <span className="font-semibold text-slate-900 text-sm">
            Câu {q.questionNumber}
          </span>
          <span className="px-2 py-0.5 rounded text-[11px] font-medium bg-slate-100 text-slate-700 border border-slate-200">
            Part {q.partNumber}
          </span>
        </div>

        <div>
          {isCorrect && (
            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded text-xs font-medium bg-emerald-50 text-emerald-800 border border-emerald-200">
              <Check className="w-3.5 h-3.5 text-emerald-700 stroke-[2.5]" />
              <span>Chính xác</span>
            </span>
          )}
          {isWrong && (
            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded text-xs font-medium bg-rose-50 text-rose-800 border border-rose-200">
              <X className="w-3.5 h-3.5 text-rose-700 stroke-[2.5]" />
              <span>Sai (Bạn chọn {q.selectedOption})</span>
            </span>
          )}
          {isSkipped && (
            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-600 border border-slate-200">
              <span>Chưa làm</span>
            </span>
          )}
        </div>
      </div>

      {/* Content text */}
      {cleanContent && (
        <p className="text-xs sm:text-sm text-slate-800 leading-relaxed font-normal">
          {cleanContent}
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
        {optionKeys.map((optKey) => {
          const optRaw =
            optKey === 'A'
              ? q.optionA
              : optKey === 'B'
              ? q.optionB
              : optKey === 'C'
              ? q.optionC
              : q.optionD;

          const cleanedOpt = cleanOptionText(optRaw);
          const cleanedExtracted = extractedOptions ? cleanOptionText(extractedOptions[optKey]) : '';
          const cleanedText = cleanedOpt || cleanedExtracted || '';
          const optionTextDisplay = isAudioOnly ? '' : formatOptionDisplay(optKey, cleanedText);

          const isUserChoice = q.selectedOption === optKey;
          const isCorrectChoice = q.correctOption === optKey;

          let optStyle = 'border-slate-200 bg-slate-50/50 text-slate-700';
          let circleStyle = 'bg-slate-100 text-slate-700';
          if (isCorrectChoice) {
            optStyle = 'border-emerald-300 bg-emerald-50/50 text-emerald-950 font-medium';
            circleStyle = 'bg-emerald-700 text-white font-bold';
          } else if (isUserChoice && !isCorrect) {
            optStyle = 'border-rose-300 bg-rose-50/50 text-rose-950 font-medium';
            circleStyle = 'bg-rose-700 text-white font-bold';
          }

          return (
            <div
              key={optKey}
              className={`p-2.5 rounded-lg border flex items-center justify-between transition-colors ${optStyle}`}
            >
              <div className="flex items-center gap-2 truncate">
                <span className={`w-5 h-5 rounded flex items-center justify-center text-[11px] shrink-0 ${circleStyle}`}>
                  {optKey}
                </span>
                {optionTextDisplay ? (
                  <span className="truncate">{optionTextDisplay}</span>
                ) : null}
              </div>
              {isCorrectChoice && (
                <span className="text-[11px] text-emerald-800 font-semibold shrink-0 ml-1">
                  ✓ Đáp án đúng
                </span>
              )}
              {isUserChoice && !isCorrect && (
                <span className="text-[11px] text-rose-800 font-semibold shrink-0 ml-1">
                  ✗ Bạn chọn
                </span>
              )}
            </div>
          );
        })}
      </div>

      {/* Transcript for Listening (Notion-style Callout) */}
      {q.transcript && (
        <div className="mt-2.5 p-3.5 rounded-r-lg bg-slate-50 border border-slate-200 border-l-[3px] border-l-amber-500 text-xs space-y-1">
          <span className="font-semibold text-slate-800 block font-serif">Transcript / Lời thoại:</span>
          <p className="text-slate-600 whitespace-pre-line leading-relaxed italic">
            {q.transcript}
          </p>
        </div>
      )}

      {/* Explanation Toggle (Notion-style Callout) */}
      {canViewAnswers && q.explanation && (
        <div className="pt-1">
          <button
            type="button"
            onClick={onToggleExpand}
            className="text-xs font-semibold text-blue-600 hover:text-blue-700 inline-flex items-center gap-1 transition-colors cursor-pointer"
          >
            <span>{isExpanded ? 'Thu gọn giải thích' : 'Xem giải thích chi tiết'}</span>
            {isExpanded ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
          </button>

          {isExpanded && (
            <div className="mt-2.5 p-3.5 rounded-r-lg bg-slate-50 border border-slate-200 border-l-[3px] border-l-blue-600 text-xs text-slate-700 leading-relaxed whitespace-pre-line">
              <span className="font-semibold text-slate-900 block mb-1 font-serif">Giải thích chi tiết:</span>
              {q.explanation}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
