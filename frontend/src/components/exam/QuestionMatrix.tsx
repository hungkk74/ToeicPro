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
}: QuestionMatrixProps) {
  const displayParts = parts && parts.length > 0 ? parts : DEFAULT_PARTS;

  return (
    <aside className="w-full lg:w-[340px] xl:w-[380px] shrink-0 lg:sticky lg:top-20 flex flex-col gap-space-md">
      {/* Progress Summary Card */}
      <div className="bg-surface rounded-lg p-space-md shadow-sm border border-border-subtle">
        <div className="flex items-center justify-between pb-space-xs flex-wrap gap-1">
          <span className="font-headline-sm text-headline-sm text-text-primary font-bold">Tiến độ làm bài</span>
          <span className="font-numeric-metric text-xs sm:text-numeric-metric text-primary tabular-nums font-semibold">
            {answeredCount}/{totalQuestions} câu ({totalQuestions > 0 ? Math.round((answeredCount / totalQuestions) * 100) : 0}%)
          </span>
        </div>
        <div className="w-full h-1.5 bg-border-strong rounded-full overflow-hidden mt-1 mb-space-md">
          <div
            className="h-full bg-primary rounded-full transition-all duration-300"
            style={{ width: `${totalQuestions > 0 ? (answeredCount / totalQuestions) * 100 : 0}%` }}
          ></div>
        </div>

        <div className="grid grid-cols-3 gap-1.5 sm:gap-space-xs">
          <div className="bg-primary/10 rounded p-2 text-center border border-primary/20">
            <span className="block font-numeric-metric text-numeric-metric text-primary tabular-nums font-bold">
              {answeredCount}
            </span>
            <span className="font-caption text-caption text-text-secondary">Đã làm</span>
          </div>
          <div className="bg-orange-50 rounded p-2 text-center border border-orange-200">
            <div className="inline-flex items-center gap-1 justify-center">
              <span className="w-1.5 h-1.5 rounded-full bg-orange-500"></span>
              <span className="font-numeric-metric text-numeric-metric text-orange-600 tabular-nums font-bold">
                {flaggedCount}
              </span>
            </div>
            <span className="block font-caption text-caption text-text-secondary">Đã đặt cờ</span>
          </div>
          <div className="bg-surface-subtle rounded p-2 text-center border border-border-subtle">
            <span className="block font-numeric-metric text-numeric-metric text-text-secondary tabular-nums font-bold">
              {Math.max(0, totalQuestions - answeredCount)}
            </span>
            <span className="font-caption text-caption text-text-secondary">Chưa làm</span>
          </div>
        </div>
      </div>

      {/* Complete Question Matrix Grid */}
      <div className="bg-surface rounded-lg p-space-md shadow-sm border border-border-subtle max-h-[580px] overflow-y-auto">
        {displayParts.map((part, pIdx) => {
          const partAnsweredCount = part.questions.filter((q) => Boolean(selectedAnswers[q])).length;
          const isListening = part.partNumber <= 4;
          return (
            <div key={part.partNumber} className={`mb-space-md ${pIdx > 0 ? 'pt-3 border-t border-border-subtle' : ''}`}>
              <div className="flex items-center justify-between pb-2">
                <div className="flex items-center gap-2">
                  <span className={`w-1.5 h-3.5 rounded-full ${isListening ? 'bg-primary' : 'bg-emerald-600'}`}></span>
                  <span className="font-label-md text-label-md text-text-primary font-semibold">
                    {part.name}
                  </span>
                </div>
                <span className="font-caption text-caption text-text-secondary tabular-nums">
                  {partAnsweredCount}/{part.questions.length} đã làm
                </span>
              </div>
              <div className="grid grid-cols-5 sm:grid-cols-6 gap-1.5 sm:gap-2 pt-1">
                {part.questions.map((qNum) => {
                  const isCurrent = currentQuestion === qNum;
                  const isAnswered = Boolean(selectedAnswers[qNum]);
                  const isFlagged = Boolean(flaggedQuestions[qNum]);

                  let cellClasses = '';
                  if (isCurrent) {
                    cellClasses = isFlagged
                      ? 'bg-orange-600 text-white ring-2 ring-orange-400 ring-offset-2 shadow-md font-bold'
                      : 'bg-primary text-on-primary ring-2 ring-primary ring-offset-2 shadow-sm font-bold';
                  } else if (isFlagged) {
                    // Ô ĐÃ ĐẶT CỜ: Màu đơn sắc cam rực rỡ (Electric Orange) tương phản mạnh với theme xanh dương
                    cellClasses = isAnswered
                      ? 'bg-orange-500 text-white border border-orange-600 font-bold shadow-xs'
                      : 'bg-orange-100 text-orange-950 border-2 border-orange-400 font-bold hover:bg-orange-200';
                  } else if (isAnswered) {
                    cellClasses = 'bg-primary-container text-on-primary font-semibold';
                  } else {
                    cellClasses = 'bg-surface-subtle text-text-secondary hover:bg-surface-container border border-border-subtle';
                  }

                  return (
                    <button
                      key={qNum}
                      className={`relative h-8 min-w-0 rounded flex items-center justify-center font-caption text-xs tabular-nums transition-all ${cellClasses}`}
                      title={`Câu hỏi ${qNum}${isFlagged ? ' (Đã đặt cờ)' : ''}`}
                      type="button"
                      onClick={() => onSelectQuestion(qNum)}
                    >
                      {qNum}
                      {isFlagged && (
                        <span className="absolute -top-1 -right-1 w-2.5 h-2.5 rounded-full bg-orange-500 ring-1.5 ring-white"></span>
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
