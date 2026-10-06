import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import CourseCatalog from '@/components/home/CourseCatalog';
import CourseCarousel from '@/components/course/CourseCarousel';
import { fetchCoursesFromBackend } from '@/services/courseService';
import { CourseItem } from '@/constants/mockCourses';
import TargetScoreForm from '@/components/course/TargetScoreForm';

export const revalidate = 60;

export default async function CoursesPage() {
  const backendCourses = await fetchCoursesFromBackend();
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
      : [];

  return (
    <div className="bg-slate-50 font-body-default text-slate-800 antialiased min-h-screen flex flex-col selection:bg-blue-100 selection:text-blue-700">
      <Navbar />

      <main className="w-full pt-20 pb-16 flex-1">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-12">
          {/* Hero Banner Carousel */}
          <CourseCarousel />

          <div id="target-score-section">
            <TargetScoreForm />
          </div>

          {courses.length > 0 ? (
            <div id="course-catalog-section" className="pt-6">
              <CourseCatalog courses={courses} />
            </div>
          ) : (
            <div className="text-center py-12 bg-white rounded-2xl shadow-sm border border-slate-200 mt-6">
              <p className="text-slate-500">Hiện tại chưa có khóa học nào. Vui lòng quay lại sau!</p>
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}
