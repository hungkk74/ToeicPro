import { CoursePromoItem } from '@/types/coursePromo';

export const PROMO_COURSES: CoursePromoItem[] = [
  {
    id: 'promo-1',
    badge: 'Khóa học bứt phá',
    badgeType: 'breakthrough',
    title: 'Master TOEIC 850+ Cùng Chuyên Gia ETS',
    subtitle: 'Lộ trình tối ưu hóa điểm số dành cho mục tiêu từ 600 lên 850+ trong 60 ngày',
    durationHours: 45,
    liveSessions: 18,
    reviewSessions: 10,
    highlights: [
      'Cam kết đầu ra bằng văn bản (hoàn 100% học phí nếu không đạt)',
      'Tặng kèm full 20 bộ đề thi ETS có video giải chi tiết từng câu',
      'Sửa bài nói & viết 1-1 trực tiếp cùng giáo viên 990 TOEIC',
    ],
    originalPrice: 3800000,
    discountedPrice: 2490000,
    discountPercent: 35,
    tagline: 'Chỉ còn 5 suất ưu đãi trong tuần này',
  },
  {
    id: 'promo-2',
    badge: 'Khóa giải đề cấp tốc',
    badgeType: 'speed',
    title: 'Chiến Lược Luyện Đề & Bẫy Đề ETS 30 Ngày',
    subtitle: 'Tổng ôn ngữ pháp bẫy Part 5 và phương pháp quét nhanh Part 7 đa đoạn văn',
    durationHours: 30,
    liveSessions: 12,
    reviewSessions: 8,
    highlights: [
      'Bộ mẹo tránh bẫy câu hỏi suy luận Part 3 & 4 đa người nói',
      'Tặng kho tài liệu 1000 từ vựng cốt lõi hay gặp nhất trong đề thi',
      'Hỗ trợ giải đáp thắc mắc 24/7 trong nhóm học tập VIP',
    ],
    originalPrice: 2800000,
    discountedPrice: 1890000,
    discountPercent: 33,
    tagline: 'Tặng kèm tài khoản luyện thi không giới hạn',
  },
];
