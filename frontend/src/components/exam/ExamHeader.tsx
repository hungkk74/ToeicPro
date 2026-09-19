import Link from 'next/link';
import UserAccountMenu from '@/components/account/UserAccountMenu';

interface ExamHeaderProps {
  timeRemaining: number;
  formatTime: (sec: number) => string;
  currentQuestion: number;
  isFlagged: boolean;
  onToggleFlag: () => void;
  isSubmitting: boolean;
  onSubmit: () => void;
  answeredCount: number;
  totalQuestions: number;
  isPlaying: boolean;
  onTogglePlay: () => void;
  playbackSpeed: string;
  onChangeSpeed: (speed: string) => void;
}

export default function ExamHeader({
  timeRemaining,
  formatTime,
  currentQuestion: _currentQuestion,
  isFlagged,
  onToggleFlag,
  isSubmitting,
  onSubmit,
  answeredCount,
  totalQuestions,
  isPlaying,
  onTogglePlay,
  playbackSpeed,
  onChangeSpeed,
}: ExamHeaderProps) {
  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-surface border-b border-border-subtle shadow-sm">
      <div className="h-14 px-gutter-desktop max-w-[1440px] mx-auto flex items-center justify-between gap-space-md">
        {/* Logo & Subtitle */}
        <div className="flex items-center gap-space-md">
          <Link className="flex items-center gap-2 group" href="/">
            <div className="w-7 h-7 rounded bg-primary text-on-primary flex items-center justify-center font-bold text-sm shadow-sm">
              T
            </div>
            <span className="font-headline-sm text-headline-sm text-text-primary tracking-tight font-bold">
              ToeicPro
            </span>
          </Link>
          <div className="h-4 w-px bg-border-subtle"></div>
          <div className="flex items-center gap-space-xs">
            <span className="bg-surface-container-low text-primary px-1.5 py-0.5 rounded text-[11px] font-semibold uppercase tracking-wider border border-border-subtle">
              Phòng Thi Trực Tuyến
            </span>
          </div>
        </div>

        {/* Central Audio Player Console */}
        <div className="flex-1 max-w-xl mx-auto hidden md:flex items-center gap-space-md bg-surface-subtle px-space-md py-1.5 rounded-lg border border-border-subtle">
          <button
            className="flex items-center justify-center text-text-primary hover:text-primary transition-colors"
            type="button"
            onClick={onTogglePlay}
            title={isPlaying ? 'Tạm dừng' : 'Phát âm thanh'}
          >
            <span className="material-symbols-outlined text-[22px]">
              {isPlaying ? 'pause' : 'play_arrow'}
            </span>
          </button>
          <div className="flex items-center gap-space-sm flex-1">
            <span className="font-caption text-caption text-text-secondary tabular-nums">00:42</span>
            <div className="relative flex-1 h-1.5 bg-border-strong rounded-full overflow-hidden cursor-pointer">
              <div className="absolute left-0 top-0 bottom-0 w-2/5 bg-primary rounded-full"></div>
            </div>
            <span className="font-caption text-caption text-text-secondary tabular-nums">01:15</span>
          </div>
          <div className="flex items-center gap-1 border-l border-border-subtle pl-space-sm">
            {['0.8x', '1.0x', '1.2x'].map((speed) => (
              <button
                key={speed}
                className={`px-1.5 py-0.5 rounded text-[11px] transition-colors ${
                  playbackSpeed === speed
                    ? 'font-semibold text-primary bg-surface border border-border-subtle shadow-sm'
                    : 'font-medium text-text-secondary hover:text-text-primary'
                }`}
                type="button"
                onClick={() => onChangeSpeed(speed)}
              >
                {speed}
              </button>
            ))}
          </div>
        </div>

        {/* Right Toolbar: Timer, Flag, Submit */}
        <div className="flex items-center justify-end gap-space-md min-w-[300px]">
          <div className="flex items-center gap-space-xs text-text-primary bg-surface-subtle px-2.5 py-1 rounded border border-border-subtle">
            <span className="material-symbols-outlined text-[18px] text-text-secondary">timer</span>
            <span className="font-numeric-timer text-numeric-timer tabular-nums tracking-tight font-bold text-primary">
              {formatTime(timeRemaining)}
            </span>
          </div>

          <div className="h-4 w-px bg-border-subtle"></div>

          <button
            className={`flex items-center gap-1 px-2.5 py-1 rounded border border-border-subtle transition-colors text-sm font-medium ${
              isFlagged
                ? 'text-status-flag bg-status-flag-bg border-amber-300'
                : 'bg-surface text-text-secondary hover:text-status-flag hover:bg-status-flag-bg'
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
            <span>{isFlagged ? 'Đã đặt cờ' : 'Đặt cờ'}</span>
          </button>

          <button
            className="bg-primary text-on-primary hover:bg-primary-container font-label-md text-label-md px-4 py-1.5 rounded transition-colors shadow-sm font-semibold disabled:opacity-60"
            disabled={isSubmitting}
            type="button"
            onClick={onSubmit}
          >
            {isSubmitting ? 'Đang chấm điểm...' : 'Nộp bài thi'}
          </button>

          <UserAccountMenu compact />
        </div>
      </div>

      {/* Global Progress Track line */}
      <div className="w-full h-[2px] bg-border-subtle relative">
        <div
          className="absolute left-0 top-0 bottom-0 bg-primary transition-all duration-300"
          style={{ width: `${(answeredCount / totalQuestions) * 100}%` }}
        ></div>
      </div>
    </header>
  );
}
