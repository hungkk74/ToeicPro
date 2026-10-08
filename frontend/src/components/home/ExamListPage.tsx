'use client';

import { useState, useEffect, useMemo } from 'react';
import { useRouter, useSearchParams, usePathname } from 'next/navigation';
import { HelpCircle } from 'lucide-react';
import { ExamItem, FilterState } from '@/types/examList';

import { getCurrentUser } from '@/services/authService';
import { fetchMyExamHistory } from '@/services/examService';
import ExamCard from './ExamCard';

import TopPromotionBanner from './TopPromotionBanner';
import FilterSection from './FilterSection';
import BackendUnavailableNotice from '@/components/common/BackendUnavailableNotice';

interface ExamListPageProps {
  initialExams: ExamItem[];
}

const DEFAULT_FILTER_STATE: FilterState = {
  category: 'all',
  source: 'all',
  targetScore: 'all',
  status: 'all',
  sortBy: 'recent',
  searchQuery: '',
};

export default function ExamListPage({ initialExams }: ExamListPageProps) {
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
    const params = new URLSearchParams();

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

    const currentQuery = searchParams.toString();
    const newQuery = params.toString();

    // Chỉ thực hiện replace khi query params thực sự có sự thay đổi, tránh navigation loop
    if (newQuery !== currentQuery) {
      const newUrl = newQuery ? `${pathname}?${newQuery}` : pathname;
      router.replace(newUrl, { scroll: false });
    }
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

      // Xoá các key cũ không có prefix tài khoản để không bị xung đột dữ liệu giữa các tài khoản/khách
      initialExams.forEach((exam) => {
        try {
          localStorage.removeItem(`exam_progress_${exam.id}`);
          localStorage.removeItem(`toeic_latest_attempt_${exam.id}`);
        } catch { /* noop */ }
      });

      // Nếu đã đăng nhập, tải điểm số và lịch sử các đề đã hoàn thành (completed) từ backend
      const latestHistoryMap: Record<string, import('@/services/examService').MyExamHistoryItem> = {};
      try {
        const history = await fetchMyExamHistory();
        if (!isCancelled && Array.isArray(history)) {
          history.forEach((item) => {
            if (item.examId != null) {
              const strId = String(item.examId);
              if (!latestHistoryMap[strId]) {
                latestHistoryMap[strId] = item;
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
          const examIdStr = String(exam.id);
          let latest = latestHistoryMap[examIdStr];

          if (!latest) {
            try {
              const cached = localStorage.getItem(`toeic_latest_attempt_${userScope}_${exam.id}`);
              if (cached) {
                latest = JSON.parse(cached);
              }
            } catch {
              // noop
            }
          }

          // 1. Kiểm tra tiến độ đang làm dở (in_progress)
          try {
            const saved = localStorage.getItem(`exam_progress_${userScope}_${exam.id}`);
            if (saved) {
              const progress = JSON.parse(saved);
              const isExpired = !progress.savedAt || (Date.now() - progress.savedAt >= 24 * 60 * 60 * 1000);

              if (!isExpired) {
                const answeredCount = progress.selectedAnswers
                  ? Object.keys(progress.selectedAnswers).length
                  : (progress.answeredCount || 0);
                const totalQ = progress.totalQuestions || exam.totalQuestions;

                return {
                  ...exam,
                  status: 'in_progress' as const,
                  userProgress: `${answeredCount}/${totalQ}`,
                  userScore: latest?.totalScore,
                  latestAttempt: latest ? {
                    attemptId: latest.attemptId,
                    totalScore: latest.totalScore,
                    listeningScore: latest.listeningScore,
                    readingScore: latest.readingScore,
                    correctAnswers: latest.correctAnswers,
                    wrongAnswers: latest.wrongAnswers,
                    skippedAnswers: latest.skippedAnswers,
                    completedAt: latest.completedAt,
                  } : undefined,
                };
              } else {
                localStorage.removeItem(`exam_progress_${userScope}_${exam.id}`);
              }
            }
          } catch {
            // localStorage unavailable
          }

          // 2. Kết quả lần thi gần nhất
          if (latest) {
            return {
              ...exam,
              status: 'completed' as const,
              userScore: latest.totalScore,
              userProgress: latest.correctAnswers != null ? `${latest.correctAnswers}/${exam.totalQuestions}` : undefined,
              latestAttempt: {
                attemptId: latest.attemptId,
                totalScore: latest.totalScore,
                listeningScore: latest.listeningScore,
                readingScore: latest.readingScore,
                correctAnswers: latest.correctAnswers,
                wrongAnswers: latest.wrongAnswers,
                skippedAnswers: latest.skippedAnswers,
                completedAt: latest.completedAt,
              },
            };
          }

          // 3. Chưa từng làm: untaken
          return {
            ...exam,
            status: 'untaken' as const,
            userScore: undefined,
            userProgress: undefined,
            latestAttempt: undefined,
          };
        })
      );
    }

    hydrate();

    const handleUpdate = () => {
      hydrate();
    };

    window.addEventListener('auth-state-changed', handleUpdate);
    window.addEventListener('exam-progress-updated', handleUpdate);
    window.addEventListener('exam-history-updated', handleUpdate);
    window.addEventListener('storage', handleUpdate);

    return () => {
      isCancelled = true;
      window.removeEventListener('auth-state-changed', handleUpdate);
      window.removeEventListener('exam-progress-updated', handleUpdate);
      window.removeEventListener('exam-history-updated', handleUpdate);
      window.removeEventListener('storage', handleUpdate);
    };
  }, [initialExams]);

  const handleFilterChange = (updates: Partial<FilterState>) => {
    setFilterState((prev) => ({ ...prev, ...updates }));
  };

  const handleResetFilters = () => {
    setFilterState(DEFAULT_FILTER_STATE);
  };

  const filteredExams = useMemo(() => {
    return exams
      .filter((exam) => {
        if (filterState.category !== 'all' && exam.category !== filterState.category) {
          return false;
        }
        if (filterState.source !== 'all' && exam.source !== filterState.source) {
          return false;
        }
        if (filterState.targetScore !== 'all' && exam.targetScore !== filterState.targetScore) {
          return false;
        }
        if (filterState.status !== 'all' && exam.status !== filterState.status) {
          return false;
        }
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
        return Number(b.id) - Number(a.id);
      });
  }, [exams, filterState]);

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="pb-4 border-b border-slate-200/80">
        <h2 className="font-serif text-2xl sm:text-3xl font-semibold text-slate-950 tracking-tight">
          Danh Sách Đề Thi Thử TOEIC
        </h2>
        <p className="text-xs sm:text-sm text-slate-600 mt-1">
          Hệ thống đề thi chuẩn định dạng ETS với giải thích chi tiết từng câu hỏi.
        </p>
      </div>

      {exams.length === 0 ? (
        <BackendUnavailableNotice />
      ) : (
        <>
          {/* Top Promotion Banner */}
          <TopPromotionBanner />

          {/* Filter Section */}
          <FilterSection
            filterState={filterState}
            onFilterChange={handleFilterChange}
            onResetFilters={handleResetFilters}
            totalFiltered={filteredExams.length}
            totalAll={exams.length}
          />

          {/* Exam Grid */}
          {filteredExams.length > 0 ? (
            <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5 sm:gap-6">
              {filteredExams.map((exam) => (
                <ExamCard key={`exam-${exam.id}`} card={exam} />
              ))}
            </section>
          ) : (
            /* Empty State */
            <div className="flex flex-col items-center justify-center p-12 text-center bg-white rounded-xl border border-slate-200/90 space-y-3">
              <div className="w-10 h-10 rounded-lg bg-slate-100 flex items-center justify-center text-slate-500 border border-slate-200">
                <HelpCircle className="w-5 h-5" />
              </div>
              <h3 className="font-serif text-base font-semibold text-slate-900">
                Không tìm thấy đề thi phù hợp
              </h3>
              <p className="text-xs sm:text-sm text-slate-600 max-w-md">
                Hiện tại không có đề thi nào thỏa mãn tiêu chí lọc. Vui lòng thử lại với bộ lọc khác.
              </p>
              <button
                type="button"
                onClick={handleResetFilters}
                className="mt-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white rounded-lg text-xs sm:text-sm font-medium transition-colors cursor-pointer shadow-xs"
              >
                Đặt lại bộ lọc
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
