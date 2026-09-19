interface QuestionCardProps {
  currentQuestion: number;
  isFlagged: boolean;
  onToggleFlag: () => void;
  selectedAnswer?: string;
  onSelectAnswer: (ans: string) => void;
  onPrevQuestion: () => void;
  onNextQuestion: () => void;
  canGoPrev: boolean;
  canGoNext: boolean;
}

const SAMPLE_OPTIONS = [
  { key: 'A', text: 'One of the workers is adjusting a safety helmet.' },
  { key: 'B', text: 'Boxes are being stacked with a forklift machine.' },
  { key: 'C', text: 'The supervisors are examining documents on a clipboard.' },
  { key: 'D', text: 'Pallets are being loaded into the back of a delivery truck.' },
];

export default function QuestionCard({
  currentQuestion,
  isFlagged,
  onToggleFlag,
  selectedAnswer,
  onSelectAnswer,
  onPrevQuestion,
  onNextQuestion,
  canGoPrev,
  canGoNext: _canGoNext,
}: QuestionCardProps) {
  return (
    <div className="bg-surface rounded-lg p-space-lg shadow-sm border border-border-subtle">
      {/* Question Header Meta */}
      <div className="flex items-center justify-between pb-space-md">
        <div className="flex items-center gap-space-md">
          <span className="inline-flex items-center justify-center w-8 h-8 rounded bg-primary text-on-primary font-headline-sm text-headline-sm font-bold shadow-sm">
            {currentQuestion}
          </span>
          <div>
            <h2 className="font-headline-sm text-headline-sm text-text-primary tracking-tight font-bold">
              Câu hỏi {currentQuestion} / 200
            </h2>
            <span className="font-caption text-caption text-text-secondary">
              Phần thi: Nghe • Part 1 • Mã câu hỏi: L01-00{currentQuestion}
            </span>
          </div>
        </div>
        <div className="flex items-center gap-space-sm">
          <span className="bg-surface-subtle text-text-secondary px-2.5 py-1 rounded font-caption text-caption border border-border-subtle">
            1 Điểm
          </span>
          <button
            className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded transition-colors font-label-sm text-label-sm border ${
              isFlagged
                ? 'text-status-flag bg-status-flag-bg border-amber-300 font-semibold'
                : 'bg-surface-subtle text-text-secondary border-border-subtle hover:bg-surface'
            }`}
            type="button"
            onClick={onToggleFlag}
          >
            <span
              className="material-symbols-outlined text-[18px]"
              style={{ fontVariationSettings: isFlagged ? "'FILL' 1" : "'FILL' 0" }}
            >
              flag
            </span>
            <span>{isFlagged ? 'Đã đặt cờ' : 'Đặt cờ câu này'}</span>
          </button>
        </div>
      </div>

      {/* Stimulus Photograph */}
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
        <p className="font-caption text-caption text-text-muted mt-2 text-right">
          Nhấn vào ảnh để phóng to (Có hỗ trợ thu phóng 1.5x)
        </p>
      </div>

      {/* Multiple Choice Options (A, B, C, D) */}
      <div className="flex flex-col gap-space-sm mt-space-lg">
        {SAMPLE_OPTIONS.map((opt) => {
          const isSelected = selectedAnswer === opt.key;
          return (
            <label
              key={opt.key}
              className={`group flex items-center justify-between p-space-md rounded cursor-pointer transition-all border ${
                isSelected
                  ? 'bg-surface-container-low border-primary shadow-sm'
                  : 'bg-surface border-border-subtle hover:bg-surface-subtle'
              }`}
              onClick={() => onSelectAnswer(opt.key)}
            >
              <div className="flex items-center gap-space-md flex-1">
                <div
                  className={`w-9 h-9 min-w-[36px] rounded flex items-center justify-center font-headline-sm text-headline-sm transition-colors font-bold ${
                    isSelected
                      ? 'bg-primary text-on-primary'
                      : 'bg-surface-subtle text-text-body group-hover:bg-primary/10 group-hover:text-primary'
                  }`}
                >
                  {opt.key}
                </div>
                <span
                  className={`font-body-reading text-body-reading ${
                    isSelected ? 'font-semibold text-text-primary' : 'text-text-body'
                  }`}
                >
                  {opt.text}
                </span>
              </div>
              <input
                checked={isSelected}
                onChange={() => onSelectAnswer(opt.key)}
                className="w-4 h-4 text-primary focus:ring-0"
                name={`question-${currentQuestion}`}
                type="radio"
                value={opt.key}
              />
            </label>
          );
        })}
      </div>

      {/* Bottom Interaction Action Row */}
      <div className="flex flex-wrap items-center justify-between gap-space-sm pt-space-lg mt-space-lg border-t border-border-subtle">
        <div className="flex items-center gap-space-sm">
          <button
            className="inline-flex items-center gap-1.5 px-3 py-2 rounded bg-surface hover:bg-surface-subtle text-text-secondary hover:text-text-primary font-label-md text-label-md transition-colors shadow-sm border border-border-subtle"
            type="button"
          >
            <span className="material-symbols-outlined text-[18px]">lock</span>
            <span>Lời thoại Audio (Khóa khi thi)</span>
          </button>
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
            className="inline-flex items-center gap-2 px-5 py-2 rounded bg-primary text-on-primary font-label-md text-label-md shadow-sm hover:bg-primary-container transition-colors font-semibold"
            type="button"
            onClick={onNextQuestion}
          >
            <span>Câu tiếp theo</span>
            <span className="bg-white/20 px-1.5 py-0.5 rounded text-[11px] font-mono tracking-tight">⌘+Enter</span>
            <span className="material-symbols-outlined text-[18px]">arrow_forward</span>
          </button>
        </div>
      </div>
    </div>
  );
}
