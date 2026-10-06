export interface MatrixPartItem {
  partNumber: number;
  name: string;
  questions: number[];
}

interface QuestionMatrixProps {
  currentQuestion: number;
  totalQuestions: number;
  answeredCount: number;
  flaggedCount: number;
  selectedAnswers: Record<number, string>;
  flaggedQuestions: Record<number, boolean>;
  onSelectQuestion: (qNum: number) => void;
  parts?: MatrixPartItem[];
  isReviewMode?: boolean;
  reviewAnswers?: Record<number, { isCorrect: boolean; selectedOption?: string; correctOption: string }>;
  correctCount?: number;
  wrongCount?: number;
  skippedCount?: number;
}

const DEFAULT_PARTS: MatrixPartItem[] = [
  { partNumber: 1, name: 'Part 1: Mô tả Tranh (1–6)', questions: [1, 2, 3, 4, 5, 6] },
  { partNumber: 2, name: 'Part 2: Hỏi & Đáp (7–31)', questions: Array.from({ length: 25 }, (_, i) => i + 7) },
  { partNumber: 3, name: 'Part 3: Hội thoại (32–70)', questions: Array.from({ length: 39 }, (_, i) => i + 32) },
  { partNumber: 4, name: 'Part 4: Bài nói ngắn (71–100)', questions: Array.from({ length: 30 }, (_, i) => i + 71) },
  { partNumber: 5, name: 'Part 5: Điền câu (101–130)', questions: Array.from({ length: 30 }, (_, i) => i + 101) },
  { partNumber: 6, name: 'Part 6: Điền đoạn văn (131–146)', questions: Array.from({ length: 16 }, (_, i) => i + 131) },
  { partNumber: 7, name: 'Part 7: Đọc hiểu (147–200)', questions: Array.from({ length: 54 }, (_, i) => i + 147) },
];

