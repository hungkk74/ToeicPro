'use client';

import { useEffect, useRef, useCallback } from 'react';

interface ExamExitDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onSaveAndExit: () => void;
  onExitWithoutSaving: () => void;
  answeredCount: number;
  totalQuestions: number;
  timeRemaining: number;
  formatTime: (sec: number) => string;
}

export default function ExamExitDialog({
  isOpen,
  onClose,
  onSaveAndExit,
  onExitWithoutSaving,
  answeredCount,
  totalQuestions,
  timeRemaining,
  formatTime,
}: ExamExitDialogProps) {
  const dialogRef = useRef<HTMLDivElement>(null);

  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    },
    [onClose]
  );

  useEffect(() => {
    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown);
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = '';
    };
  }, [isOpen, handleKeyDown]);

  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 z-[9999] flex items-center justify-center p-4"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="absolute inset-0 bg-slate-950/45 backdrop-blur-[1px]" />

      <div
        ref={dialogRef}
        className="relative bg-white rounded-xl shadow-xl w-full max-w-[360px] mx-auto overflow-hidden border border-slate-200/90 animate-in fade-in zoom-in-95 duration-150 font-sans"
        role="dialog"
        aria-modal="true"
        aria-labelledby="exit-dialog-title"
      >
        {/* Header & Status */}
        <div className="p-6 pb-3 text-center space-y-2">
          <h2 id="exit-dialog-title" className="font-serif text-lg font-semibold text-slate-950 tracking-tight">
            Tạm dừng bài thi?
          </h2>

          <p className="text-xs text-slate-500 leading-relaxed max-w-xs mx-auto">
            Hệ thống tự động lưu bài làm của bạn trong 24 giờ để có thể tiếp tục bất cứ lúc nào.
          </p>

          {/* Compact Stats */}
          <div className="pt-2">
            <div className="inline-flex items-center gap-3 text-xs text-slate-600 bg-slate-50/90 py-2 px-3.5 rounded-lg border border-slate-200/80">
              <span>
                Đã làm: <strong className="text-slate-950 font-semibold tabular-nums">{answeredCount}/{totalQuestions}</strong>
              </span>
              <span className="w-px h-3 bg-slate-200" />
              <span>
                Thời gian: <strong className="text-slate-950 font-semibold tabular-nums">{formatTime(timeRemaining)}</strong>
              </span>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="p-6 pt-2 flex flex-col gap-2">
          {/* Primary CTA: Lưu & Thoát */}
          <button
            type="button"
            onClick={onSaveAndExit}
            className="w-full h-10 px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-medium rounded-lg transition-colors cursor-pointer shadow-xs"
          >
            Lưu tiến trình &amp; Thoát
          </button>

          {/* Secondary CTA: Tiếp tục làm bài */}
          <button
            type="button"
            onClick={onClose}
            className="w-full h-10 px-4 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-medium rounded-lg transition-colors cursor-pointer"
          >
            Tiếp tục làm bài
          </button>

          {/* Tertiary: Thoát không lưu */}
          <button
            type="button"
            onClick={onExitWithoutSaving}
            className="w-full py-1.5 text-slate-400 hover:text-rose-600 text-[11px] font-medium transition-colors cursor-pointer text-center mt-1"
          >
            Thoát không lưu (hủy kết quả)
          </button>
        </div>
      </div>
    </div>
  );
}
