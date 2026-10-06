'use client';

import { useState, useMemo } from 'react';
import {
  Flag,
  BookOpen,
  Headphones,
  Lock,
  ArrowLeft,
  ArrowRight,
  CheckCircle2,
  XCircle,
  Lightbulb,
} from 'lucide-react';
import {
  parseQuestionContent,
  cleanOptionText,
  isToeicPart2,
  isToeicAudioOnlyPart,
  formatOptionDisplay,
} from '@/lib/questionUtils';

export interface QuestionOptionItem {
  key: string;
  text: string;
}

interface QuestionCardProps {
  currentQuestion: number;
  totalQuestions?: number;
  partNumber?: number;
  partName?: string;
  content?: string;
  passageText?: string;
  imageUrl?: string;
  audioUrl?: string;
  options?: QuestionOptionItem[];
  isFlagged: boolean;
  onToggleFlag: () => void;
  selectedAnswer?: string;
  onSelectAnswer: (ans: string) => void;
  onPrevQuestion: () => void;
  onNextQuestion: () => void;
  canGoPrev: boolean;
  canGoNext: boolean;
  isReviewMode?: boolean;
  correctOption?: string;
  explanation?: string;
  transcript?: string;
}

const DEFAULT_EMPTY_OPTIONS: QuestionOptionItem[] = [
  { key: 'A', text: '' },
  { key: 'B', text: '' },
  { key: 'C', text: '' },
  { key: 'D', text: '' },
];

