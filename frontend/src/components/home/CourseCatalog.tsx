'use client';

import { useState, useMemo } from 'react';
import { CourseItem } from '@/constants/mockCourses';
import { Search } from 'lucide-react';

interface CourseCatalogProps {
  courses: CourseItem[];
}

const CATEGORY_TABS = [
  { key: 'all', label: 'Tất cả khóa học' },
  { key: 'Nền tảng 500–650+', label: 'Nền tảng' },
  { key: 'Bứt phá 750–850+', label: 'Bứt phá' },
  { key: 'Luyện đề 30 ngày', label: 'Luyện đề' },
  { key: 'TOEIC Pro Master', label: 'Chuyên sâu' },
];

export default function CourseCatalog({ courses }: CourseCatalogProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [activeCategory, setActiveCategory] = useState('all');

  const filteredCourses = useMemo(() => {
    return courses.filter((c) => {
      if (activeCategory !== 'all' && c.tag !== activeCategory) {
        return false;
      }
      if (!searchQuery) return true;
      const lowerQuery = searchQuery.toLowerCase();
      return (
        c.title.toLowerCase().includes(lowerQuery) ||
        c.desc.toLowerCase().includes(lowerQuery)
      );
    });
  }, [courses, searchQuery, activeCategory]);

  return (
    <section id="courses" className="space-y-6">
      {/* Header section */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="font-serif text-2xl sm:text-3xl text-slate-950 font-semibold tracking-tight">
            Khóa Học TOEIC Trọng Tâm
          </h2>
          <p className="text-slate-600 text-xs sm:text-sm mt-1">
            Lộ trình bài giảng video chuyên sâu, bài tập thực hành theo từng dạng câu hỏi và ngân hàng đề thi.
          </p>
        </div>
      </div>

      {/* Filter Box */}
      <div className="relative z-20 bg-white rounded-xl p-4 sm:p-5 border border-slate-200/90 space-y-4">
        {/* Search Bar */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input
            type="text"
            placeholder="Tìm kiếm khóa học..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-slate-50/60 hover:bg-white focus:bg-white border border-slate-200 rounded-lg pl-9 pr-4 py-2 text-xs sm:text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors"
          />
        </div>

        {/* Top Row: Segmented Control Tabs + Counter */}
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-3">
          <div className="inline-flex p-1 bg-slate-100 rounded-lg flex-wrap gap-1 border border-slate-200/60">
            {CATEGORY_TABS.map((tab) => {
              const isActive = activeCategory === tab.key;
              return (
                <button
                  key={tab.key}
                  type="button"
                  onClick={() => setActiveCategory(tab.key)}
                  className={`px-3 py-1.5 rounded-md text-xs font-medium transition-colors cursor-pointer ${
                    isActive
                      ? 'bg-blue-600 text-white shadow-xs'
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/60'
                  }`}
                >
                  {tab.label}
                </button>
              );
            })}
          </div>

          <div className="flex items-center gap-2.5 text-xs text-slate-500">
            <span className="whitespace-nowrap">
              Hiển thị: <strong className="font-semibold text-slate-900">{filteredCourses.length}</strong> khóa học
            </span>
          </div>
        </div>
      </div>

      {/* Grid */}
      {filteredCourses.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredCourses.map((course) => (
            <article
              key={course.id}
              className="bg-white rounded-xl p-5 border border-slate-200/80 hover:border-slate-400 hover:-translate-y-0.5 transition-all duration-200 flex flex-col justify-between shadow-xs"
            >
              <div className="space-y-3.5">
                <div className="flex items-center justify-between">
                  <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-50 text-blue-700 border border-blue-200/80">
                    {course.tag}
                  </span>
                  <span className="material-symbols-outlined text-slate-400 text-[18px]">
                    school
                  </span>
                </div>

                <div>
                  <h3 className="font-serif text-base text-slate-950 tracking-tight font-semibold">
                    {course.title}
                  </h3>
                  <p className="text-xs text-slate-600 mt-1 leading-relaxed">
                    {course.desc}
                  </p>
                </div>

                <div className="space-y-1.5 p-2.5 rounded-lg bg-slate-50 text-xs text-slate-700 border border-slate-200/70">
                  <div className="flex items-center gap-2">
                    <span className="material-symbols-outlined text-[16px] text-slate-400">
                      video_library
                    </span>
                    <span className="font-medium text-slate-900">{course.lessons}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="material-symbols-outlined text-[16px] text-slate-400">
                      assignment
                    </span>
                    <span>{course.tests}</span>
                  </div>
                </div>
              </div>

              <div className="pt-3.5 mt-3.5 flex items-center justify-between gap-2 border-t border-slate-100">
                <div className="flex items-baseline gap-1.5">
                  <span className="font-serif text-base text-slate-950 font-bold tabular-nums">
                    {course.price}
                  </span>
                  <span className="text-xs text-slate-400 line-through tabular-nums">
                    {course.originalPrice}
                  </span>
                </div>
                <button
                  className="bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-xs font-medium py-2 px-3.5 rounded-lg transition-colors cursor-pointer shadow-xs"
                  type="button"
                >
                  Đăng ký học
                </button>
              </div>
            </article>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center py-12 px-4 bg-white rounded-xl border border-slate-200 text-center">
          <Search className="w-8 h-8 text-slate-300 mb-3" />
          <p className="text-slate-700 font-medium text-sm">Không tìm thấy khóa học nào phù hợp</p>
          <p className="text-slate-400 text-xs mt-1">Vui lòng thử tìm kiếm bằng từ khóa khác</p>
        </div>
      )}
    </section>
  );
}
