-- ===================================================================
-- SCRIPT TẠO BẢNG user_subscription (QUAN HỆ N-1 VỚI plan / subscription_plan)
-- Database: subscriptionservice
-- ===================================================================

USE `subscriptionservice`;

-- 1. Tạo bảng user_subscription
DROP TABLE IF EXISTS `user_subscription`;

CREATE TABLE `user_subscription` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(36) NOT NULL COMMENT 'Định danh người dùng từ userservice/Keycloak',
    `plan_id` BIGINT NOT NULL COMMENT 'Khóa ngoại tham chiếu tới subscription_plan/plan',
    `status` VARCHAR(255) NOT NULL COMMENT 'Trạng thái: TRIAL, ACTIVE, EXPIRED, CANCELLED',
    `starts_at` DATETIME(6) NOT NULL COMMENT 'Thời gian bắt đầu gói',
    `expires_at` DATETIME(6) NOT NULL COMMENT 'Thời gian hết hạn gói',
    `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm đăng ký',
    PRIMARY KEY (`id`),
    KEY `idx_user_subscription__user_id` (`user_id`),
    KEY `idx_user_subscription__plan_id` (`plan_id`),
    CONSTRAINT `fk_user_subscription__plan_id` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Đồng bộ dữ liệu vào bảng subscription (Entity chuẩn của Spring Boot subscriptionservice)
-- Để Spring Data JPA của backend và câu lệnh truy vấn SQL đều hoạt động đồng bộ
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `payment_transaction`;
TRUNCATE TABLE `subscription`;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. Chèn dữ liệu mẫu cho các user (gắn với 5 gói học đã tạo)
INSERT INTO `user_subscription` (`id`, `user_id`, `plan_id`, `status`, `starts_at`, `expires_at`, `created_at`) VALUES
-- user1: Đã hết hạn gói dùng thử 7 ngày
(1, 'user1', 1, 'EXPIRED', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
-- user1: Đã nâng cấp lên gói 1 tháng và đang hoạt động
(2, 'user1', 2, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- user2: Đang dùng gói Tiêu chuẩn 3 tháng
(3, 'user2', 3, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 80 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
-- user3: Đang dùng gói VIP Trọn Gói 1 Năm
(4, 'user3', 5, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 335 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
-- user4: Đang dùng gói Chuyên Sâu 6 Tháng
(5, 'user4', 4, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_ADD(NOW(), INTERVAL 120 DAY), DATE_SUB(NOW(), INTERVAL 60 DAY)),
-- user5: Vừa đăng ký gói Dùng thử 7 ngày (TRIAL)
(6, 'user5', 1, 'TRIAL', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
-- user6: Đã hủy gói (CANCELLED)
(7, 'user6', 2, 'CANCELLED', DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY)),
-- user7: Đang dùng gói Tiêu chuẩn 3 tháng
(8, 'user7', 3, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 75 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
-- admin: Tài khoản Admin có gói VIP 1 năm
(9, 'admin', 5, 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY), NOW());

-- 4. Đồng bộ dữ liệu sang bảng subscription để Spring Boot API đọc được ngay
INSERT INTO `subscription` (`id`, `user_id`, `plan_id`, `status`, `starts_at`, `expires_at`, `created_at`)
SELECT `id`, `user_id`, `plan_id`, `status`, `starts_at`, `expires_at`, `created_at` FROM `user_subscription`;

-- Kiểm tra kết quả
SELECT 
    us.id,
    us.user_id,
    p.name AS plan_name,
    p.price,
    us.status,
    us.starts_at,
    us.expires_at
FROM `user_subscription` us
JOIN `plan` p ON us.plan_id = p.id;
