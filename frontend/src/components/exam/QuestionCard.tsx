'use client';

import { useState } from 'react';

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
  audioUrl,
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
  const displayOptions = options && options.length > 0 ? options : DEFAULT_EMPTY_OPTIONS;
  const isReadingPart = partNumber >= 5;

  return (
    <div className="bg-surface rounded-lg p-space-lg shadow-sm border border-border-subtle">
      {/* Question Header Meta */}
      <div className="flex items-center justify-between pb-space-md border-b border-border-subtle">
        <div className="flex items-center gap-space-md">
          <span className="inline-flex items-center justify-center w-8 h-8 rounded bg-primary text-on-primary font-headline-sm text-headline-sm font-bold shadow-sm">
            {currentQuestion}
          </span>
          <div>
            <h2 className="font-headline-sm text-headline-sm text-text-primary tracking-tight font-bold">
              Câu hỏi {currentQuestion} / {totalQuestions}
            </h2>
            <span className="font-caption text-caption text-text-secondary">
              {partName ? partName : `Phần thi: ${isReadingPart ? 'Đọc' : 'Nghe'} • Part ${partNumber}`} • Mã câu hỏi: Q-{currentQuestion}
            </span>
          </div>
        </div>
        <div className="flex items-center gap-space-sm">
          <span className="bg-surface-subtle text-text-secondary px-2.5 py-1 rounded font-caption text-caption border border-border-subtle">
            1 Điểm
          </span>
          <button
            className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded transition-colors font-label-sm text-label-sm border cursor-pointer ${
              isFlagged
                ? 'text-orange-700 bg-orange-50 border-orange-300 font-semibold shadow-2xs hover:bg-orange-100'
                : 'bg-surface-subtle text-text-secondary border-border-subtle hover:bg-surface hover:text-orange-600'
            }`}
            type="button"
            onClick={onToggleFlag}
          >
            <span
              className={`material-symbols-outlined text-[18px] ${isFlagged ? 'text-orange-500' : ''}`}
              style={{ fontVariationSettings: isFlagged ? "'FILL' 1" : "'FILL' 0" }}
            >
              flag
            </span>
            <span>{isFlagged ? 'Đã đặt cờ' : 'Đặt cờ câu này'}</span>
          </button>
        </div>
      </div>

      {/* Reading Passage Stimulus (Part 6 & Part 7) */}
      {passageText && (
        <div className="my-space-md p-space-md bg-slate-50/80 border border-border-subtle rounded-lg shadow-2xs">
          <div className="flex items-center gap-2 text-primary font-label-sm text-label-sm font-semibold mb-2">
            <span className="material-symbols-outlined text-[18px]">menu_book</span>
            <span>Đoạn văn Đọc hiểu (Reading Passage)</span>
          </div>
          <div className="font-serif text-[15px] leading-relaxed text-text-primary whitespace-pre-line p-3 bg-white rounded border border-border-subtle/80">
            {passageText}
          </div>
        </div>
      )}

      {/* Dedicated Image Stimulus */}
      {imageUrl ? (
        <div className="my-space-md">
          <div className="relative bg-surface-subtle rounded-md overflow-hidden shadow-sm border border-border-subtle">
            <img
              src={imageUrl}
              alt={`Hình ảnh câu hỏi ${currentQuestion}`}
              className="w-full max-h-96 object-contain bg-white"
            />
          </div>
        </div>
      ) : !passageText && partNumber === 1 ? (
        /* Stimulus Photograph Fallback for Part 1 Mockup */
        <div className="my-space-md">
          <div className="relative bg-surface-subtle rounded-md overflow-hidden shadow-sm border border-border-subtle">
            <div className="w-full h-80 bg-slate-200 flex flex-col items-center justify-center text-slate-500 relative">
              <span className="material-symbols-outlined text-[56px] text-slate-400 mb-2">image</span>
              <p className="font-semibold text-sm">ETS Logistics Fulfillment Center Inspection</p>
              <p className="text-xs text-slate-400 mt-1">
                Hai giám sát viên đang kiểm tra các kiện hàng cùng bảng kẹp hồ sơ và máy quét
              </p>
              <div className="absolute bottom-3 right-3 bg-surface/90 backdrop-blur-sm px-2.5 py-1 rounded font-caption text-caption text-text-primary shadow-sm border border-border-subtle">
                Ảnh mô tả Câu hỏi {currentQuestion} / 6
              </div>
            </div>
          </div>
        </div>
      ) : null}


      {/* Question Content / Stem */}
      {content && (
        <div className="my-space-md p-space-md bg-blue-50/40 rounded-lg border border-blue-100 text-text-primary font-body-reading text-[16px] font-medium leading-relaxed">
          {content}
        </div>
      )}

      {/* Multiple Choice Options (A, B, C, D) */}
      <div className="flex flex-col gap-space-sm mt-space-md">
        {displayOptions.map((opt) => {
          const isSelected = selectedAnswer === opt.key;
          const isCorrectChoice = isReviewMode && correctOption === opt.key;
          const isUserWrongChoice = isReviewMode && isSelected && !isCorrectChoice;

          let optionContainerClasses = 'bg-surface border-border-subtle hover:bg-surface-subtle';
          let badgeClasses = 'bg-surface-subtle text-text-body';

          if (isReviewMode) {
            if (isCorrectChoice) {
              optionContainerClasses = 'bg-emerald-50 border-emerald-500 shadow-xs ring-1 ring-emerald-500';
              badgeClasses = 'bg-emerald-600 text-white font-bold';
            } else if (isUserWrongChoice) {
              optionContainerClasses = 'bg-rose-50 border-rose-400 shadow-xs ring-1 ring-rose-400';
              badgeClasses = 'bg-rose-600 text-white font-bold';
            } else {
              optionContainerClasses = 'bg-slate-50/70 border-slate-200 opacity-80';
              badgeClasses = 'bg-slate-100 text-slate-600';
            }
          } else if (isSelected) {
            optionContainerClasses = 'bg-surface-container-low border-primary shadow-sm';
            badgeClasses = 'bg-primary text-on-primary font-bold';
          }

          return (
            <label
              key={opt.key}
              className={`group flex items-center justify-between p-space-md rounded ${
                isReviewMode ? 'cursor-default' : 'cursor-pointer'
              } transition-all border ${optionContainerClasses}`}
              onClick={() => {
                if (!isReviewMode) onSelectAnswer(opt.key);
              }}
            >
              <div className="flex items-center gap-space-md flex-1">
                <div
                  className={`w-9 h-9 min-w-[36px] rounded flex items-center justify-center font-headline-sm text-headline-sm transition-colors font-bold ${badgeClasses}`}
                >
                  {opt.key}
                </div>
                <span
                  className={`font-body-reading text-body-reading ${
                    isSelected || isCorrectChoice ? 'font-semibold text-text-primary' : 'text-text-body'
                  }`}
                >
                  {opt.text || `Đáp án (${opt.key})`}
                </span>
              </div>

              {/* Status chips in Review Mode */}
              {isReviewMode && isCorrectChoice && (
                <span className="text-xs font-bold text-emerald-800 bg-emerald-100 px-2.5 py-1 rounded-full shrink-0 ml-2">
                  ✓ Đáp án đúng
                </span>
              )}
              {isReviewMode && isUserWrongChoice && (
                <span className="text-xs font-bold text-rose-800 bg-rose-100 px-2.5 py-1 rounded-full shrink-0 ml-2">
                  ✗ Bạn chọn
                </span>
              )}

              {!isReviewMode && (
                <input
                  checked={isSelected}
                  onChange={() => onSelectAnswer(opt.key)}
                  className="w-4 h-4 text-primary focus:ring-0"
                  name={`question-${currentQuestion}`}
                  type="radio"
                  value={opt.key}
                />
              )}
            </label>
          );
        })}
      </div>

      {/* Review Mode: Detailed Explanation and Audio Transcript */}
      {isReviewMode && (explanation || transcript) && (
        <div className="mt-4 p-4 rounded-xl bg-blue-50/60 border border-blue-200/80 space-y-3 animate-in fade-in duration-200">
          {explanation && (
            <div>
              <div className="flex items-center gap-1.5 text-xs font-bold text-blue-900 mb-1">
                <span>💡 Giải thích chi tiết câu {currentQuestion}:</span>
              </div>
              <p className="text-xs sm:text-sm text-blue-950 leading-relaxed whitespace-pre-wrap font-medium">
                {explanation}
              </p>
            </div>
          )}

          {transcript && showTranscript && (
            <div className="pt-2 border-t border-blue-200/60">
              <div className="flex items-center gap-1.5 text-xs font-bold text-blue-900 mb-1">
                <span>🎙️ Lời thoại Audio (Transcript):</span>
              </div>
              <p className="text-xs font-mono text-blue-900 leading-relaxed whitespace-pre-wrap bg-white/70 p-2.5 rounded border border-blue-200/50">
                {transcript}
              </p>
            </div>
          )}
        </div>
      )}

      {/* Bottom Interaction Action Row */}
      <div className="flex flex-wrap items-center justify-between gap-space-sm pt-space-lg mt-space-lg border-t border-border-subtle">
        <div className="flex items-center gap-space-sm">
          {isReviewMode && transcript ? (
            <button
              className="inline-flex items-center gap-1.5 px-3 py-2 rounded bg-blue-50 text-blue-700 hover:bg-blue-100 font-label-md text-label-md transition-colors shadow-sm border border-blue-200 cursor-pointer"
              type="button"
              onClick={() => setShowTranscript((prev) => !prev)}
            >
              <span className="material-symbols-outlined text-[18px]">graphic_eq</span>
              <span>{showTranscript ? 'Ẩn lời thoại Audio' : 'Xem lời thoại Audio'}</span>
            </button>
          ) : (
            <button
              className="inline-flex items-center gap-1.5 px-3 py-2 rounded bg-surface hover:bg-surface-subtle text-text-secondary hover:text-text-primary font-label-md text-label-md transition-colors shadow-sm border border-border-subtle opacity-70"
              type="button"
              disabled
            >
              <span className="material-symbols-outlined text-[18px]">lock</span>
              <span>Lời thoại Audio (Khóa khi thi)</span>
            </button>
          )}
          <button
            className="inline-flex items-center gap-1.5 px-3 py-2 rounded bg-surface hover:bg-surface-subtle text-text-secondary hover:text-text-primary font-label-md text-label-md transition-colors shadow-sm border border-border-subtle"
            type="button"
          >
            <span className="material-symbols-outlined text-[18px]">notes</span>
            <span>Bảng nháp</span>
          </button>
        </div>
        <div className="flex items-center gap-space-sm">
          <button
            className="inline-flex items-center gap-1.5 px-4 py-2 rounded bg-surface-subtle text-text-secondary hover:text-text-primary font-label-md text-label-md border border-border-subtle disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={!canGoPrev}
            type="button"
            onClick={onPrevQuestion}
          >
            <span className="material-symbols-outlined text-[18px]">arrow_back</span>
            <span>Câu trước</span>
          </button>
          <button
            className="inline-flex items-center gap-2 px-5 py-2 rounded bg-primary text-on-primary font-label-md text-label-md shadow-sm hover:bg-primary-container transition-colors font-semibold disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
            disabled={!canGoNext}
            type="button"
            onClick={onNextQuestion}
          >
            <span>Câu tiếp theo</span>
            <span className="material-symbols-outlined text-[18px]">arrow_forward</span>
          </button>
        </div>
      </div>
    </div>
  );
}
