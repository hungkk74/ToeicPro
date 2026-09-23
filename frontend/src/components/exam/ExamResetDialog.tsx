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
      <div className="absolute inset-0 bg-black/40 backdrop-blur-2xs" />

      <div
        ref={dialogRef}
        className="relative bg-white rounded-2xl shadow-xl w-full max-w-sm mx-auto overflow-hidden border border-slate-200 animate-in fade-in zoom-in-95 duration-150"
        role="dialog"
        aria-modal="true"
      >
        {/* Content */}
        <div className="p-6 text-center space-y-3">
          <div className="w-12 h-12 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center mx-auto border border-blue-200">
            <RotateCcw className="w-6 h-6" />
          </div>

          <h2 className="text-base font-bold text-slate-900">
            Làm lại bài thi từ đầu?
          </h2>

          <p className="text-xs text-slate-500 leading-relaxed">
            Toàn bộ {answeredCount > 0 ? <strong className="text-slate-800">{answeredCount} câu đã trả lời</strong> : 'câu trả lời'} và đồng hồ đếm ngược sẽ được xóa để bạn làm lại từ câu 1.
          </p>
        </div>

        {/* Actions */}
        <div className="p-4 pt-0 flex flex-col gap-2">
          <button
            type="button"
            onClick={onConfirmReset}
            className="w-full py-2.5 px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-semibold rounded-xl transition-colors cursor-pointer shadow-xs"
          >
            Xác nhận làm lại từ đầu
          </button>

          <button
            type="button"
            onClick={onClose}
            className="w-full py-2 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-medium rounded-xl transition-colors cursor-pointer"
          >
            Hủy thao tác
          </button>
        </div>
      </div>
    </div>
  );
}
