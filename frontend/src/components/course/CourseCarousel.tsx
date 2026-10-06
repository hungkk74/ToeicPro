'use client';

import { useState, useEffect } from 'react';
import Image from 'next/image';
import { ArrowRight, ChevronLeft, ChevronRight, Compass } from 'lucide-react';

const courseSlides = [
  {
    src: '/banners/course/course_mentorship.png',
    caption: 'Cố vấn chuyên môn & phân tích cấu trúc đề thi chuyên sâu',
  },
  {
    src: '/banners/course/course_community.png',
    caption: 'Cộng đồng học viên luyện đề & thảo luận thực chiến 24/7',
  },
  {
    src: '/banners/course/course_online.png',
    caption: 'Không gian tự học & luyện thi trực tuyến chuẩn định dạng máy',
  },
];

export default function CourseCarousel() {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % courseSlides.length);
    }, 6000);
    return () => clearInterval(timer);
  }, []);

  const goToPrevious = () => {
    setCurrentIndex((prev) => (prev === 0 ? courseSlides.length - 1 : prev - 1));
  };

  const goToNext = () => {
    setCurrentIndex((prev) => (prev + 1) % courseSlides.length);
  };

  const scrollToSection = (id: string) => {
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <section className="border-b border-slate-200/80 pb-12 sm:pb-16">
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-10 items-center">
        {/* Left Column: Editorial & Authentic Typography */}
        <div className="lg:col-span-7 space-y-6">
          <div className="space-y-3">
            <div className="inline-flex items-center gap-2 text-[11px] font-semibold text-slate-600 tracking-wider uppercase">
              <span className="w-1.5 h-1.5 rounded-full bg-blue-600" />
              <span>Chương Trình Đào Tạo &amp; Lộ Trình Mục Tiêu</span>
            </div>

            <h1 className="font-serif text-3xl sm:text-4xl lg:text-[44px] font-semibold text-slate-950 tracking-tight leading-[1.18]">
              Chinh phục mục tiêu TOEIC với lộ trình tinh gọn, thực chiến.
            </h1>

            <p className="text-slate-600 text-sm sm:text-base leading-relaxed max-w-xl font-sans pt-1">
              Hệ thống bài giảng cô đọng, bám sát cấu trúc đề thi ETS 2026. Phân tích chi tiết từng bẫy đề Nghe - Đọc, tối ưu hóa thời gian ôn tập và trang bị phương pháp giải quyết từng dạng câu hỏi trọng điểm.
            </p>
          </div>

          {/* Key Specs in Minimalist Hairline Columns */}
          <div className="grid grid-cols-3 gap-4 pt-2 border-t border-slate-200/80 max-w-lg">
            <div className="space-y-0.5">
              <span className="font-serif text-xl sm:text-2xl font-bold text-slate-950 tabular-nums">4 Chặng</span>
              <p className="text-xs text-slate-500 font-medium">Lộ trình cá nhân hóa</p>
            </div>
            <div className="space-y-0.5 border-l border-slate-200/80 pl-4">
              <span className="font-serif text-xl sm:text-2xl font-bold text-slate-950 tabular-nums">36+</span>
              <p className="text-xs text-slate-500 font-medium">Chuyên đề video</p>
            </div>
            <div className="space-y-0.5 border-l border-slate-200/80 pl-4">
              <span className="font-serif text-xl sm:text-2xl font-bold text-slate-950 tabular-nums">850+</span>
              <p className="text-xs text-slate-500 font-medium">Cam kết đầu ra</p>
            </div>
          </div>

          {/* Action Row */}
          <div className="flex flex-wrap items-center gap-3 pt-2">
            <button
              type="button"
              onClick={() => scrollToSection('target-score-section')}
              className="inline-flex items-center gap-2 px-5 py-2.5 rounded-lg bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs sm:text-sm font-medium transition-colors shadow-xs cursor-pointer"
            >
              <span>Tư vấn lộ trình cá nhân</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
            <button
              type="button"
              onClick={() => scrollToSection('course-catalog-section')}
              className="inline-flex items-center gap-1.5 px-4 py-2.5 rounded-lg border border-slate-300 hover:border-slate-400 bg-white hover:bg-slate-50 text-slate-700 text-xs sm:text-sm font-medium transition-colors cursor-pointer"
            >
              <Compass className="w-3.5 h-3.5 text-slate-500" />
              <span>Khám phá khóa học</span>
            </button>
          </div>
        </div>

        {/* Right Column: Frame Banner with Calm Motion */}
        <div className="lg:col-span-5 flex items-center justify-center">
          <div className="relative w-full max-w-lg lg:max-w-none rounded-xl border border-slate-200/90 bg-slate-100 overflow-hidden shadow-xs">
            <div className="relative w-full h-[260px] sm:h-[320px] lg:h-[340px]">
              {courseSlides.map((item, index) => (
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
                  {/* Subtle Gradient Shadow at bottom for caption legibility */}
                  <div className="absolute inset-x-0 bottom-0 h-16 bg-gradient-to-t from-slate-950/40 to-transparent pointer-events-none" />
                </div>
              ))}

              {/* Side Floating Controls */}
              <button
                type="button"
                onClick={goToPrevious}
                aria-label="Ảnh trước"
                className="absolute left-2.5 top-1/2 -translate-y-1/2 z-20 w-7 h-7 sm:w-8 sm:h-8 rounded-full bg-white/80 hover:bg-white text-slate-700 hover:text-slate-900 flex items-center justify-center shadow-xs backdrop-blur-xs transition-all cursor-pointer opacity-70 hover:opacity-100"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                type="button"
                onClick={goToNext}
                aria-label="Ảnh sau"
                className="absolute right-2.5 top-1/2 -translate-y-1/2 z-20 w-7 h-7 sm:w-8 sm:h-8 rounded-full bg-white/80 hover:bg-white text-slate-700 hover:text-slate-900 flex items-center justify-center shadow-xs backdrop-blur-xs transition-all cursor-pointer opacity-70 hover:opacity-100"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>

            {/* Quiet Pagination Controls & Slide Caption */}
            <div className="flex items-center justify-between px-4 py-2.5 bg-white border-t border-slate-200/80">
              <p className="text-[11px] text-slate-600 font-medium truncate max-w-[240px] sm:max-w-[300px]">
                {courseSlides[currentIndex].caption}
              </p>
              <div className="flex items-center gap-1.5 shrink-0 pl-2">
                {courseSlides.map((_, index) => (
                  <button
                    key={index}
                    type="button"
                    onClick={() => setCurrentIndex(index)}
                    aria-label={`Chuyển đến ảnh ${index + 1}`}
                    className={`h-1.5 rounded-full transition-all duration-300 cursor-pointer ${
                      currentIndex === index ? 'w-6 bg-blue-600' : 'w-2 bg-slate-300 hover:bg-slate-400'
                    }`}
                  />
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
