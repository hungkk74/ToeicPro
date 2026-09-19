import { memo } from 'react';
import Link from 'next/link';
import { Clock, FileText, Users, TrendingUp, ArrowRight } from 'lucide-react';
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

  return (
    <article className="flex flex-col justify-between h-full bg-white rounded-xl border border-slate-200 hover:border-blue-300 hover:shadow-md transition-[border-color,box-shadow] duration-200 p-5 group">
      <div className="space-y-4">
        {/* Badges/Tags Bar */}
        <div className="flex items-center justify-between gap-2">
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

        {/* Title & Description */}
        <div className="space-y-1.5">
          <h3 className="font-bold text-slate-900 text-lg group-hover:text-blue-600 transition-colors line-clamp-1 leading-snug">
            {card.title}
          </h3>
          <p className="text-slate-500 text-sm line-clamp-2 leading-relaxed">
            {card.description}
          </p>
        </div>

        {/* Metrics Grid 2x2 with Lucide Icons */}
        <div className="grid grid-cols-2 gap-2.5 p-3 rounded-lg bg-slate-50 border border-slate-100 text-xs">
          <div className="flex items-center gap-2 text-slate-600">
            <Clock className="w-4 h-4 text-slate-400 shrink-0" />
            <span className="tabular-nums text-slate-700 font-medium">
              {card.durationMinutes} phút
            </span>
          </div>

          <div className="flex items-center gap-2 text-slate-600">
            <FileText className="w-4 h-4 text-slate-400 shrink-0" />
            <span className="tabular-nums text-slate-700 font-medium">
              {card.totalQuestions} câu hỏi
            </span>
          </div>

          <div className="flex items-center gap-2 text-slate-600">
            <Users className="w-4 h-4 text-slate-400 shrink-0" />
            <span className="tabular-nums text-slate-700 font-medium">
              {card.takenCount.toLocaleString('vi-VN')} lượt thi
            </span>
          </div>

          <div className="flex items-center gap-2 text-slate-600">
            <TrendingUp className="w-4 h-4 text-slate-400 shrink-0" />
            <span className="tabular-nums text-slate-700 font-medium">
              TB: {card.averageScore}/990
            </span>
          </div>
        </div>

        {/* Question Breakdown Chips */}
        <div className="flex items-center gap-2 pt-0.5">
          <span className="text-[11px] font-medium bg-slate-100 text-slate-700 px-2 py-0.5 rounded border border-slate-200/80">
            Nghe {card.listeningQuestions} câu
          </span>
          <span className="text-[11px] font-medium bg-slate-100 text-slate-700 px-2 py-0.5 rounded border border-slate-200/80">
            Đọc {card.readingQuestions} câu
          </span>
          <span className="text-[11px] font-medium text-slate-500 ml-auto">
            {card.audioAccents || `Mục tiêu ${card.targetScore}`}
          </span>
        </div>
      </div>

      {/* Action Footer (72% - 28% split) */}
      <div className="flex items-center gap-2.5 pt-4 mt-4 border-t border-slate-100">
        <Link
          href={`/exam/${card.id}`}
          className="w-[72%] bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-medium py-2.5 px-4 rounded-lg shadow-none text-center text-sm transition-colors flex items-center justify-center gap-1.5 group/btn"
        >
          <span>Vào thi ngay</span>
          <ArrowRight className="w-4 h-4 group-hover/btn:translate-x-0.5 transition-transform" />
        </Link>
        <button
          type="button"
          className="w-[28%] border border-slate-200 hover:border-slate-300 hover:bg-slate-50 active:bg-slate-100 text-slate-700 font-medium py-2.5 px-2 rounded-lg text-center text-sm transition-colors"
        >
          Chi tiết
        </button>
      </div>
    </article>
  );
}

export default memo(ExamCard);
