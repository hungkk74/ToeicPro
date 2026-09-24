import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import CourseCatalog from '@/components/home/CourseCatalog';
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
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center py-12">
            <h1 className="text-4xl font-extrabold text-slate-900 mb-4">Lộ Trình Khóa Học TOEIC</h1>
            <p className="text-lg text-slate-600 max-w-2xl mx-auto">
              Hệ thống các khóa học được thiết kế chuyên biệt, sát với đề thi thật ETS. Cung cấp phương pháp giải đề tối ưu và tiết kiệm thời gian nhất.
            </p>
          </div>

          <TargetScoreForm />

          {courses.length > 0 ? (
            <div className="mt-12">
              <CourseCatalog courses={courses} />
            </div>
          ) : (
            <div className="text-center py-12 bg-white rounded-2xl shadow-sm border border-slate-200 mt-12">
              <p className="text-slate-500">Hiện tại chưa có khóa học nào. Vui lòng quay lại sau!</p>
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}
