'use client';

import { Sparkles, X } from 'lucide-react';
import { ExamAdminItem } from '@/constants/mockAdminExams';

interface AdminExamDetailModalProps {
  exam: ExamAdminItem;
  onClose: () => void;
}

export function AdminExamDetailModal({ exam, onClose }: AdminExamDetailModalProps) {
  return (
    <div
      className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 animate-in fade-in"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-2xl max-w-lg w-full border border-slate-200 shadow-2xl p-6 space-y-4"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
          <div className="flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-blue-600" />
            <h3 className="font-bold text-slate-900 text-sm">
              Cấu Trúc Đề: {exam.title}
            </h3>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="text-slate-400 hover:text-slate-700 p-1"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="space-y-2">
          <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">
            Phân bố 7 phần thi TOEIC tiêu chuẩn:
          </span>
          <div className="divide-y divide-slate-100 border border-slate-200 rounded-xl overflow-hidden">
            {exam.partsDetail.map((p) => (
              <div key={p.part} className="flex items-center justify-between p-2.5 text-xs">
                <span className="font-semibold text-slate-800">Part {p.part}: {p.name}</span>
                <span className="font-bold text-blue-600 bg-blue-50 px-2 py-0.5 rounded">
                  {p.questions} câu
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="pt-2 flex justify-end">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition-colors cursor-pointer"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}
