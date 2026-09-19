export interface CourseItem {
  id: string | number;
  tag: string;
  tagClass: string;
  title: string;
  desc: string;
  lessons: string;
  tests: string;
  price: string;
  originalPrice: string;
}

export const FALLBACK_COURSES: CourseItem[] = [
  {
    id: '1',
    tag: 'Nền tảng 500–650+',
    tagClass: 'bg-primary/10 text-primary',
    title: 'ToeicPro Mục tiêu 650+',
    desc: 'Hệ thống ngữ pháp trọng điểm Part 5 & 6, 600 từ vựng cốt lõi và phản xạ nghe Part 1 & 2.',
    lessons: '48 bài giảng video',
    tests: '12 bài kiểm tra thực hành',
    price: '1.890.000₫',
    originalPrice: '2.800.000₫',
  },
  {
    id: '2',
    tag: 'Bứt phá 750–850+',
    tagClass: 'bg-tertiary/10 text-tertiary',
    title: 'ToeicPro Bứt phá 850+',
    desc: 'Chinh phục bẫy đề thi Part 3 & 4 (ngữ điệu Anh/Úc) và kỹ thuật đọc quét đa đoạn văn Part 7.',
    lessons: '36 chuyên đề chuyên sâu',
    tests: '15 đề thi thử có chấm điểm',
    price: '2.390.000₫',
    originalPrice: '3.500.000₫',
  },
  {
    id: '3',
    tag: 'Luyện đề 30 ngày',
    tagClass: 'bg-status-success-bg text-status-success',
    title: 'Luyện Đề Cấp Tốc ETS 2026',
    desc: 'Giải chi tiết 10 bộ đề chuẩn format mới, phân tích phương án gây nhiễu và tối ưu tốc độ.',
    lessons: '30 buổi luyện đề video',
    tests: '10 bộ đề thi chuẩn hóa',
    price: '1.490.000₫',
    originalPrice: '2.200.000₫',
  },
];
