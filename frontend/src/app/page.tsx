import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import ExamListPage from '@/components/home/ExamListPage';
import { fetchExamsFromBackend } from '@/services/examService';
import { fetchCoursesFromBackend } from '@/services/courseService';
import { FALLBACK_EXAM_CARDS } from '@/constants/mockExams';
import { FALLBACK_COURSES, CourseItem } from '@/constants/mockCourses';
import { ExamItem } from '@/types/examList';

export const dynamic = 'force-dynamic';

const CATEGORY_MAP: Record<string, ExamItem['category']> = {
  FULL_TEST: 'full',
  MINI_TEST: 'mini',
  PRACTICE_PART: 'reading',
  READING: 'reading',
  LISTENING: 'listening',
};

export default async function HomePage() {
  const [backendExams, backendCourses] = await Promise.all([
    fetchExamsFromBackend(),
    fetchCoursesFromBackend(),
  ]);

  const examCards: ExamItem[] =
    backendExams && backendExams.length > 0
      ? backendExams.map((e, idx) => {
          const cat = (e.category && CATEGORY_MAP[e.category]) || (e.title?.toLowerCase().includes('reading') ? 'reading' : 'full');
          const isReading = cat === 'reading';
          const isListening = cat === 'listening';
          return {
            id: String(e.id),
            title: e.title || `ToeicPro Practice Exam ${e.id}`,
            description: `Đề thi chính thức đồng bộ từ ExamService. Gồm ${e.totalQuestions || (isReading ? 100 : 200)} câu hỏi trắc nghiệm chuẩn format ETS.`,
            tag1: e.title?.includes('2023') ? 'ETS 2023' : e.title?.includes('2024') ? 'ETS 2024' : 'ETS 2026',
            tag1Type: 'standard' as const,
            tag2: e.code || `Đề #${e.id}`,
            tag2Type: 'normal' as const,
            durationMinutes: e.durationMinutes || (isReading ? 75 : 120),
            totalQuestions: e.totalQuestions || (isReading ? 100 : 200),
            takenCount: (idx + 1) * 1240,
            averageScore: 680,
            category: cat,
            source: 'ETS Authentic',
            targetScore: '750+',
            status: 'untaken' as const,
            audioAccents: isReading ? 'Bài thi Đọc' : 'Audio: 4 Giọng đọc',
            listeningQuestions: isReading ? 0 : 100,
            readingQuestions: isListening ? 0 : (e.totalQuestions || 100),
          };
        })
      : FALLBACK_EXAM_CARDS;

  const courses: CourseItem[] =
    backendCourses && backendCourses.length > 0
      ? backendCourses.map((c) => ({
          id: c.id,
          tag: 'TOEIC Pro Master',
          tagClass: 'bg-blue-50 text-blue-700',
          title: c.title,
          desc: c.description || 'Khóa học ôn luyện TOEIC chuẩn format quốc tế.',
          lessons: '36 bài giảng video',
          tests: '10 bài test thực hành',
          price: c.price ? `${c.price.toLocaleString('vi-VN')}₫` : '1.890.000₫',
          originalPrice: '2.500.000₫',
        }))
      : FALLBACK_COURSES;

  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col selection:bg-blue-100 selection:text-blue-700">
      <Navbar />

      <main className="w-full pt-20 pb-16 flex-1">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <ExamListPage initialExams={examCards} courses={courses} />
        </div>
      </main>

      <Footer />
    </div>
  );
}
