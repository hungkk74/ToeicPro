'use client';

import { useEffect, useRef, useCallback } from 'react';
import { RotateCcw } from 'lucide-react';

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
      <div className="absolute inset-0 bg-slate-950/40 backdrop-blur-[2px]" />

      <div
        ref={dialogRef}
        className="relative bg-white rounded-xl shadow-xl w-full max-w-sm mx-auto overflow-hidden border border-slate-200 animate-in fade-in zoom-in-95 duration-150 font-sans"
        role="dialog"
        aria-modal="true"
      >
        {/* Content */}
        <div className="p-6 text-center space-y-3">
          <div className="w-11 h-11 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center mx-auto border border-blue-200/80">
            <RotateCcw className="w-5 h-5 stroke-[1.75]" />
          </div>

          <h2 className="font-serif text-base sm:text-lg font-semibold text-slate-950 tracking-tight">
            Làm lại bài thi từ đầu?
          </h2>

          <p className="text-xs text-slate-500 leading-relaxed max-w-xs mx-auto">
            Toàn bộ {answeredCount > 0 ? <strong className="text-slate-800 font-medium">{answeredCount} câu đã trả lời</strong> : 'câu trả lời'} và đồng hồ đếm ngược sẽ được đặt lại từ đầu.
          </p>
        </div>

        {/* Actions */}
        <div className="p-5 pt-0 flex flex-col gap-2">
          <button
            type="button"
            onClick={onConfirmReset}
            className="w-full py-2.5 px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-medium rounded-lg transition-colors cursor-pointer shadow-xs"
          >
            Xác nhận làm lại từ đầu
          </button>

          <button
            type="button"
            onClick={onClose}
            className="w-full py-2 px-4 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-medium rounded-lg transition-colors cursor-pointer"
          >
            Hủy thao tác
          </button>
        </div>
      </div>
    </div>
  );
}
