'use client';

import { ArrowRight } from 'lucide-react';

interface TopPromotionBannerProps {
  onExploreClick?: () => void;
}

export default function TopPromotionBanner({ onExploreClick }: TopPromotionBannerProps) {
  const handleClick = () => {
    if (onExploreClick) {
      onExploreClick();
    } else {
      const el = document.getElementById('courses');
      if (el) {
        el.scrollIntoView({ behavior: 'smooth' });
      }
    }
  };

  return (
    <div className="bg-slate-100/70 border border-slate-200/90 rounded-lg px-4 py-3 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
      {/* Left Message */}
      <div className="text-xs sm:text-sm leading-relaxed">
        <span className="font-serif font-semibold text-slate-900 mr-2">
          Mục tiêu 750+ TOEIC?
        </span>
        <span className="text-slate-600">
          Lộ trình luyện đề chuẩn ETS với bảng phân tích chi tiết từng dạng câu hỏi và kỹ năng còn yếu.
        </span>
      </div>

      {/* Right Solid CTA Button */}
      <div className="flex items-center self-end sm:self-center shrink-0">
        <button
          type="button"
          onClick={handleClick}
          className="bg-slate-900 hover:bg-slate-800 active:bg-slate-950 text-white font-medium text-xs px-3.5 py-1.5 rounded-md transition-colors flex items-center gap-1.5 cursor-pointer"
        >
          <span>Khóa học trọng tâm</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </button>
      </div>
    </div>
  );
}
