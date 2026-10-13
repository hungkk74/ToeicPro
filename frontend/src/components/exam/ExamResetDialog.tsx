'use client';

import { useEffect, useRef, useCallback } from 'react';

interface ExamResetDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirmReset: () => void;
  answeredCount: number;
}

export default function ExamResetDialog({
  isOpen,
  onClose,
  onConfirmReset,
  answeredCount,
}: ExamResetDialogProps) {
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
        aria-labelledby="reset-dialog-title"
      >
        <div className="space-y-2">
          <h2
            id="reset-dialog-title"
            className="font-serif text-lg font-semibold text-slate-950 tracking-tight"
          >
            Làm lại bài thi từ đầu?
          </h2>

          <p className="text-sm text-slate-600 leading-relaxed">
            Toàn bộ {answeredCount > 0 ? <strong className="text-slate-900 font-semibold tabular-nums">{answeredCount} câu đã trả lời</strong> : 'câu trả lời'} và đồng hồ đếm ngược sẽ được đặt lại về mốc ban đầu.
          </p>
        </div>

        <div className="flex flex-col gap-2 pt-4">
          <button
            type="button"
            onClick={onConfirmReset}
            className="w-full py-2.5 px-4 bg-rose-600 hover:bg-rose-700 active:bg-rose-800 text-white text-sm font-medium rounded-lg transition-colors cursor-pointer text-center shadow-xs"
          >
            Xác nhận làm lại từ đầu
          </button>

          <button
            type="button"
            onClick={onClose}
            className="w-full py-2.5 px-4 bg-white hover:bg-slate-50 active:bg-slate-100 text-slate-700 border border-slate-300 text-sm font-medium rounded-lg transition-colors cursor-pointer text-center"
          >
            Hủy thao tác
          </button>
        </div>
      </div>
    </div>
  );
}