export default function QuestionMatrix({
  currentQuestion,
  totalQuestions,
  answeredCount,
  flaggedCount,
  selectedAnswers,
  flaggedQuestions,
  onSelectQuestion,
  parts,
  isReviewMode = false,
  reviewAnswers,
  correctCount = 0,
  wrongCount = 0,
  skippedCount = 0,
}: QuestionMatrixProps) {
  const displayParts = parts && parts.length > 0 ? parts : DEFAULT_PARTS;

  return (
    <aside className="w-full lg:w-[340px] xl:w-[360px] shrink-0 lg:sticky lg:top-20 flex flex-col gap-4 font-sans">
      {/* Progress Summary Card */}
      <div className="bg-white rounded-xl p-4 border border-slate-200/90 shadow-xs">
        {isReviewMode ? (
          <>
            <div className="flex items-center justify-between pb-1 flex-wrap gap-1">
              <span className="font-serif text-sm font-semibold text-slate-900">
                Kết quả bài thi
              </span>
              <span className="text-xs font-semibold text-emerald-800 tabular-nums font-mono">
                Đúng {correctCount}/{totalQuestions} câu ({totalQuestions > 0 ? Math.round((correctCount / totalQuestions) * 100) : 0}%)
              </span>
            </div>
            <div className="w-full h-1.5 bg-slate-100 rounded-full overflow-hidden mt-1 mb-3 flex">
              <div
                className="bg-emerald-600 h-full transition-all duration-300"
                style={{ width: `${totalQuestions > 0 ? (correctCount / totalQuestions) * 100 : 0}%` }}
                title={`Đúng: ${correctCount} câu`}
              />
              <div
                className="bg-rose-600 h-full transition-all duration-300"
                style={{ width: `${totalQuestions > 0 ? (wrongCount / totalQuestions) * 100 : 0}%` }}
                title={`Sai: ${wrongCount} câu`}
              />
              <div
                className="bg-slate-300 h-full transition-all duration-300"
                style={{ width: `${totalQuestions > 0 ? (skippedCount / totalQuestions) * 100 : 0}%` }}
                title={`Chưa làm: ${skippedCount} câu`}
              />
            </div>

            <div className="grid grid-cols-3 gap-2 text-center text-xs">
              <div className="bg-emerald-50/80 border border-emerald-200/70 rounded-lg p-2">
                <span className="block font-serif font-bold text-emerald-900 text-sm tabular-nums">
                  {correctCount}
                </span>
                <span className="text-[11px] text-emerald-700 font-medium">Đúng</span>
              </div>
              <div className="bg-rose-50/80 border border-rose-200/70 rounded-lg p-2">
                <span className="block font-serif font-bold text-rose-900 text-sm tabular-nums">
                  {wrongCount}
                </span>
                <span className="text-[11px] text-rose-700 font-medium">Sai</span>
              </div>
              <div className="bg-slate-100 border border-slate-200 rounded-lg p-2">
                <span className="block font-serif font-bold text-slate-700 text-sm tabular-nums">
                  {skippedCount}
                </span>
                <span className="text-[11px] text-slate-500 font-medium">Chưa làm</span>
              </div>
            </div>
          </>
        ) : (
          <>
            <div className="flex items-center justify-between pb-1 flex-wrap gap-1">
              <span className="font-serif text-sm font-semibold text-slate-900">Tiến độ làm bài</span>
              <span className="text-xs font-semibold text-slate-700 tabular-nums font-mono">
                {answeredCount}/{totalQuestions} câu ({totalQuestions > 0 ? Math.round((answeredCount / totalQuestions) * 100) : 0}%)
              </span>
            </div>
            <div className="w-full h-1 bg-slate-100 rounded-full overflow-hidden mt-1 mb-3">
              <div
                className="h-full bg-blue-600 rounded-full transition-all duration-300"
                style={{ width: `${totalQuestions > 0 ? (answeredCount / totalQuestions) * 100 : 0}%` }}
              />
            </div>

            <div className="grid grid-cols-3 gap-2">
              <div className="bg-slate-100 rounded-lg p-2 text-center border border-slate-200">
                <span className="block text-sm font-serif font-bold text-slate-900 tabular-nums">
                  {answeredCount}
                </span>
                <span className="text-[11px] text-slate-600 font-medium">Đã làm</span>
              </div>
              <div className="bg-amber-50 rounded-lg p-2 text-center border border-amber-200">
                <div className="inline-flex items-center gap-1 justify-center">
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-600" />
                  <span className="text-sm font-serif font-bold text-amber-900 tabular-nums">
                    {flaggedCount}
                  </span>
                </div>
                <span className="block text-[11px] text-amber-700 font-medium">Đặt cờ</span>
              </div>
              <div className="bg-slate-50 rounded-lg p-2 text-center border border-slate-200/70">
                <span className="block text-sm font-serif font-bold text-slate-600 tabular-nums">
                  {Math.max(0, totalQuestions - answeredCount)}
                </span>
                <span className="text-[11px] text-slate-500 font-medium">Chưa làm</span>
              </div>
            </div>
          </>
        )}
      </div>

      {/* Question Matrix Grid */}
      <div className="bg-white rounded-xl p-4 border border-slate-200/90 shadow-xs max-h-[580px] overflow-y-auto">
        {displayParts.map((part, pIdx) => {
          const partAnsweredCount = part.questions.filter((q) => Boolean(selectedAnswers[q])).length;
          return (
            <div key={part.partNumber} className={`mb-3.5 ${pIdx > 0 ? 'pt-3 border-t border-slate-100' : ''}`}>
              <div className="flex items-center justify-between pb-1.5">
                <div className="flex items-center gap-1.5">
                  <span className="w-1 h-3 rounded-full bg-blue-600" />
                  <span className="text-xs font-semibold text-slate-900">
                    {part.name}
                  </span>
                </div>
                <span className="text-xs text-slate-500 tabular-nums font-mono">
                  {isReviewMode
                    ? `${part.questions.filter((q) => reviewAnswers?.[q]?.isCorrect).length}/${part.questions.length}`
                    : `${partAnsweredCount}/${part.questions.length}`}
                </span>
              </div>
              <div className="grid grid-cols-5 sm:grid-cols-6 gap-1 pt-1">
                {part.questions.map((qNum) => {
                  const isCurrent = currentQuestion === qNum;
                  const isAnswered = Boolean(selectedAnswers[qNum]);
                  const isFlagged = Boolean(flaggedQuestions[qNum]);
                  const revItem = reviewAnswers?.[qNum];

                  let cellClasses = '';

                  if (isReviewMode) {
                    if (revItem?.isCorrect) {
                      cellClasses = isCurrent
                        ? 'bg-emerald-700 text-white font-bold ring-2 ring-emerald-600 ring-offset-1'
                        : 'bg-emerald-600 text-white font-medium hover:bg-emerald-700';
                    } else if (revItem && revItem.selectedOption != null) {
                      cellClasses = isCurrent
                        ? 'bg-rose-700 text-white font-bold ring-2 ring-rose-600 ring-offset-1'
                        : 'bg-rose-600 text-white font-medium hover:bg-rose-700';
                    } else {
                      cellClasses = isCurrent
                        ? 'bg-slate-300 text-slate-900 font-bold ring-2 ring-slate-400 ring-offset-1'
                        : 'bg-slate-100 text-slate-500 hover:bg-slate-200 border border-slate-200/60';
                    }
                  } else {
                    if (isCurrent) {
                      cellClasses = isFlagged
                        ? 'bg-amber-600 text-white ring-2 ring-amber-500 ring-offset-1 font-bold'
                        : 'bg-blue-600 text-white ring-2 ring-blue-600 ring-offset-1 font-bold shadow-xs';
                    } else if (isFlagged) {
                      cellClasses = isAnswered
                        ? 'bg-amber-500 text-white font-medium'
                        : 'bg-amber-100 text-amber-900 border border-amber-300 font-medium hover:bg-amber-200';
                    } else if (isAnswered) {
                      cellClasses = 'bg-blue-600 text-white font-medium hover:bg-blue-700';
                    } else {
                      cellClasses = 'bg-slate-50 text-slate-700 hover:bg-slate-100 border border-slate-200/80';
                    }
                  }

                  return (
                    <button
                      key={qNum}
                      type="button"
                      className={`relative h-7 min-w-0 rounded flex items-center justify-center text-xs tabular-nums transition-colors cursor-pointer ${cellClasses}`}
                      title={`Câu hỏi ${qNum}${isReviewMode && revItem ? (revItem.isCorrect ? ' (Đúng)' : revItem.selectedOption ? ` (Sai - Bạn chọn ${revItem.selectedOption})` : ' (Chưa làm)') : ''}`}
                      onClick={() => onSelectQuestion(qNum)}
                    >
                      {qNum}
                      {!isReviewMode && isFlagged && (
                        <span className="absolute -top-0.5 -right-0.5 w-1.5 h-1.5 rounded-full bg-amber-600" />
                      )}
                    </button>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>
    </aside>
  );
}
