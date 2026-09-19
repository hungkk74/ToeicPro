interface ExamDirectionsProps {
  currentQuestion: number;
  playbackSpeed: string;
  onChangeSpeed: (spd: string) => void;
}

export default function ExamDirections({
  currentQuestion,
  playbackSpeed,
  onChangeSpeed,
}: ExamDirectionsProps) {
  return (
    <>
      {/* Part 1 Header & Dedicated Audio Track */}
      <div className="bg-surface rounded-lg p-space-md shadow-sm border border-border-subtle">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-space-md pb-space-sm">
          <div>
            <div className="flex items-center gap-space-xs text-primary font-label-sm text-label-sm uppercase tracking-wide font-semibold">
              <span className="material-symbols-outlined text-[18px]">headphones</span>
              <span>BÀI THI NGHE — Part 1: Mô tả Tranh</span>
            </div>
            <h1 className="font-headline-sm text-headline-sm text-text-primary mt-0.5 font-bold">
              Hướng dẫn &amp; Câu hỏi {currentQuestion} / 6
            </h1>
          </div>
          <div className="flex items-center gap-space-xs bg-status-success-bg text-status-success px-2.5 py-1 rounded text-caption font-medium">
            <span className="w-2 h-2 rounded-full bg-status-success animate-pulse"></span>
            <span>Kênh Audio Trực Tuyến Đang Hoạt Động</span>
          </div>
        </div>

        {/* Embedded Track Player Card */}
        <div className="mt-space-sm bg-surface-subtle p-space-md rounded-lg flex flex-col gap-space-xs border border-border-subtle">
          <div className="flex items-center justify-between font-caption text-caption text-text-secondary">
            <div className="flex items-center gap-2">
              <span className="material-symbols-outlined text-[16px] text-primary">graphic_eq</span>
              <span className="font-medium text-text-primary">Track 01_Part1_HuongDan.mp3</span>
              <span className="text-text-muted">• Âm thanh Chất lượng Cao</span>
            </div>
            <span className="tabular-nums font-numeric-metric text-text-secondary">00:42 / 03:15</span>
          </div>
          <div className="relative w-full h-1.5 bg-border-strong rounded-full overflow-hidden mt-1 cursor-pointer">
            <div className="absolute left-0 top-0 bottom-0 w-[21.5%] bg-primary rounded-full"></div>
          </div>
          <div className="flex items-center justify-between pt-1">
            <div className="flex items-center gap-space-sm">
              <button
                className="p-1 rounded hover:bg-surface text-text-secondary hover:text-text-primary transition-colors"
                type="button"
                title="Lùi lại 5 giây"
              >
                <span className="material-symbols-outlined text-[18px]">replay_5</span>
              </button>
              <button
                className="p-1 rounded hover:bg-surface text-primary transition-colors"
                type="button"
                title="Tạm dừng / Phát tiếp"
              >
                <span className="material-symbols-outlined text-[20px]">pause_circle</span>
              </button>
              <button
                className="p-1 rounded hover:bg-surface text-text-secondary hover:text-text-primary transition-colors"
                type="button"
                title="Tua tới 5 giây"
              >
                <span className="material-symbols-outlined text-[18px]">forward_5</span>
              </button>
              <span className="font-caption text-caption text-text-muted ml-1">Tốc độ:</span>
              <div className="inline-flex bg-surface rounded p-0.5 shadow-sm border border-border-subtle">
                {['0.8x', '1.0x', '1.2x'].map((spd) => (
                  <button
                    key={spd}
                    className={`px-2 py-0.5 rounded text-[11px] ${
                      playbackSpeed === spd
                        ? 'font-semibold bg-primary text-on-primary'
                        : 'font-medium text-text-secondary'
                    }`}
                    type="button"
                    onClick={() => onChangeSpeed(spd)}
                  >
                    {spd}
                  </button>
                ))}
              </div>
            </div>
            <div className="text-text-muted font-caption text-caption">Băng thông Audio Tối ưu: 0đ</div>
          </div>
        </div>
      </div>

      {/* Standard Directions Card */}
      <div className="bg-surface rounded-lg p-space-lg shadow-sm border border-border-subtle">
        <div className="flex items-center gap-space-xs text-text-primary font-headline-sm text-headline-sm mb-space-xs font-semibold">
          <span className="material-symbols-outlined text-primary text-[20px]">info</span>
          <span>Hướng dẫn làm bài cho Câu hỏi 1–6</span>
        </div>
        <p className="font-body-reading text-body-reading text-text-body leading-[1.65]">
          Trong phần này, bạn sẽ nghe 4 câu mô tả về một bức tranh hiển thị trên màn hình. Khi nghe các câu này, bạn hãy chọn phương án mô tả chính xác nhất những gì bạn nhìn thấy trong bức tranh.
        </p>
      </div>
    </>
  );
}
