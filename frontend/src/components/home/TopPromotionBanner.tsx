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
    <div className="bg-blue-50 border border-blue-200 rounded-xl px-5 py-3.5 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
      {/* Left Message without icon */}
      <div className="text-sm leading-snug">
        <span className="font-bold text-blue-950 mr-1.5">Mục tiêu 750+ trong 30 ngày?</span>
        <span className="text-blue-700">
          Xem ngay lộ trình luyện đề thực chiến có mentor hướng dẫn.
        </span>
      </div>

      {/* Right Solid CTA Button */}
      <div className="flex items-center self-end sm:self-center shrink-0">
        <button
          type="button"
          onClick={handleClick}
          className="bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-medium text-xs px-4 py-2 rounded-lg transition-colors flex items-center gap-1.5"
        >
          <span>Xem khóa học</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </button>
      </div>
    </div>
  );
}
