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
      className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-slate-900/50"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div
        ref={dialogRef}
        className="relative bg-white rounded-xl shadow-xl w-full max-w-sm mx-auto p-6 border border-slate-200 animate-in fade-in zoom-in-95 duration-150 font-sans"
        role="dialog"
        aria-modal="true"
        aria-labelledby="exit-dialog-title"
      >
        <div className="space-y-2">
          <h2
            id="exit-dialog-title"
            className="font-serif text-lg font-semibold text-slate-950 tracking-tight"
          >
            Rời khỏi phòng thi?
          </h2>

          <p className="text-sm text-slate-600 leading-relaxed">
            Hệ thống có thể lưu lại tiến trình bài thi để bạn tiếp tục sau. Nếu chọn thoát không lưu, các câu trả lời của lần thi này sẽ bị hủy.
          </p>

          <div className="flex items-center justify-between text-xs text-slate-600 border-y border-slate-100 my-4 py-2.5">
            <span>
              Đã làm: <strong className="text-slate-900 font-semibold tabular-nums">{answeredCount}/{totalQuestions}</strong> câu
            </span>
            <span>
              Thời gian còn: <strong className="text-slate-900 font-mono font-semibold tabular-nums">{formatTime(timeRemaining)}</strong>
            </span>
          </div>
        </div>

        <div className="flex flex-col gap-2 pt-1">
          <button
            type="button"
            onClick={onSaveAndExit}
            className="w-full py-2.5 px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-sm font-medium rounded-lg transition-colors cursor-pointer text-center shadow-xs"
          >
            Lưu tiến trình và thoát
          </button>

          <button
            type="button"
            onClick={onExitWithoutSaving}
            className="w-full py-2.5 px-4 bg-rose-50 hover:bg-rose-100 active:bg-rose-200 text-rose-700 border border-rose-200 text-sm font-medium rounded-lg transition-colors cursor-pointer text-center"
          >
            Thoát không lưu
          </button>

          <button
            type="button"
            onClick={onClose}
            className="w-full py-2.5 px-4 bg-white hover:bg-slate-50 active:bg-slate-100 text-slate-700 border border-slate-300 text-sm font-medium rounded-lg transition-colors cursor-pointer text-center"
          >
            Tiếp tục làm bài
          </button>
        </div>
      </div>
    </div>
  );
}
