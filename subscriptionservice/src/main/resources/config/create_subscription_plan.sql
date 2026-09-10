-- ===================================================================
-- SCRIPT TẠO VÀ CHÈN DỮ LIỆU CÁC GÓI HỌC (PLAN / SUBSCRIPTION_PLAN)
-- Database: subscriptionservice
-- ===================================================================

USE `subscriptionservice`;

-- 1. Tạo bảng plan (Chuẩn theo Entity Plan trong subscriptionservice.jdl)
CREATE TABLE IF NOT EXISTS `plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `price` DECIMAL(21, 2) NOT NULL,
    `duration_days` INT NOT NULL,
    `features` VARCHAR(255) DEFAULT NULL,
    `is_active` TINYINT(1) DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_plan__code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Tạo View subscription_plan trỏ về plan (để dùng được cả 2 tên gọi)
CREATE OR REPLACE VIEW `subscription_plan` AS SELECT * FROM `plan`;

-- 3. Xóa dữ liệu mẫu Faker cũ và chèn 5 gói học TOEIC thực tế
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `payment_transaction`;
TRUNCATE TABLE `subscription`;
TRUNCATE TABLE `plan`;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `plan` (`id`, `code`, `name`, `price`, `duration_days`, `features`, `is_active`) VALUES
(1, 'TOEIC_TRIAL', 'Gói Dùng Thử 7 Ngày', 0.00, 7, '{"full_test": 1, "practice_parts": true, "ai_explanation": false}', 1),
(2, 'TOEIC_BASIC_1M', 'Gói Luyện Thi Cơ Bản 1 Tháng', 199000.00, 30, '{"full_test": 10, "practice_parts": true, "ai_explanation": true}', 1),
(3, 'TOEIC_STANDARD_3M', 'Gói Tiêu Chuẩn 3 Tháng', 499000.00, 90, '{"full_test": 30, "practice_parts": true, "ai_explanation": true}', 1),
(4, 'TOEIC_PREMIUM_6M', 'Gói Chuyên Sâu 6 Tháng', 899000.00, 180, '{"full_test": 60, "practice_parts": true, "speaking_writing": true}', 1),
(5, 'TOEIC_VIP_1Y', 'Gói VIP Trọn Gói 1 Năm', 1499000.00, 365, '{"full_test": -1, "speaking_writing": true, "mentor_support": true}', 1);

-- Kiểm tra dữ liệu sau khi chèn
SELECT * FROM `plan`;
