'use client';

import { useState, useEffect } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { ArrowRight, ChevronLeft, ChevronRight } from 'lucide-react';

const images = [
  {
    src: '/banners/banner.png',
    caption: 'Không gian phòng thi trực tuyến chuẩn hóa',
  },
  {
    src: '/banners/class_study.png',
    caption: 'Luyện đề chuyên sâu theo cấu trúc ETS 2026',
  },
  {
    src: '/banners/disscussion.png',
    caption: 'Phân tích chi tiết từng bẫy đề thi và từ vựng',
  },
  {
    src: '/banners/girl_study.png',
    caption: 'Tự đánh giá tiến độ qua từng lượt làm bài',
  },
  {
    src: '/banners/teacher.png',
    caption: 'Đội ngũ học thuật bám sát đề thi thực tế',
  },
];

export default function HomeCarousel() {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % images.length);
    }, 6000);
    return () => clearInterval(timer);
  }, []);

  const goToPrevious = () => {
    setCurrentIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
  };

  const goToNext = () => {
    setCurrentIndex((prev) => (prev + 1) % images.length);
  };

  return (
    <section className="border-b border-slate-200/80 pb-12 sm:pb-16">
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-10 items-center">
        {/* Left Column: Editorial & Authentic Typography */}
        <div className="lg:col-span-7 space-y-6">
          <div className="space-y-3">
            <div className="inline-flex items-center gap-2 text-[11px] font-semibold text-slate-600 tracking-wider uppercase">
              <span className="w-1.5 h-1.5 rounded-full bg-blue-600" />
              <span>Hệ Thống Đào Tạo &amp; Khảo Thí Chuẩn ETS</span>
            </div>

            <h1 className="font-serif text-3xl sm:text-4xl lg:text-[44px] font-semibold text-slate-950 tracking-tight leading-[1.18]">
              Đo lường chính xác năng lực TOEIC theo thời gian thực.
            </h1>

            <p className="text-slate-600 text-sm sm:text-base leading-relaxed max-w-xl font-sans pt-1">
              Mô phỏng 100% giao diện thi trên máy tính, trọn vẹn 7 phần Nghe - Đọc với giọng đọc bản ngữ Anh, Mỹ, Úc, Canada. Báo cáo điểm số và đáp án tức thì sau khi nộp bài.
            </p>
          </div>

          {/* Key Specs in Minimalist Hairline Columns */}
          <div className="grid grid-cols-3 gap-4 pt-2 border-t border-slate-200/80 max-w-lg">
            <div className="space-y-0.5">
              <span className="font-serif text-xl sm:text-2xl font-bold text-slate-950 tabular-nums">200</span>
              <p className="text-xs text-slate-500 font-medium">Câu hỏi / Đề full</p>
            </div>
            <div className="space-y-0.5 border-l border-slate-200/80 pl-4">
              <span className="font-serif text-xl sm:text-2xl font-bold text-slate-950 tabular-nums">120′</span>
              <p className="text-xs text-slate-500 font-medium">Đồng hồ đếm ngược</p>
            </div>
            <div className="space-y-0.5 border-l border-slate-200/80 pl-4">
              <span className="font-serif text-xl sm:text-2xl font-bold text-slate-950 tabular-nums">4 Giọng</span>
              <p className="text-xs text-slate-500 font-medium">Audio chuẩn ETS</p>
            </div>
          </div>

          {/* Action Row */}
          <div className="flex flex-wrap items-center gap-3 pt-2">
            <Link
              href="/de-thi"
              className="inline-flex items-center gap-2 px-5 py-2.5 rounded-lg bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs sm:text-sm font-medium transition-colors shadow-xs"
            >
              <span>Vào luyện đề ngay</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
            <Link
              href="/quy-che-khao-thi"
              className="inline-flex items-center gap-1.5 px-4 py-2.5 rounded-lg border border-slate-300 hover:border-slate-400 bg-white hover:bg-slate-50 text-slate-700 text-xs sm:text-sm font-medium transition-colors"
            >
              <span>Quy chế tính điểm</span>
            </Link>
          </div>
        </div>

        {/* Right Column: Frame Banner with Calm Motion */}
        <div className="lg:col-span-5">
          <div className="relative rounded-xl border border-slate-200/90 bg-slate-100 overflow-hidden">
            <div className="relative w-full h-[260px] sm:h-[320px]">
              {images.map((item, index) => (
                <div
                  key={index}
                  className={`absolute inset-0 transition-opacity duration-700 ease-in-out ${
                    index === currentIndex ? 'opacity-100 z-10' : 'opacity-0 pointer-events-none z-0'
                  }`}
                >
                  <Image
                    src={item.src}
                    alt={item.caption}
                    fill
                    sizes="(max-width: 768px) 100vw, 40vw"
                    className="object-cover"
                    priority={index === 0}
                  />
                  {/* Subtle caption bar */}
                  <div className="absolute inset-x-0 bottom-0 bg-slate-950/70 backdrop-blur-xs text-slate-200 px-3.5 py-2 text-xs flex items-center justify-between">
                    <span className="truncate pr-2">{item.caption}</span>
                    <span className="tabular-nums font-mono text-[11px] text-slate-400 shrink-0">
                      0{index + 1} / 0{images.length}
                    </span>
                  </div>
                </div>
              ))}
            </div>

            {/* Quiet Pagination Controls */}
            <div className="flex items-center justify-between px-3 py-2 bg-slate-50 border-t border-slate-200/80">
              <div className="flex gap-1.5">
                {images.map((_, index) => (
                  <button
                    key={index}
                    type="button"
                    onClick={() => setCurrentIndex(index)}
                    aria-label={`Ảnh ${index + 1}`}
                    className={`h-1.5 rounded-full transition-all duration-300 ${
                      currentIndex === index ? 'w-6 bg-blue-600' : 'w-2 bg-slate-300 hover:bg-slate-400'
                    }`}
                  />
                ))}
              </div>

              <div className="flex items-center gap-1">
                <button
                  type="button"
                  onClick={goToPrevious}
                  aria-label="Ảnh trước"
                  className="p-1 rounded text-slate-600 hover:text-slate-900 hover:bg-slate-200/60 transition-colors"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
                <button
                  type="button"
                  onClick={goToNext}
                  aria-label="Ảnh sau"
                  className="p-1 rounded text-slate-600 hover:text-slate-900 hover:bg-slate-200/60 transition-colors"
                >
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
