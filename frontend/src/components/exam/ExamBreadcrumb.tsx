import Link from 'next/link';

interface ExamBreadcrumbProps {
  examId: string;
}

export default function ExamBreadcrumb({ examId }: ExamBreadcrumbProps) {
  return (
    <div className="mb-space-md flex flex-wrap items-center justify-between gap-space-sm bg-surface p-space-md rounded-lg shadow-sm border border-border-subtle">
      <div className="flex items-center gap-space-md">
        <div className="flex items-center gap-space-xs text-text-secondary font-label-sm text-label-sm">
          <Link href="/" className="hover:text-primary">
            Đề thi thử ToeicPro #{examId}
          </Link>
          <span className="material-symbols-outlined text-[14px]">chevron_right</span>
          <span className="text-text-primary font-semibold">Phần 1: Kỹ năng Nghe (Listening)</span>
          <span className="material-symbols-outlined text-[14px]">chevron_right</span>
          <span className="text-primary font-semibold">Part 1: Mô tả Tranh</span>
        </div>
        <span className="bg-surface-container-low text-primary text-[11px] font-semibold px-2 py-0.5 rounded uppercase tracking-wider border border-border-subtle">
          Định dạng ETS v2026
        </span>
      </div>
      <div className="flex items-center gap-space-md text-text-secondary font-caption text-caption">
        <div className="flex items-center gap-1">
          <span className="w-2 h-2 rounded-full bg-status-success inline-block"></span>
          <span>Kết nối phòng thi ổn định (Độ trễ: 18ms)</span>
        </div>
        <div className="h-3 w-px bg-border-subtle"></div>
        <span className="tabular-nums font-mono">Mã thí sinh: TP-8849-01</span>
      </div>
    </div>
  );
}
