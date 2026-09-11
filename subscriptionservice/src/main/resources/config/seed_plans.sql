-- ===================================================================
-- SCRIPT NẠP DỮ LIỆU CÁC GÓI ĐĂNG KÝ HỌC TOEIC (PLAN)
-- Database: subscriptionservice
-- ===================================================================

USE `subscriptionservice`;

SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu cũ của bảng plan
TRUNCATE TABLE `plan`;

-- Nạp danh sách các gói cước chuẩn thực tế cho hệ thống Toeic Pro
INSERT INTO `plan` (`id`, `code`, `name`, `price`, `duration_days`, `features`, `is_active`) VALUES
(1, 'TRIAL_7D', 'Gói Dùng Thử 7 Ngày', 0.00, 7, 'Trải nghiệm 2 đề Full Test, giải thích chi tiết Part 1-2, tra 100 từ vựng', 1),
(2, 'BASIC_1M', 'Gói Cơ Bản 1 Tháng', 199000.00, 30, 'Mở khóa 10 đề ETS mới nhất, luyện thi theo từng Part, tra từ điển không giới hạn', 1),
(3, 'STANDARD_3M', 'Gói Tiêu Chuẩn 3 Tháng', 499000.00, 90, 'Mở khóa 30 đề thi chuẩn, AI chấm điểm & giải đề chi tiết, lộ trình bứt phá 650+', 1),
(4, 'INTENSIVE_6M', 'Gói Chuyên Sâu 6 Tháng', 899000.00, 180, 'Toàn bộ kho đề ETS & Hacker, AI phân tích điểm yếu, lộ trình cá nhân hóa mục tiêu 800+', 1),
(5, 'VIP_1Y', 'Gói VIP Trọn Gói 1 Năm', 1499000.00, 365, 'Full quyền không giới hạn 365 ngày, cố vấn AI 1-1, cam kết tăng tối thiểu 150+ điểm', 1),
(6, 'FAST_TRACK_14D', 'Gói Cấp Tốc 14 Ngày', 99000.00, 14, 'Dành riêng cho sĩ tử sắp thi, tổng ôn ngữ pháp trọng điểm và 5 đề sát đề thật nhất', 1),
(7, 'VIP_LIFETIME', 'Gói VIP Trọn Đời', 2499000.00, 9999, 'Sở hữu vĩnh viễn, cập nhật miễn phí toàn bộ bộ đề mới trọn đời, hỗ trợ đặc quyền 24/7', 1);

-- Đồng bộ lại dữ liệu mẫu vào bảng subscription (gắn với các gói vừa tạo)
TRUNCATE TABLE `subscription`;

INSERT INTO `subscription` (`id`, `user_id`, `plan_id`, `status`, `starts_at`, `expires_at`, `created_at`) VALUES
(1, '5f8c5069-2244-4f49-ade2-0871ac517a25', 1, 'EXPIRED', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
(2, '5f8c5069-2244-4f49-ade2-0871ac517a25', 3, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 85 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, '4c973896-5761-41fc-8217-07c5d13a004b', 5, 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), NOW()),
(4, 'user_demo_basic', 2, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(5, 'user_demo_trial', 1, 'TRIAL', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NOW());

SET FOREIGN_KEY_CHECKS = 1;
