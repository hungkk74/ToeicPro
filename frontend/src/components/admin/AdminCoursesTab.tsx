'use client';

import { GraduationCap, BookOpen, Users, Clock, CheckCircle2, Plus, Sparkles } from 'lucide-react';

export default function AdminCoursesTab() {
  const courses = [
    {
      id: 1,
      title: 'Lộ Trình Cấp Tốc TOEIC 550+ (Nền Tảng Đột Phá)',
      level: 'Mục tiêu 550 – 650',
      chapters: 6,
      lessons: 18,
      students: 240,
      status: 'Đang mở đăng ký',
      tag: 'Phổ biến nhất',
    },
    {
      id: 2,
      title: 'Chiến Thuật Bứt Phá Điểm Số TOEIC 750+ Chuyên Sâu',
      level: 'Mục tiêu 750 – 850',
      chapters: 8,
      lessons: 26,
      students: 195,
      status: 'Đang mở đăng ký',
      tag: 'Nâng cao',
    },
    {
      id: 3,
      title: 'Chinh Phục Tuyệt Đối TOEIC 900+ (Master Listening & Reading)',
      level: 'Mục tiêu 900 – 990',
      chapters: 10,
      lessons: 32,
      students: 92,
      status: 'Đang mở đăng ký',
      tag: 'Chuyên gia',
    },
  ];

  return (
    <div className="space-y-5">
      {/* Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-slate-200 shadow-2xs">
        <div className="flex items-center gap-2">
          <GraduationCap className="w-5 h-5 text-blue-600" />
          <div>
            <h2 className="text-sm font-bold text-slate-900">Quản Lý Khóa Học &amp; Bài Giảng</h2>
            <p className="text-[11px] text-slate-500">Đồng bộ với CourseService (:8083) qua API Gateway</p>
          </div>
        </div>

        <button
          type="button"
          onClick={() => alert('Chức năng tạo khóa học mới qua CourseService sẽ sớm được phát hành!')}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white font-medium text-xs rounded-lg shadow-xs transition-colors cursor-pointer self-start sm:self-auto"
        >
          <Plus className="w-3.5 h-3.5" />
          <span>Thêm Khóa Học Mới</span>
        </button>
      </div>

      {/* Courses Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {courses.map((c) => (
          <div
            key={c.id}
            className="p-5 bg-white rounded-2xl border border-slate-200 shadow-2xs flex flex-col justify-between space-y-4 hover:border-blue-500 transition-colors"
          >
            <div>
              <div className="flex items-center justify-between gap-2">
                <span className="text-[11px] font-bold text-blue-700 bg-blue-50 border border-blue-200 px-2 py-0.5 rounded">
                  {c.level}
                </span>
                <span className="text-[10px] font-medium text-slate-500 bg-slate-100 px-1.5 py-0.5 rounded">
                  {c.tag}
                </span>
              </div>
              <h3 className="font-bold text-slate-900 text-sm mt-2.5 leading-snug">
                {c.title}
              </h3>
            </div>

            <div className="space-y-2 pt-2 border-t border-slate-100 text-xs text-slate-600">
              <div className="flex items-center justify-between">
                <span className="flex items-center gap-1.5 text-slate-500">
                  <BookOpen className="w-3.5 h-3.5 text-slate-400" />
                  Chương / Bài giảng:
                </span>
                <span className="font-semibold text-slate-800">
                  {c.chapters} chương • {c.lessons} bài
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="flex items-center gap-1.5 text-slate-500">
                  <Users className="w-3.5 h-3.5 text-slate-400" />
                  Học viên ghi danh:
                </span>
                <span className="font-semibold text-blue-600">{c.students} học viên</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="flex items-center gap-1.5 text-slate-500">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500" />
                  Trạng thái:
                </span>
                <span className="font-medium text-emerald-700">{c.status}</span>
              </div>
            </div>

            <button
              type="button"
              onClick={() => alert(`Xem chi tiết chương trình khóa: ${c.title}`)}
              className="w-full py-2 bg-slate-50 hover:bg-slate-100 active:bg-slate-200 text-slate-700 font-semibold text-xs rounded-xl border border-slate-200 transition-colors cursor-pointer text-center"
            >
              Xem Chi Tiết Bài Học
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