export default function QuestionCard({
  currentQuestion,
  totalQuestions = 200,
  partNumber = 1,
  partName,
  content,
  passageText,
  imageUrl,
  audioUrl: _audioUrl,
  options = DEFAULT_EMPTY_OPTIONS,
  isFlagged,
  onToggleFlag,
  selectedAnswer,
  onSelectAnswer,
  onPrevQuestion,
  onNextQuestion,
  canGoPrev,
  canGoNext,
  isReviewMode = false,
  correctOption,
  explanation,
  transcript,
}: QuestionCardProps) {
  const [showTranscript, setShowTranscript] = useState(false);
  const isPart2 = isToeicPart2(partNumber, currentQuestion);
  const isAudioOnly = isToeicAudioOnlyPart(partNumber, currentQuestion);

  const { cleanContent, extractedOptions } = useMemo(() => {
    return parseQuestionContent(content);
  }, [content]);

  const displayOptions = useMemo(() => {
    const defaultOptions = isPart2
      ? [
          { key: 'A', text: '' },
          { key: 'B', text: '' },
          { key: 'C', text: '' },
        ]
      : DEFAULT_EMPTY_OPTIONS;

    const baseList = options && options.length > 0 ? options : defaultOptions;
    const filteredList = isPart2 ? baseList.filter((o) => o.key !== 'D') : baseList;

    return filteredList.map((opt) => {
      const cleanedOpt = cleanOptionText(opt.text);
      const cleanedExtracted = extractedOptions
        ? cleanOptionText(extractedOptions[opt.key as 'A' | 'B' | 'C' | 'D'])
        : '';
      const text = cleanedOpt || cleanedExtracted || '';
      return {
        key: opt.key,
        text,
      };
    });
  }, [options, extractedOptions, isPart2]);

  const isReadingPart = partNumber >= 5;

  return (
    <div className="bg-white rounded-xl p-5 sm:p-6 border border-slate-200/90 shadow-xs">
      {/* Question Header Meta */}
      <div className="flex items-center justify-between pb-3.5 border-b border-slate-100">
        <div className="flex items-center gap-3">
          <span className="inline-flex items-center justify-center w-7 h-7 rounded-md bg-blue-600 text-white font-semibold text-xs tabular-nums shrink-0 shadow-xs">
            {currentQuestion}
          </span>
          <div>
            <h2 className="font-serif text-sm sm:text-base font-semibold text-slate-950 tracking-tight leading-tight">
              Câu hỏi {currentQuestion} / {totalQuestions}
            </h2>
            <span className="text-xs text-slate-500 mt-0.5 block font-sans">
              {partName ? partName : `${isReadingPart ? 'Kỹ năng Đọc' : 'Kỹ năng Nghe'} • Part ${partNumber}`} • Q-{currentQuestion}
            </span>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={onToggleFlag}
            title={isFlagged ? 'Đã đặt cờ (Phím F để bỏ)' : 'Đặt cờ câu này (Phím F)'}
            className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-colors border cursor-pointer ${
              isFlagged
                ? 'text-amber-900 bg-amber-50 border-amber-300 font-semibold'
                : 'bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100 hover:text-slate-900'
            }`}
          >
            <Flag className={`w-3.5 h-3.5 ${isFlagged ? 'text-amber-600 fill-amber-600' : 'text-slate-400'}`} />
            <span>{isFlagged ? 'Đã đặt cờ' : 'Đặt cờ'}</span>
            <kbd className="hidden sm:inline-block text-[10px] font-mono text-slate-500 ml-0.5 px-1 rounded bg-slate-200/60">
              F
            </kbd>
          </button>
        </div>
      </div>

      {/* Reading Passage Stimulus (Part 6 & Part 7) - Authentic ETS Test Paper Look */}
      {passageText && (
        <div className="my-5 p-4 sm:p-5 bg-slate-50/70 border border-slate-200/90 rounded-lg">
          <div className="flex items-center gap-2 text-slate-900 font-semibold text-xs mb-2.5 font-serif">
            <BookOpen className="w-4 h-4 text-slate-700" />
            <span>Đoạn văn đọc hiểu (Reading Passage)</span>
          </div>
          <div className="max-w-prose mx-auto text-[15px] sm:text-base leading-relaxed text-slate-900 whitespace-pre-line p-4 sm:p-5 bg-white rounded border border-slate-200/80 font-serif">
            {passageText}
          </div>
        </div>
      )}

      {/* Dedicated Image Stimulus */}
      {imageUrl && (
        <div className="my-5">
          <div className="relative bg-slate-50 rounded-lg overflow-hidden border border-slate-200/80">
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img
              src={imageUrl}
              alt={`Hình ảnh câu hỏi ${currentQuestion}`}
              className="w-full max-h-96 object-contain bg-white mx-auto"
            />
          </div>
        </div>
      )}

      {/* Question Content / Stem */}
      {cleanContent && (
        <div className="my-4 p-3.5 sm:p-4 bg-slate-50/60 rounded-lg border border-slate-200/70 text-slate-950 text-sm sm:text-[15px] font-medium leading-relaxed font-sans">
          {cleanContent}
        </div>
      )}

      {/* Multiple Choice Options (A, B, C, D) */}
      <div className="flex flex-col gap-2.5 mt-5" role="radiogroup" aria-label="Phương án trả lời">
        {displayOptions.map((opt) => {
          const isSelected = selectedAnswer === opt.key;
          const isCorrectChoice = isReviewMode && correctOption === opt.key;
          const isUserWrongChoice = isReviewMode && isSelected && !isCorrectChoice;
          const optionTextDisplay = isAudioOnly ? '' : formatOptionDisplay(opt.key, opt.text);

          let optionClasses = 'bg-white border-slate-200 hover:border-slate-400 text-slate-800';
          let badgeClasses = 'bg-slate-100 text-slate-700 border border-slate-200/80';

          if (isReviewMode) {
            if (isCorrectChoice) {
              optionClasses = 'bg-emerald-50/70 border-emerald-600 ring-1 ring-emerald-600 text-emerald-950 font-medium';
              badgeClasses = 'bg-emerald-700 text-white font-bold';
            } else if (isUserWrongChoice) {
              optionClasses = 'bg-rose-50/70 border-rose-500 ring-1 ring-rose-500 text-rose-950 font-medium';
              badgeClasses = 'bg-rose-700 text-white font-bold';
            } else {
              optionClasses = 'bg-slate-50/40 border-slate-200 text-slate-500 opacity-60';
              badgeClasses = 'bg-slate-100 text-slate-400';
            }
          } else if (isSelected) {
            optionClasses = 'bg-blue-50/70 border-blue-600 ring-1 ring-blue-600 text-slate-950 font-medium';
            badgeClasses = 'bg-blue-600 text-white font-bold';
          }

          return (
            <div
              key={opt.key}
              role={isReviewMode ? undefined : 'radio'}
              aria-checked={isReviewMode ? undefined : isSelected}
              tabIndex={isReviewMode ? undefined : 0}
              onKeyDown={(e) => {
                if (!isReviewMode && (e.key === ' ' || e.key === 'Enter')) {
                  e.preventDefault();
                  onSelectAnswer(opt.key);
                }
              }}
              onClick={() => {
                if (!isReviewMode) onSelectAnswer(opt.key);
              }}
              className={`group flex items-center justify-between p-3.5 sm:p-4 rounded-lg transition-colors border outline-hidden ${
                isReviewMode ? 'cursor-default' : 'cursor-pointer'
              } ${optionClasses}`}
            >
              <div className="flex items-center gap-3.5 flex-1 min-w-0">
                <div
                  className={`w-7 h-7 rounded-md flex items-center justify-center text-xs font-semibold shrink-0 transition-colors ${badgeClasses}`}
                >
                  {opt.key}
                </div>
                {optionTextDisplay ? (
                  <span className="text-sm sm:text-[15px] leading-relaxed break-words flex-1 font-sans">
                    {optionTextDisplay}
                  </span>
                ) : null}
              </div>

              {/* Status chips in Review Mode */}
              {isReviewMode && isCorrectChoice && (
                <span className="inline-flex items-center gap-1 text-xs font-semibold text-emerald-800 bg-emerald-100 px-2 py-0.5 rounded shrink-0 ml-2">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-700" />
                  <span>Đáp án đúng</span>
                </span>
              )}
              {isReviewMode && isUserWrongChoice && (
                <span className="inline-flex items-center gap-1 text-xs font-semibold text-rose-800 bg-rose-100 px-2 py-0.5 rounded shrink-0 ml-2">
                  <XCircle className="w-3.5 h-3.5 text-rose-700" />
                  <span>Bạn chọn</span>
                </span>
              )}

              {!isReviewMode && (
                <div className="flex items-center shrink-0 ml-2">
                  <div
                    className={`w-4 h-4 rounded-full border flex items-center justify-center shrink-0 transition-colors ${
                      isSelected
                        ? 'border-blue-600 bg-blue-600'
                        : 'border-slate-300 bg-white group-hover:border-slate-400'
                    }`}
                  >
                    {isSelected && <div className="w-1.5 h-1.5 rounded-full bg-white" />}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Review Mode: Detailed Explanation and Audio Transcript (Notion-style Callouts) */}
      {isReviewMode && (explanation || transcript) && (
        <div className="mt-5 space-y-3">
          {explanation && (
            <div className="p-4 rounded-r-lg bg-slate-50 border border-slate-200 border-l-[3px] border-l-blue-600">
              <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-900 mb-1.5">
                <Lightbulb className="w-4 h-4 text-blue-600" />
                <span className="font-serif">Giải thích chi tiết câu {currentQuestion}:</span>
              </div>
              <p className="text-xs sm:text-sm text-slate-700 leading-relaxed whitespace-pre-wrap font-sans max-w-prose">
                {explanation}
              </p>
            </div>
          )}

          {transcript && showTranscript && (
            <div className="p-4 rounded-r-lg bg-slate-50 border border-slate-200 border-l-[3px] border-l-amber-500">
              <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-900 mb-1.5">
                <Headphones className="w-4 h-4 text-amber-600" />
                <span className="font-serif">Lời thoại Audio (Transcript):</span>
              </div>
              <p className="text-xs font-mono text-slate-800 leading-relaxed whitespace-pre-wrap bg-white p-3 rounded border border-slate-200 max-w-prose">
                {transcript}
              </p>
            </div>
          )}
        </div>
      )}

      {/* Bottom Action Row */}
      <div className="flex flex-wrap items-center justify-between gap-3 pt-4 mt-6 border-t border-slate-100">
        <div className="flex items-center gap-2">
          {isReviewMode && transcript ? (
            <button
              type="button"
              onClick={() => setShowTranscript((prev) => !prev)}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs sm:text-sm font-medium transition-colors border border-slate-200 cursor-pointer"
            >
              <Headphones className="w-3.5 h-3.5 text-slate-600" />
              <span>{showTranscript ? 'Ẩn lời thoại Audio' : 'Xem lời thoại Audio'}</span>
            </button>
          ) : (
            <button
              type="button"
              disabled
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-50 text-slate-400 text-xs sm:text-sm font-medium border border-slate-200/50 cursor-not-allowed"
            >
              <Lock className="w-3.5 h-3.5" />
              <span>Lời thoại Audio (Khóa khi thi)</span>
            </button>
          )}
        </div>

        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={onPrevQuestion}
            disabled={!canGoPrev}
            title="Câu trước (Phím ← hoặc PageUp)"
            className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs sm:text-sm font-medium border border-slate-200 transition-colors disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Câu trước</span>
            <kbd className="hidden sm:inline-block text-[10px] font-mono text-slate-500 bg-slate-200/80 px-1 py-0.5 rounded">
              ←
            </kbd>
          </button>
          <button
            type="button"
            onClick={onNextQuestion}
            disabled={!canGoNext}
            title="Câu tiếp theo (Phím → hoặc PageDown)"
            className="inline-flex items-center gap-1.5 px-4 py-1.5 rounded-lg bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs sm:text-sm font-medium transition-colors disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer shadow-xs"
          >
            <span>Câu tiếp theo</span>
            <kbd className="hidden sm:inline-block text-[10px] font-mono text-blue-100 bg-blue-700 px-1 py-0.5 rounded">
              →
            </kbd>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
