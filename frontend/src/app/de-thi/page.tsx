import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import ExamListPage from '@/components/home/ExamListPage';
import { fetchExamsFromBackend } from '@/services/examService';
import { Suspense } from 'react';
import { FALLBACK_EXAM_CARDS } from '@/constants/mockExams';
import { ExamItem } from '@/types/examList';

export const revalidate = 60;

const CATEGORY_MAP: Record<string, ExamItem['category']> = {
  FULL_TEST: 'full',
  MINI_TEST: 'mini',
  PRACTICE_PART: 'reading',
  READING: 'reading',
  LISTENING: 'listening',
};

export default async function ExamsPage() {
  const backendExams = await fetchExamsFromBackend();

  const examCards: ExamItem[] =
    backendExams && backendExams.length > 0
      ? backendExams.map((e, idx) => {
          const cat = (e.category && CATEGORY_MAP[e.category]) || (e.title?.toLowerCase().includes('reading') ? 'reading' : 'full');
          const isReading = cat === 'reading';
          const isListening = cat === 'listening';
          const titleLower = e.title?.toLowerCase() || '';
          
          let examSource = 'ETS Authentic';
          if (titleLower.includes('economy')) examSource = 'Economy';
          else if (titleLower.includes('hackers')) examSource = 'Hackers';
          else if (titleLower.includes('2023')) examSource = 'ETS Authentic 2023';

          let examTargetScore = '750+';
          if (titleLower.includes('550')) examTargetScore = '550+';
          else if (titleLower.includes('850')) examTargetScore = '850+';
          else if (titleLower.includes('900')) examTargetScore = '900+';

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
            source: examSource,
            targetScore: examTargetScore,
            status: 'untaken' as const,
            audioAccents: isReading ? 'Bài thi Đọc' : 'Audio: 4 Giọng đọc',
            listeningQuestions: isReading ? 0 : 100,
            readingQuestions: isListening ? 0 : (e.totalQuestions || 100),
          };
        })
      : FALLBACK_EXAM_CARDS;

  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col selection:bg-blue-100 selection:text-blue-700">
      <Navbar />

      <main className="w-full pt-20 pb-16 flex-1">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <Suspense fallback={<div className="p-12 text-center text-slate-500">Đang tải danh sách đề thi...</div>}>
            <ExamListPage initialExams={examCards} />
          </Suspense>
        </div>
      </main>

      <Footer />
    </div>
  );
}
