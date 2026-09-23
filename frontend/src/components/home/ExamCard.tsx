import { memo } from 'react';
import Link from 'next/link';
import { Clock, FileText, TrendingUp, ArrowRight, CheckCircle2, CircleDot } from 'lucide-react';
import { ExamItem } from '@/types/examList';

interface ExamCardProps {
  card: ExamItem;
}

function ExamCard({ card }: ExamCardProps) {
  // Quy chuẩn màu đơn sắc đồng bộ cho Badge/Tag danh mục & format
  const getTag1Class = () => {
    return 'bg-slate-100 text-slate-700 border-slate-200';
  };

  const getTag2Class = () => {
    return 'bg-slate-100 text-slate-700 border-slate-200';
  };

  // Visual Indicator trạng thái làm bài ở góc card
  const renderStatusBadge = () => {
    if (card.status === 'completed') {
      return (
        <span
          title={`Đã hoàn thành đề thi${card.userScore ? ` - Điểm số: ${card.userScore}/990` : ''}`}
          className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200/80 shrink-0"
        >
          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
          <span>{card.userScore ? `Đã làm: ${card.userScore}/990` : 'Đã làm'}</span>
        </span>
      );
    }

    if (card.status === 'in_progress') {
      return (
        <span
          title={`Đang làm dở${card.userProgress ? ` - Tiến độ: ${card.userProgress}` : ''}`}
          className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200/80 shrink-0"
        >
          <Clock className="w-3.5 h-3.5 text-amber-600 shrink-0" />
          <span>{card.userProgress ? `Đang làm: ${card.userProgress}` : 'Đang làm dở'}</span>
        </span>
      );
    }

    // status === 'untaken'
    return (
      <span
        title="Chưa từng làm đề này"
        className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-600 border border-slate-200 shrink-0"
      >
        <CircleDot className="w-3 h-3 text-slate-400 shrink-0" />
        <span>Chưa làm</span>
      </span>
    );
  };

  return (
    <article className="flex flex-col justify-between h-full bg-white rounded-xl border border-slate-200 hover:border-blue-300 hover:shadow-md transition-[border-color,box-shadow] duration-200 p-5 group">
      <div className="space-y-4">
        {/* Badges/Tags & Status Indicator Bar */}
        <div className="flex items-center justify-between gap-2 flex-wrap">
          <div className="flex items-center gap-1.5 flex-wrap">
            <span
              className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border ${getTag1Class()}`}
            >
              {card.tag1}
            </span>
            <span
              className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border ${getTag2Class()}`}
            >
              {card.tag2}
            </span>
          </div>

          {/* Badge trạng thái ở góc card */}
          {renderStatusBadge()}
        </div>

        {/* Title & Description với line-clamp (tối đa 2 dòng) kèm tooltip */}
        <div className="space-y-1.5">
          <h3
            title={card.title}
            className="font-bold text-slate-900 text-base sm:text-lg group-hover:text-blue-600 transition-colors line-clamp-2 leading-snug cursor-pointer"
          >
            {card.title}
          </h3>
          <p
            title={card.description}
            className="text-slate-500 text-sm line-clamp-2 leading-relaxed"
          >
            {card.description}
          </p>
        </div>

        {/* Metrics Grid 3 items with Lucide Icons (Tương thích thu phóng hoàn hảo) */}
        <div className="grid grid-cols-3 gap-2 p-2.5 sm:p-3 rounded-lg bg-slate-50 border border-slate-100 text-xs">
          <div className="flex items-center gap-1.5 text-slate-600 min-w-0">
            <Clock className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <span className="tabular-nums font-semibold text-slate-800 truncate">
              {card.durationMinutes}p
            </span>
          </div>

          <div className="flex items-center gap-1.5 text-slate-600 min-w-0">
            <FileText className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <span className="tabular-nums font-semibold text-slate-800 truncate">
              {card.totalQuestions} câu
            </span>
          </div>
        </div>

        {/* Question Breakdown Chips */}
        <div className="flex items-center gap-2 pt-0.5 flex-wrap">
          <span className="text-[11px] font-medium bg-slate-100 text-slate-700 px-2 py-0.5 rounded border border-slate-200/80">
            Nghe {card.listeningQuestions} câu
          </span>
          <span className="text-[11px] font-medium bg-slate-100 text-slate-700 px-2 py-0.5 rounded border border-slate-200/80">
            Đọc {card.readingQuestions} câu
          </span>
        </div>
      </div>

      {/* Action Footer (Flex-1 + shrink-0: chống tràn và vỡ layout tuyệt đối khi zoom) */}
      <div className="flex items-center gap-2 pt-4 mt-4 border-t border-slate-100">
        <Link
          href={`/exam/${card.id}`}
          className={`flex-1 min-w-0 text-white font-medium py-2.5 px-3 sm:px-4 rounded-lg shadow-none text-center text-sm transition-colors flex items-center justify-center gap-1.5 group/btn ${
            card.status === 'in_progress'
              ? 'bg-amber-600 hover:bg-amber-700 active:bg-amber-800'
              : 'bg-blue-600 hover:bg-blue-700 active:bg-blue-800'
          }`}
        >
          <span className="truncate">
            {card.status === 'completed'
              ? 'Làm lại đề'
              : card.status === 'in_progress'
              ? 'Tiếp tục thi'
              : 'Vào thi'}
          </span>
          <ArrowRight className="w-4 h-4 shrink-0 group-hover/btn:translate-x-0.5 transition-transform" />
        </Link>
        <button
          type="button"
          className="shrink-0 px-3 py-2.5 border border-slate-200 hover:border-slate-300 hover:bg-slate-50 active:bg-slate-100 text-slate-700 font-medium rounded-lg text-center text-sm transition-colors cursor-pointer"
        >
          {card.status === 'completed' ? 'Xem lại' : 'Chi tiết'}
        </button>
      </div>
    </article>
  );
}

export default memo(ExamCard);
