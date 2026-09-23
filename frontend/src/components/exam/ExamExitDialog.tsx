'use client';

import { useEffect, useRef, useCallback } from 'react';
import { LogOut, Save, Trash2, ArrowLeft } from 'lucide-react';

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
        <div className="p-6 pb-4 text-center space-y-2.5">
          <div className="w-12 h-12 rounded-full bg-slate-100 text-slate-700 flex items-center justify-center mx-auto border border-slate-200">
            <LogOut className="w-5 h-5 text-slate-600" />
          </div>

          <h2 className="text-base font-bold text-slate-900">
            Rời khỏi phòng thi?
          </h2>

          <p className="text-xs text-slate-500 leading-relaxed">
            Bạn có thể chọn lưu lại bài làm để lần sau tiếp tục, hoặc thoát và hủy bỏ toàn bộ câu trả lời hiện tại.
          </p>

          {/* Compact Stats */}
          <div className="mt-3 flex items-center justify-center gap-3 text-xs text-slate-600 bg-slate-50 py-2 px-3 rounded-xl border border-slate-100">
            <span>
              <strong className="text-slate-900 font-semibold">{answeredCount}/{totalQuestions}</strong> câu đã làm
            </span>
            <span className="w-px h-3.5 bg-slate-200" />
            <span>
              Còn <strong className="text-blue-600 font-mono font-semibold">{formatTime(timeRemaining)}</strong>
            </span>
          </div>
        </div>

        {/* Action Options */}
        <div className="p-4 pt-2 flex flex-col gap-2">
          {/* Lựa chọn 1: Lưu & Thoát */}
          <button
            type="button"
            onClick={onSaveAndExit}
            className="w-full py-2.5 px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-semibold rounded-xl transition-colors cursor-pointer flex items-center justify-center gap-2 shadow-xs"
          >
            <Save className="w-4 h-4" />
            <span>Lưu tiến trình &amp; Thoát</span>
          </button>

          {/* Lựa chọn 2: Thoát không lưu */}
          <button
            type="button"
            onClick={onExitWithoutSaving}
            className="w-full py-2.5 px-4 bg-rose-50 hover:bg-rose-100 active:bg-rose-200 text-rose-700 border border-rose-200 text-xs font-semibold rounded-xl transition-colors cursor-pointer flex items-center justify-center gap-2"
          >
            <Trash2 className="w-4 h-4" />
            <span>Thoát và không lưu kết quả</span>
          </button>

          {/* Lựa chọn 3: Hủy, ở lại làm tiếp */}
          <button
            type="button"
            onClick={onClose}
            className="w-full py-2 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-medium rounded-xl transition-colors cursor-pointer flex items-center justify-center gap-1.5 mt-0.5"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Ở lại phòng thi</span>
          </button>
        </div>
      </div>
    </div>
  );
}
