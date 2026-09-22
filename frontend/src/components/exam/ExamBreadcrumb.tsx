import Link from 'next/link';

interface ExamBreadcrumbProps {
  examId: string;
  examTitle?: string;
  partName?: string;
  partNumber?: number;
}

export default function ExamBreadcrumb({ examId, examTitle, partName, partNumber }: ExamBreadcrumbProps) {
  const isReading = partNumber ? partNumber >= 5 : false;

  return (
    <div className="mb-space-md flex flex-wrap items-center justify-between gap-space-sm bg-surface p-space-md rounded-lg shadow-sm border border-border-subtle">
      <div className="flex flex-wrap items-center gap-2 sm:gap-space-md">
        <div className="flex flex-wrap items-center gap-space-xs text-text-secondary font-label-sm text-label-sm">
          <Link href="/" className="hover:text-primary">
            {examTitle || `Đề thi thử ToeicPro #${examId}`}
          </Link>
          <span className="material-symbols-outlined text-[14px]">chevron_right</span>
          <span className="text-text-primary font-semibold">
            {isReading ? 'Kỹ năng Đọc (Reading)' : 'Kỹ năng Nghe (Listening)'}
          </span>
          {partName && (
            <>
              <span className="material-symbols-outlined text-[14px]">chevron_right</span>
              <span className="text-primary font-semibold">{partName}</span>
            </>
          )}
        </div>
        <span className="bg-surface-container-low text-primary text-[11px] font-semibold px-2 py-0.5 rounded uppercase tracking-wider border border-border-subtle whitespace-nowrap shrink-0">
          Định dạng Chuẩn ETS
        </span>
      </div>
      <div className="flex items-center gap-space-md text-text-secondary font-caption text-caption shrink-0">
        <span className="tabular-nums font-mono">Mã thí sinh: TP-8849-01</span>
      </div>
    </div>
  );
}
