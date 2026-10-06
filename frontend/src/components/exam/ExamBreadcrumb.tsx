import Link from 'next/link';
import { ChevronRight, Award } from 'lucide-react';

interface ExamBreadcrumbProps {
  examId: string;
  examTitle?: string;
  partName?: string;
  partNumber?: number;
}

export default function ExamBreadcrumb({ examId, examTitle, partName, partNumber }: ExamBreadcrumbProps) {
  const isReading = partNumber ? partNumber >= 5 : false;

  return (
    <div className="mb-4 flex flex-wrap items-center justify-between gap-3 bg-white p-3 sm:p-3.5 rounded-xl border border-slate-200/90 shadow-xs">
      <div className="flex flex-wrap items-center gap-2 sm:gap-3">
        <div className="flex flex-wrap items-center gap-1.5 text-slate-500 text-xs sm:text-sm">
          <Link href="/de-thi" className="hover:text-blue-600 transition-colors">
            {examTitle || `Đề thi #${examId}`}
          </Link>
          <ChevronRight className="w-3.5 h-3.5 text-slate-400" />
          <span className="text-slate-800 font-medium">
            {isReading ? 'Kỹ năng Đọc (Reading)' : 'Kỹ năng Nghe (Listening)'}
          </span>
          {partName && (
            <>
              <ChevronRight className="w-3.5 h-3.5 text-slate-400" />
              <span className="text-slate-950 font-semibold">{partName}</span>
            </>
          )}
        </div>
        <span className="bg-slate-100 text-slate-700 text-[11px] font-medium px-2 py-0.5 rounded border border-slate-200 whitespace-nowrap shrink-0">
          Chuẩn ETS
        </span>
      </div>
      <div className="flex items-center gap-1.5 text-slate-500 text-xs shrink-0 font-medium">
        <Award className="w-3.5 h-3.5 text-slate-600" />
        <span className="tabular-nums">Format ETS 2026</span>
      </div>
    </div>
  );
}
