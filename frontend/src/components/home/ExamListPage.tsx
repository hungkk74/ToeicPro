'use client';

import { useState, useEffect, useMemo } from 'react';
import { HelpCircle } from 'lucide-react';
import { ExamItem, FilterState } from '@/types/examList';
import { CoursePromoItem } from '@/types/coursePromo';
import { PROMO_COURSES } from '@/constants/mockPromos';
import { CourseItem } from '@/constants/mockCourses';
import ExamCard from './ExamCard';
import CoursePromoCard from './CoursePromoCard';
import TopPromotionBanner from './TopPromotionBanner';
import FilterSection from './FilterSection';
import CourseCatalog from './CourseCatalog';

interface ExamListPageProps {
  initialExams: ExamItem[];
  courses?: CourseItem[];
}

const DEFAULT_FILTER_STATE: FilterState = {
  category: 'all',
  source: 'all',
  targetScore: 'all',
  status: 'all',
  sortBy: 'recent',
  searchQuery: '',
};

export default function ExamListPage({ initialExams, courses }: ExamListPageProps) {
  const [exams, setExams] = useState<ExamItem[]>(initialExams);
  const [filterState, setFilterState] = useState<FilterState>(DEFAULT_FILTER_STATE);

  // Hydrate exam status from localStorage (saved progress)
  useEffect(() => {
    setExams(
      initialExams.map((exam) => {
        try {
          const saved = localStorage.getItem(`exam_progress_${exam.id}`);
          if (saved) {
            const progress = JSON.parse(saved);
            // Only consider valid, non-expired progress (< 24h)
            if (progress.savedAt && Date.now() - progress.savedAt < 24 * 60 * 60 * 1000) {
              const answeredCount = progress.selectedAnswers
                ? Object.keys(progress.selectedAnswers).length
                : 0;
              return {
                ...exam,
                status: 'in_progress' as const,
                userProgress: `${answeredCount}/${exam.totalQuestions}`,
              };
            }
            // Expired — clean up
            localStorage.removeItem(`exam_progress_${exam.id}`);
          }
        } catch {
          // localStorage unavailable
        }
        return exam;
      })
    );
  }, [initialExams]);

  const handleFilterChange = (updates: Partial<FilterState>) => {
    setFilterState((prev) => ({ ...prev, ...updates }));
  };

  const handleResetFilters = () => {
    setFilterState(DEFAULT_FILTER_STATE);
  };

  // Logic lọc và sắp xếp trực quan theo thời gian thực
  const filteredExams = useMemo(() => {
    return exams
      .filter((exam) => {
        // Lọc theo Category
        if (filterState.category !== 'all' && exam.category !== filterState.category) {
          return false;
        }
        // Lọc theo Source
        if (filterState.source !== 'all' && exam.source !== filterState.source) {
          return false;
        }
        // Lọc theo Target Score
        if (filterState.targetScore !== 'all' && exam.targetScore !== filterState.targetScore) {
          return false;
        }
        // Lọc theo Status
        if (filterState.status !== 'all' && exam.status !== filterState.status) {
          return false;
        }
        // Lọc theo Search Query
        if (filterState.searchQuery.trim()) {
          const query = filterState.searchQuery.toLowerCase();
          const matchTitle = exam.title.toLowerCase().includes(query);
          const matchDesc = exam.description.toLowerCase().includes(query);
          if (!matchTitle && !matchDesc) return false;
        }
        return true;
      })
      .sort((a, b) => {
        if (filterState.sortBy === 'popular') {
          return b.takenCount - a.takenCount;
        }
        if (filterState.sortBy === 'hardest') {
          return a.averageScore - b.averageScore;
        }
        // 'recent' mặc định theo ID
        return Number(b.id) - Number(a.id);
      });
  }, [exams, filterState]);

  // Cứ sau mỗi 3 thẻ đề thi thì đan xen 1 thẻ khóa học khuyến mại CoursePromoCard
  const combinedItems = useMemo(() => {
    const result: Array<
      | { type: 'exam'; data: ExamItem }
      | { type: 'promo'; data: CoursePromoItem }
    > = [];
    let promoIdx = 0;

    filteredExams.forEach((exam, index) => {
      result.push({ type: 'exam', data: exam });
      if ((index + 1) % 3 === 0 && PROMO_COURSES.length > 0) {
        const promo = PROMO_COURSES[promoIdx % PROMO_COURSES.length];
        result.push({ type: 'promo', data: promo });
        promoIdx++;
      }
    });

    return result;
  }, [filteredExams]);

  return (
    <div className="space-y-6">
      {/* Header Banner Phân Cấp Thị Giác Rõ Ràng */}
      <div className="pb-4 border-b border-slate-200">

        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
          Danh Sách Đề Thi Thử TOEIC
        </h1>

      </div>

      {/* Dải Banner Khuyến Mãi Ngay Trên Khu Vực Bộ Lọc */}
      <TopPromotionBanner />

      {/* Thanh Bộ Lọc Phân Cấp (Filter Section) */}
      <FilterSection
        filterState={filterState}
        onFilterChange={handleFilterChange}
        onResetFilters={handleResetFilters}
        totalFiltered={filteredExams.length}
        totalAll={exams.length}
      />

      {/* Lưới Hiển Thị Đề Thi Đan Xen Card Khóa Học (Responsive: 1-col mobile, 2-col tablet, 3-col desktop) */}
      {filteredExams.length > 0 ? (
        <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {combinedItems.map((item, idx) =>
            item.type === 'exam' ? (
              <ExamCard key={`exam-${item.data.id}`} card={item.data} />
            ) : (
              <CoursePromoCard key={`promo-${item.data.id}-${idx}`} promo={item.data} />
            )
          )}
        </section>
      ) : (
        /* Empty State */
        <div className="flex flex-col items-center justify-center p-12 text-center bg-white rounded-xl border border-slate-200 space-y-3">
          <div className="w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center text-slate-400">
            <HelpCircle className="w-6 h-6" />
          </div>
          <h3 className="text-base font-semibold text-slate-900">
            Không tìm thấy đề thi phù hợp
          </h3>
          <p className="text-sm text-slate-500 max-w-md">
            Hiện tại không có đề thi nào thỏa mãn các tiêu chí lọc được chọn. Vui lòng thử lại với bộ lọc khác.
          </p>
          <button
            type="button"
            onClick={handleResetFilters}
            className="mt-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-medium transition-colors"
          >
            Đặt lại bộ lọc
          </button>
        </div>
      )}

      {/* Mục Khóa Học Bổ Sung */}
      {courses && courses.length > 0 && (
        <div className="pt-8">
          <CourseCatalog courses={courses} />
        </div>
      )}
    </div>
  );
}
