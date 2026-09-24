'use client';

import { useState, useEffect, useMemo } from 'react';
import { useRouter, useSearchParams, usePathname } from 'next/navigation';
import { HelpCircle } from 'lucide-react';
import { ExamItem, FilterState } from '@/types/examList';

import { CourseItem } from '@/constants/mockCourses';
import { getCurrentUser } from '@/services/authService';
import { fetchMyExamHistory } from '@/services/examService';
import ExamCard from './ExamCard';

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
  // Mặc định ban đầu luôn là untaken (Vào thi) khi chưa đăng nhập
  const [exams, setExams] = useState<ExamItem[]>(() =>
    initialExams.map((e) => ({
      ...e,
      status: 'untaken' as const,
      userProgress: undefined,
      userScore: undefined,
    }))
  );
  const router = useRouter();
  const searchParams = useSearchParams();
  const pathname = usePathname();

  const [filterState, setFilterState] = useState<FilterState>(() => {
    return {
      category: (searchParams.get('category') as any) || DEFAULT_FILTER_STATE.category,
      source: searchParams.get('source') || DEFAULT_FILTER_STATE.source,
      targetScore: searchParams.get('targetScore') || DEFAULT_FILTER_STATE.targetScore,
      status: searchParams.get('status') || DEFAULT_FILTER_STATE.status,
      sortBy: (searchParams.get('sortBy') as any) || DEFAULT_FILTER_STATE.sortBy,
      searchQuery: searchParams.get('search') || DEFAULT_FILTER_STATE.searchQuery,
    };
  });

  // Đồng bộ FilterState lên URL params (Debounced bởi next/navigation mặc định cho router.replace)
  useEffect(() => {
    const params = new URLSearchParams(Array.from(searchParams.entries()));
    
    const setOrDelete = (key: string, value: string, defaultVal: string) => {
      if (value !== defaultVal && value !== '') params.set(key, value);
      else params.delete(key);
    };

    setOrDelete('category', filterState.category, DEFAULT_FILTER_STATE.category);
    setOrDelete('source', filterState.source, DEFAULT_FILTER_STATE.source);
    setOrDelete('targetScore', filterState.targetScore, DEFAULT_FILTER_STATE.targetScore);
    setOrDelete('status', filterState.status, DEFAULT_FILTER_STATE.status);
    setOrDelete('sortBy', filterState.sortBy, DEFAULT_FILTER_STATE.sortBy);
    setOrDelete('search', filterState.searchQuery, DEFAULT_FILTER_STATE.searchQuery);

    const query = params.toString();
    const newUrl = query ? `${pathname}?${query}` : pathname;
    
    // Dùng replace thay vì push để không làm rác History khi gõ phím
    router.replace(newUrl, { scroll: false });
  }, [filterState, pathname, router, searchParams]);

  // Tải trạng thái và tiến độ bài thi chuẩn xác theo từng tài khoản đăng nhập
  useEffect(() => {
    let isCancelled = false;

    async function hydrate() {
      const user = await getCurrentUser();
      if (isCancelled) return;

      // CHƯA ĐĂNG NHẬP -> Tất cả đề thi đều là 'untaken' (Vào thi), tuyệt đối không hiện 'Tiếp tục thi'
      if (!user) {
        setExams(
          initialExams.map((exam) => ({
            ...exam,
            status: 'untaken' as const,
            userProgress: undefined,
            userScore: undefined,
          }))
        );
        return;
      }

      const userScope = `user_${user.login}`;

      // Xoá các key cũ không có prefix tài khoản để không bị xung đột dữ liệu
      initialExams.forEach((exam) => {
        try { localStorage.removeItem(`exam_progress_${exam.id}`); } catch { /* noop */ }
      });

      // Nếu đã đăng nhập, tải điểm số và lịch sử các đề đã hoàn thành (completed) từ backend
      const historyMap: Record<string, number> = {};
      try {
        const history = await fetchMyExamHistory();
        if (!isCancelled && Array.isArray(history)) {
          history.forEach((item) => {
            if (item.examId != null) {
              const currentBest = historyMap[String(item.examId)];
              if (currentBest === undefined || (item.totalScore != null && item.totalScore > currentBest)) {
                historyMap[String(item.examId)] = item.totalScore ?? 0;
              }
            }
          });
        }
      } catch {
        // Chưa đăng nhập hoặc offline
      }

      if (isCancelled) return;

      setExams(
        initialExams.map((exam) => {
          // 1. Kiểm tra tiến độ đang làm dở (in_progress) của chính tài khoản này
          try {
            const saved = localStorage.getItem(`exam_progress_${userScope}_${exam.id}`);
            if (saved) {
              const progress = JSON.parse(saved);
              if (progress.savedAt && Date.now() - progress.savedAt < 24 * 60 * 60 * 1000) {
                const answeredCount = progress.selectedAnswers
                  ? Object.keys(progress.selectedAnswers).length
                  : 0;
                return {
                  ...exam,
                  status: 'in_progress' as const,
                  userProgress: `${answeredCount}/${exam.totalQuestions}`,
                  userScore: undefined,
                };
              }
              localStorage.removeItem(`exam_progress_${userScope}_${exam.id}`);
            }
          } catch {
            // localStorage unavailable
          }

          // 2. Kiểm tra lịch sử đã hoàn thành (completed) của chính tài khoản này từ Backend
          if (historyMap[String(exam.id)] !== undefined) {
            return {
              ...exam,
              status: 'completed' as const,
              userScore: historyMap[String(exam.id)],
              userProgress: undefined,
            };
          }

          // 3. Nếu chưa làm: untaken (tuyệt đối không chia sẻ trạng thái với tài khoản khác)
          return {
            ...exam,
            status: 'untaken' as const,
            userScore: undefined,
            userProgress: undefined,
          };
        })
      );
    }

    hydrate();

    // Tự động cập nhật lại giao diện ngay lập tức khi đăng nhập / đăng xuất hoặc đổi tài khoản
    const handleAuthChange = () => {
      hydrate();
    };

    window.addEventListener('auth-state-changed', handleAuthChange);
    return () => {
      isCancelled = true;
      window.removeEventListener('auth-state-changed', handleAuthChange);
    };
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

      {/* Lưới Hiển Thị Đề Thi (Responsive: 1-col mobile, 2-col tablet, 3-col desktop) */}
      {filteredExams.length > 0 ? (
        <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredExams.map((exam) => (
            <ExamCard key={`exam-${exam.id}`} card={exam} />
          ))}
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
