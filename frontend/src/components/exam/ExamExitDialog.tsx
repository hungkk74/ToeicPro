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
      <div className="absolute inset-0 bg-slate-950/40 backdrop-blur-[2px]" />

      <div
        ref={dialogRef}
        className="relative bg-white rounded-xl shadow-xl w-full max-w-sm mx-auto overflow-hidden border border-slate-200 animate-in fade-in zoom-in-95 duration-150 font-sans"
        role="dialog"
        aria-modal="true"
      >
        {/* Content */}
        <div className="p-6 pb-4 text-center space-y-2.5">
          <div className="w-11 h-11 rounded-lg bg-slate-100 text-slate-700 flex items-center justify-center mx-auto border border-slate-200">
            <LogOut className="w-5 h-5 stroke-[1.75]" />
          </div>

          <h2 className="font-serif text-base sm:text-lg font-semibold text-slate-950 tracking-tight">
            Rời khỏi phòng thi?
          </h2>

          <p className="text-xs text-slate-500 leading-relaxed max-w-xs mx-auto">
            Hệ thống hỗ trợ lưu tiến trình trong 24 giờ. Nếu chọn thoát không lưu, các câu trả lời của lần thi này sẽ bị hủy.
          </p>

          {/* Compact Stats */}
          <div className="mt-3 flex items-center justify-center gap-3 text-xs text-slate-600 bg-slate-50 py-2 px-3 rounded-lg border border-slate-200">
            <span>
              <strong className="text-slate-900 font-semibold">{answeredCount}/{totalQuestions}</strong> câu đã làm
            </span>
            <span className="w-px h-3.5 bg-slate-200" />
            <span>
              Còn <strong className="text-slate-900 font-mono font-semibold">{formatTime(timeRemaining)}</strong>
            </span>
          </div>
        </div>

        {/* Action Options */}
        <div className="p-5 pt-2 flex flex-col gap-2">
          {/* Lựa chọn 1: Lưu & Thoát */}
          <button
            type="button"
            onClick={onSaveAndExit}
            className="w-full py-2.5 px-4 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-medium rounded-lg transition-colors cursor-pointer flex flex-col items-center justify-center gap-0.5 shadow-xs"
          >
            <div className="flex items-center gap-1.5">
              <Save className="w-3.5 h-3.5" />
              <span>Lưu trạng thái &amp; Thoát</span>
            </div>
            <span className="text-[10px] text-blue-100 font-normal">Tiến trình được bảo lưu 24h</span>
          </button>

          {/* Lựa chọn 2: Thoát không lưu */}
          <button
            type="button"
            onClick={onExitWithoutSaving}
            className="w-full py-2 px-4 bg-rose-50/80 hover:bg-rose-100 text-rose-800 border border-rose-200/70 text-xs font-medium rounded-lg transition-colors cursor-pointer flex flex-col items-center justify-center gap-0.5"
          >
            <div className="flex items-center gap-1.5">
              <Trash2 className="w-3.5 h-3.5 text-rose-600" />
              <span>Thoát và không lưu</span>
            </div>
            <span className="text-[10px] text-rose-600/90 font-normal">Hủy bỏ kết quả lần thi này</span>
          </button>

          {/* Lựa chọn 3: Hủy, ở lại làm tiếp */}
          <button
            type="button"
            onClick={onClose}
            className="w-full py-2 px-4 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-medium rounded-lg transition-colors cursor-pointer flex items-center justify-center gap-1.5 mt-0.5"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Tiếp tục làm bài</span>
          </button>
        </div>
      </div>
    </div>
  );
}
