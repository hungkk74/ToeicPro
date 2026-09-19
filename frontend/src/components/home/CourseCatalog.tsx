import { CourseItem } from '@/constants/mockCourses';

interface CourseCatalogProps {
  courses: CourseItem[];
}

export default function CourseCatalog({ courses }: CourseCatalogProps) {
  return (
    <section id="courses" className="space-y-4 pt-8 border-t border-slate-200">
      {/* Header section: Gọn gàng, rõ ràng */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
        <div>
          <h2 className="text-xl sm:text-2xl text-slate-900 font-bold tracking-tight">
            Khóa Học TOEIC Trực Tuyến
          </h2>
          <p className="text-slate-500 text-sm mt-0.5">
            Lộ trình bài giảng video chuyên sâu, bài tập thực hành theo từng dạng câu hỏi và ngân hàng đề thi.
          </p>
        </div>
        <span className="text-xs text-slate-500">
          {courses.length} Khóa học khả dụng
        </span>
      </div>

      {/* Lưới thẻ khóa học */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {courses.map((course) => (
          <article
            key={course.id}
            className="bg-white rounded-xl p-5 border border-slate-200 flex flex-col justify-between hover:border-blue-300 hover:shadow-md transition-[border-color,box-shadow] duration-200"
          >
            <div className="space-y-3.5">
              <div className="flex items-center justify-between">
                <span className="inline-flex items-center px-2.5 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200">
                  {course.tag}
                </span>
                <span className="material-symbols-outlined text-slate-400 text-[18px]">
                  school
                </span>
              </div>

              <div>
                <h3 className="text-base text-slate-900 tracking-tight font-bold">
                  {course.title}
                </h3>
                <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                  {course.desc}
                </p>
              </div>

              {/* Thông số khóa học */}
              <div className="space-y-1.5 p-3 rounded-lg bg-slate-50 text-xs text-slate-600 border border-slate-100">
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

            {/* Giá và nút Đăng ký */}
            <div className="pt-3.5 mt-3.5 flex items-center justify-between gap-2 border-t border-slate-100">
              <div className="flex items-baseline gap-1.5">
                <span className="text-base text-slate-900 font-bold tabular-nums">
                  {course.price}
                </span>
                <span className="text-xs text-slate-400 line-through tabular-nums">
                  {course.originalPrice}
                </span>
              </div>
              <button
                className="bg-blue-600 hover:bg-blue-700 text-white text-xs font-medium py-2 px-3.5 rounded-lg transition-colors shadow-none"
                type="button"
              >
                Đăng ký học
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
