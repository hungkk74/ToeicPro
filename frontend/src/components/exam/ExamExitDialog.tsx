'use client';

import { useEffect, useRef, useCallback } from 'react';

interface ExamExitDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  answeredCount: number;
  totalQuestions: number;
  timeRemaining: number;
  formatTime: (sec: number) => string;
}

export default function ExamExitDialog({
  isOpen,
  onClose,
  onConfirm,
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
    }
    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = '';
    };
  }, [isOpen, handleKeyDown]);

  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 z-[9999] flex items-center justify-center"
      onClick={(e) => { if (e.target === e.currentTarget) onClose(); }}
    >
      <div className="absolute inset-0 bg-black/40" />

      <div
        ref={dialogRef}
        className="relative bg-white rounded-lg shadow-lg w-full max-w-sm mx-4 overflow-hidden"
        role="dialog"
        aria-modal="true"
      >
        {/* Content */}
        <div className="px-6 pt-6 pb-4">
          <h2 className="text-base font-bold text-neutral-900">Thoát phòng thi?</h2>
          <p className="text-sm text-neutral-500 mt-2 leading-relaxed">
            Bài làm sẽ được lưu lại. Bạn có thể quay lại tiếp tục bất cứ lúc nào.
          </p>

          {/* Compact stats */}
          <div className="mt-4 flex items-center gap-4 text-xs text-neutral-500">
            <span>{answeredCount}/{totalQuestions} đã trả lời</span>
            <span className="w-px h-3 bg-neutral-200" />
            <span>Còn {formatTime(timeRemaining)}</span>
          </div>
        </div>

        {/* Actions */}
        <div className="flex border-t border-neutral-100">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 py-3 text-sm font-medium text-neutral-600 hover:bg-neutral-50 transition-colors cursor-pointer"
          >
            Tiếp tục thi
          </button>
          <div className="w-px bg-neutral-100" />
          <button
            type="button"
            onClick={onConfirm}
            className="flex-1 py-3 text-sm font-semibold text-neutral-900 hover:bg-neutral-50 transition-colors cursor-pointer"
          >
            Lưu & Thoát
          </button>
        </div>
      </div>
    </div>
  );
}
