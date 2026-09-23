import Link from 'next/link';
import Image from 'next/image';
import UserAccountMenu from '@/components/account/UserAccountMenu';

interface ExamHeaderProps {
  timeRemaining: number;
  formatTime: (sec: number) => string;
  currentQuestion: number;
  isFlagged: boolean;
  onToggleFlag: () => void;
  isSubmitting: boolean;
  onSubmit: () => void;
  onExit: () => void;
  onResetExam?: () => void;
  answeredCount: number;
  totalQuestions: number;
  isPlaying?: boolean;
  onTogglePlay?: () => void;
  playbackSpeed?: string;
  onChangeSpeed?: (speed: string) => void;
}

export default function ExamHeader({
  timeRemaining,
  formatTime,
  currentQuestion: _currentQuestion,
  isFlagged,
  onToggleFlag,
  isSubmitting,
  onSubmit,
  onExit,
  onResetExam,
  answeredCount,
  totalQuestions,
  isPlaying: _isPlaying,
  onTogglePlay: _onTogglePlay,
  playbackSpeed: _playbackSpeed,
  onChangeSpeed: _onChangeSpeed,
}: ExamHeaderProps) {
  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-surface border-b border-border-subtle shadow-sm">
      <div className="h-14 px-4 sm:px-6 lg:px-8 max-w-[1440px] mx-auto flex items-center justify-between gap-2 sm:gap-space-md">
        {/* Logo & Subtitle */}
        <div className="flex items-center gap-2 sm:gap-space-md shrink-0">
          <Link className="flex items-center gap-2 group" href="/">
            <div className="w-7 h-7 flex items-center justify-center shrink-0">
              <Image
                src="/logo.png"
                alt="ToeicPro Logo"
                width={28}
                height={28}
                className="w-full h-full object-contain drop-shadow-xs"
                priority
              />
            </div>
            <span className="font-headline-sm text-headline-sm text-text-primary tracking-tight font-bold">
              ToeicPro
            </span>
          </Link>
          <div className="h-4 w-px bg-border-subtle hidden lg:block"></div>
          <div className="hidden lg:flex items-center gap-space-xs">
            <span className="bg-surface-container-low text-primary px-1.5 py-0.5 rounded text-[11px] font-semibold uppercase tracking-wider border border-border-subtle whitespace-nowrap">
              Phòng Thi Trực Tuyến
            </span>
          </div>
        </div>

        {/* Center Spacer */}
        <div className="flex-1 min-w-0" />

        {/* Right Toolbar: Timer, Flag, Submit */}
        <div className="flex items-center justify-end gap-1.5 sm:gap-2 md:gap-space-md shrink-0 flex-nowrap">
          <div className="flex items-center gap-1 sm:gap-space-xs text-text-primary bg-surface-subtle px-2 sm:px-2.5 py-1 rounded border border-border-subtle shrink-0">
            <span className="material-symbols-outlined text-[16px] sm:text-[18px] text-text-secondary">timer</span>
            <span className="font-numeric-timer text-xs sm:text-numeric-timer tabular-nums tracking-tight font-bold text-primary">
              {formatTime(timeRemaining)}
            </span>
          </div>

          {onResetExam && (
            <button
              className="flex items-center gap-1 px-2 sm:px-2.5 py-1 rounded border border-border-subtle bg-surface text-text-secondary hover:text-blue-600 hover:bg-blue-50/50 hover:border-blue-200 transition-colors text-xs sm:text-sm font-medium cursor-pointer shrink-0"
              type="button"
              onClick={onResetExam}
              title="Làm lại bài thi từ đầu"
            >
              <span className="material-symbols-outlined text-[16px] sm:text-[18px]">restart_alt</span>
              <span className="hidden md:inline">Làm lại</span>
            </button>
          )}

          <button
            className="flex items-center gap-1 px-2 sm:px-2.5 py-1 rounded border border-border-subtle bg-surface text-text-secondary hover:text-red-600 hover:bg-red-50/50 hover:border-red-200 transition-colors text-xs sm:text-sm font-medium cursor-pointer shrink-0"
            type="button"
            onClick={onExit}
            title="Thoát phòng thi"
          >
            <span className="material-symbols-outlined text-[16px] sm:text-[18px]">logout</span>
            <span className="hidden md:inline">Thoát</span>
          </button>

          <button
            className={`flex items-center gap-1 px-2 sm:px-2.5 py-1 rounded border transition-colors text-xs sm:text-sm font-medium cursor-pointer shrink-0 ${
              isFlagged
                ? 'text-orange-700 bg-orange-50 border-orange-300 shadow-2xs font-semibold'
                : 'bg-surface text-text-secondary border-border-subtle hover:text-orange-600 hover:bg-orange-50/50'
            }`}
            type="button"
            onClick={onToggleFlag}
            title={isFlagged ? 'Đã đặt cờ (Click để bỏ)' : 'Đặt cờ câu này'}
          >
            <span
              className={`material-symbols-outlined text-[16px] sm:text-[18px] ${isFlagged ? 'text-orange-500' : ''}`}
              style={{ fontVariationSettings: isFlagged ? "'FILL' 1" : "'FILL' 0" }}
            >
              flag
            </span>
            <span className="hidden md:inline">{isFlagged ? 'Đã đặt cờ' : 'Đặt cờ'}</span>
          </button>

          <button
            className="bg-primary text-on-primary hover:bg-primary-container font-label-md text-xs sm:text-label-md px-2.5 sm:px-4 py-1.5 rounded transition-colors shadow-sm font-semibold disabled:opacity-60 cursor-pointer shrink-0"
            disabled={isSubmitting}
            type="button"
            onClick={onSubmit}
          >
            {isSubmitting ? 'Đang nộp...' : 'Nộp bài'}
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
