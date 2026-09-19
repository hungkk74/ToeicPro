import { memo } from 'react';
import { Video, Clock, CheckCircle2, Sparkles, MessageSquare } from 'lucide-react';
import { CoursePromoItem } from '@/types/coursePromo';

interface CoursePromoCardProps {
  promo: CoursePromoItem;
}

function CoursePromoCard({ promo }: CoursePromoCardProps) {
  return (
    <article className="flex flex-col justify-between h-full bg-white rounded-xl border border-slate-200 hover:border-blue-300 hover:shadow-md transition-[border-color,box-shadow] duration-200 p-5 group">
      <div className="space-y-3.5">
        {/* Badges / Promo Tag Row */}
        <div className="flex items-center justify-between gap-2">
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200">
            <Sparkles className="w-3.5 h-3.5 text-slate-500" />
            {promo.badge}
          </span>
          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-rose-50 text-rose-700 border border-rose-200/80">
            -{promo.discountPercent}%
          </span>
        </div>

        {/* Title & Subtitle */}
        <div className="space-y-1">
          <h3 className="font-bold text-slate-900 text-lg group-hover:text-blue-600 transition-colors line-clamp-1 leading-snug">
            {promo.title}
          </h3>
          <p className="text-slate-500 text-sm line-clamp-2 leading-relaxed">
            {promo.subtitle}
          </p>
        </div>

        {/* Roadmap Specs Grid */}
        <div className="grid grid-cols-3 gap-2 p-2.5 rounded-lg bg-slate-50 border border-slate-100 text-xs">
          <div className="flex flex-col items-center text-center">
            <Clock className="w-4 h-4 text-slate-400 mb-0.5" />
            <span className="tabular-nums text-slate-700 font-medium">{promo.durationHours}h</span>
            <span className="text-[10px] text-slate-500">Video bài giảng</span>
          </div>

          <div className="flex flex-col items-center text-center border-x border-slate-200">
            <Video className="w-4 h-4 text-slate-400 mb-0.5" />
            <span className="tabular-nums text-slate-700 font-medium">{promo.liveSessions} buổi</span>
            <span className="text-[10px] text-slate-500">Học Live tương tác</span>
          </div>

          <div className="flex flex-col items-center text-center">
            <MessageSquare className="w-4 h-4 text-slate-400 mb-0.5" />
            <span className="tabular-nums text-slate-700 font-medium">{promo.reviewSessions} lượt</span>
            <span className="text-[10px] text-slate-500">Chữa bài 1-1</span>
          </div>
        </div>

        {/* Highlights List */}
        <ul className="space-y-1.5 pt-1 text-xs text-slate-700">
          {promo.highlights.slice(0, 2).map((point, idx) => (
            <li key={idx} className="flex items-start gap-1.5 leading-snug">
              <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600 shrink-0 mt-0.5" />
              <span className="line-clamp-1">{point}</span>
            </li>
          ))}
        </ul>

        {/* Price & Tagline Box */}
        <div className="pt-2 border-t border-slate-100 flex items-baseline justify-between">
          <div className="flex items-baseline gap-2">
            <span className="text-rose-600 font-bold text-xl tabular-nums tracking-tight">
              {promo.discountedPrice.toLocaleString('vi-VN')}₫
            </span>
            <span className="text-slate-400 line-through text-xs tabular-nums">
              {promo.originalPrice.toLocaleString('vi-VN')}₫
            </span>
          </div>
          <span className="text-[11px] font-medium text-slate-700 bg-slate-100 border border-slate-200 px-2 py-0.5 rounded">
            Ưu đãi có hạn
          </span>
        </div>
      </div>

      {/* Action CTA Buttons */}
      <div className="flex items-center gap-2 pt-4 mt-3 border-t border-slate-100">
        <button
          type="button"
          className="w-[72%] bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-medium py-2.5 px-3 rounded-lg text-sm text-center transition-colors shadow-none"
        >
          Tư vấn / Mua ngay
        </button>
        <button
          type="button"
          className="w-[28%] border border-slate-200 hover:border-slate-300 hover:bg-slate-50 active:bg-slate-100 text-slate-700 font-medium py-2.5 px-2 rounded-lg text-xs text-center transition-colors whitespace-nowrap"
        >
          Học thử miễn phí
        </button>
      </div>
    </article>
  );
}

export default memo(CoursePromoCard);
