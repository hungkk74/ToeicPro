interface QuestionMatrixProps {
  currentQuestion: number;
  totalQuestions: number;
  answeredCount: number;
  flaggedCount: number;
  selectedAnswers: Record<number, string>;
  flaggedQuestions: Record<number, boolean>;
  onSelectQuestion: (qNum: number) => void;
}

export default function QuestionMatrix({
  currentQuestion,
  totalQuestions,
  answeredCount,
  flaggedCount,
  selectedAnswers,
  flaggedQuestions,
  onSelectQuestion,
}: QuestionMatrixProps) {
  return (
    <aside className="w-full lg:w-[35%] lg:sticky lg:top-20 flex flex-col gap-space-md">
      {/* Progress Summary Card */}
      <div className="bg-surface rounded-lg p-space-md shadow-sm border border-border-subtle">
        <div className="flex items-center justify-between pb-space-xs">
          <span className="font-headline-sm text-headline-sm text-text-primary font-bold">Tiến độ làm bài</span>
          <span className="font-numeric-metric text-numeric-metric text-primary tabular-nums font-semibold">
            {answeredCount} / {totalQuestions} câu đã làm ({Math.round((answeredCount / totalQuestions) * 100)}%)
          </span>
        </div>
        <div className="w-full h-1.5 bg-border-strong rounded-full overflow-hidden mt-1 mb-space-md">
          <div
            className="h-full bg-primary rounded-full transition-all duration-300"
            style={{ width: `${(answeredCount / totalQuestions) * 100}%` }}
          ></div>
        </div>

        <div className="grid grid-cols-3 gap-space-xs">
          <div className="bg-primary/10 rounded p-2 text-center border border-primary/20">
            <span className="block font-numeric-metric text-numeric-metric text-primary tabular-nums font-bold">
              {answeredCount}
            </span>
            <span className="font-caption text-caption text-text-secondary">Đã làm</span>
          </div>
          <div className="bg-status-flag-bg rounded p-2 text-center border border-amber-200">
            <div className="inline-flex items-center gap-1 justify-center">
              <span className="w-1.5 h-1.5 rounded-full bg-status-flag"></span>
              <span className="font-numeric-metric text-numeric-metric text-status-flag tabular-nums font-bold">
                {flaggedCount}
              </span>
            </div>
            <span className="block font-caption text-caption text-text-secondary">Đã đặt cờ</span>
          </div>
          <div className="bg-surface-subtle rounded p-2 text-center border border-border-subtle">
            <span className="block font-numeric-metric text-numeric-metric text-text-secondary tabular-nums font-bold">
              {totalQuestions - answeredCount}
            </span>
            <span className="font-caption text-caption text-text-secondary">Chưa làm</span>
          </div>
        </div>
      </div>

      {/* Complete Question Matrix Grid */}
      <div className="bg-surface rounded-lg p-space-md shadow-sm border border-border-subtle max-h-[580px] overflow-y-auto">
        {/* Part 1: Photographs (1-6) */}
        <div className="mb-space-md">
          <div className="flex items-center justify-between pb-2">
            <div className="flex items-center gap-2">
              <span className="w-1.5 h-3.5 bg-primary rounded-full"></span>
              <span className="font-label-md text-label-md text-text-primary font-semibold">
                Part 1: Mô tả Tranh (1–6)
              </span>
            </div>
            <span className="font-caption text-caption text-text-secondary tabular-nums">
              {Object.keys(selectedAnswers).filter((k) => Number(k) <= 6).length}/6 đã làm
            </span>
          </div>
          <div className="grid grid-cols-6 gap-2 pt-1">
            {[1, 2, 3, 4, 5, 6].map((qNum) => {
              const isCurrent = currentQuestion === qNum;
              const isAnswered = Boolean(selectedAnswers[qNum]);
              const isFlagged = Boolean(flaggedQuestions[qNum]);

              return (
                <button
                  key={qNum}
                  className={`relative h-8 rounded flex items-center justify-center font-caption text-caption font-semibold transition-all ${
                    isCurrent
                      ? 'bg-primary text-on-primary ring-2 ring-primary ring-offset-2 shadow-sm'
                      : isAnswered
                      ? 'bg-primary-container text-on-primary'
                      : 'bg-surface-subtle text-text-secondary hover:bg-surface-container border border-border-subtle'
                  }`}
                  title={`Câu hỏi ${qNum}`}
                  type="button"
                  onClick={() => onSelectQuestion(qNum)}
                >
                  {qNum}
                  {isFlagged && (
                    <span className="absolute -top-1 -right-1 w-2.5 h-2.5 rounded-full bg-status-flag ring-1 ring-surface"></span>
                  )}
                </button>
              );
            })}
          </div>
        </div>

        {/* Part 2: Question-Response (7-31) */}
        <div className="mb-space-md pt-2 border-t border-border-subtle">
          <div className="flex items-center justify-between pb-2">
            <div className="flex items-center gap-2">
              <span className="w-1.5 h-3.5 bg-secondary rounded-full"></span>
              <span className="font-label-md text-label-md text-text-primary font-semibold">
                Part 2: Hỏi &amp; Đáp (7–31)
              </span>
            </div>
          </div>
          <div className="grid grid-cols-6 gap-2 pt-1">
            {Array.from({ length: 25 }, (_, i) => i + 7).map((qNum) => {
              const isCurrent = currentQuestion === qNum;
              const isAnswered = Boolean(selectedAnswers[qNum]);
              const isFlagged = Boolean(flaggedQuestions[qNum]);
              return (
                <button
                  key={qNum}
                  className={`relative h-8 rounded flex items-center justify-center font-caption text-caption font-medium transition-all ${
                    isCurrent
                      ? 'bg-primary text-on-primary ring-2 ring-primary ring-offset-2'
                      : isAnswered
                      ? 'bg-primary-container text-on-primary'
                      : 'bg-surface-subtle text-text-secondary hover:bg-surface-container border border-border-subtle'
                  }`}
                  title={`Câu hỏi ${qNum}`}
                  type="button"
                  onClick={() => onSelectQuestion(qNum)}
                >
                  {qNum}
                  {isFlagged && (
                    <span className="absolute -top-1 -right-1 w-2 h-2 rounded-full bg-status-flag"></span>
                  )}
                </button>
              );
            })}
          </div>
        </div>
      </div>
    </aside>
  );
}
