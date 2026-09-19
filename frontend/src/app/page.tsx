import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import ExamListPage from '@/components/home/ExamListPage';
import { fetchExamsFromBackend } from '@/services/examService';
import { fetchCoursesFromBackend } from '@/services/courseService';
import { FALLBACK_EXAM_CARDS } from '@/constants/mockExams';
import { FALLBACK_COURSES, CourseItem } from '@/constants/mockCourses';
import { ExamItem } from '@/types/examList';

export default async function HomePage() {
  const [backendExams, backendCourses] = await Promise.all([
    fetchExamsFromBackend(),
    fetchCoursesFromBackend(),
  ]);

  const examCards: ExamItem[] =
    backendExams && backendExams.length > 0
      ? backendExams.map((e, idx) => ({
          id: String(e.id),
          title: e.title || `ToeicPro Practice Exam ${e.id}`,
          description: `Đề thi chính thức đồng bộ từ ExamService. Gồm ${e.totalQuestions || 200} câu hỏi trắc nghiệm chuẩn format ETS.`,
          tag1: 'ETS 2026',
          tag1Type: 'standard',
          tag2: e.code || `Đề #${e.id}`,
          tag2Type: 'normal',
          durationMinutes: e.durationMinutes || 120,
          totalQuestions: e.totalQuestions || 200,
          takenCount: (idx + 1) * 1240,
          averageScore: 680,
          category: 'full',
          source: 'ETS Authentic',
          targetScore: '750+',
          status: 'untaken',
          audioAccents: 'Live Backend DB',
          listeningQuestions: 100,
          readingQuestions: 100,
        }))
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
