-- ===================================================================
-- DATABASE INITIALIZATION SCRIPT FOR COURSESERVICE
-- Toeic Pro Microservices Ecosystem
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `courseservice` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `courseservice`;

-- 1. Table: course (Quản lý khóa học)
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `title` VARCHAR(255) NOT NULL,
    `slug` VARCHAR(255) NOT NULL UNIQUE,
    `description` LONGTEXT,
    `price` DECIMAL(21, 2) NOT NULL,
    `discount_price` DECIMAL(21, 2),
    `thumbnail_url` VARCHAR(1000),
    `level` VARCHAR(50) NOT NULL, -- 'BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'ALL_LEVELS'
    `is_published` BOOLEAN NOT NULL DEFAULT TRUE,
    `total_duration_seconds` INT DEFAULT 0,
    `total_lessons` INT DEFAULT 0,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table: chapter (Chương / Phần học)
CREATE TABLE IF NOT EXISTS `chapter` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `course_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `description` LONGTEXT,
    `sort_order` INT NOT NULL DEFAULT 1,
    CONSTRAINT `fk_chapter_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table: lesson (Chi tiết từng bài học)
CREATE TABLE IF NOT EXISTS `lesson` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `chapter_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `lesson_type` VARCHAR(50) NOT NULL, -- 'VIDEO', 'DOCUMENT', 'QUIZ'
    `video_url` VARCHAR(1000),
    `duration_seconds` INT DEFAULT 0,
    `document_url` VARCHAR(1000),
    `content` LONGTEXT,
    `is_free_preview` BOOLEAN NOT NULL DEFAULT FALSE,
    `sort_order` INT NOT NULL DEFAULT 1,
    CONSTRAINT `fk_lesson_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Table: course_enrollment (Học viên sở hữu khóa học)
CREATE TABLE IF NOT EXISTS `course_enrollment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(36) NOT NULL, -- Keycloak UUID
    `course_id` BIGINT NOT NULL,
    `status` VARCHAR(50) NOT NULL, -- 'ACTIVE', 'EXPIRED', 'CANCELLED'
    `enrolled_at` DATETIME(6) NOT NULL,
    `expires_at` DATETIME(6) NULL, -- NULL = sở hữu trọn đời
    CONSTRAINT `fk_enrollment_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Table: lesson_progress (Theo dõi tiến độ học bài)
CREATE TABLE IF NOT EXISTS `lesson_progress` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(36) NOT NULL,
    `lesson_id` BIGINT NOT NULL,
    `is_completed` BOOLEAN NOT NULL DEFAULT FALSE,
    `last_watched_second` INT DEFAULT 0,
    `completed_at` DATETIME(6) NULL,
    `updated_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_progress_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- SEED SAMPLE DATA (Dữ liệu mẫu kiểm thử)
-- ===================================================================

-- 1. Thêm 2 khóa học mẫu
INSERT INTO `course` (`id`, `code`, `title`, `slug`, `description`, `price`, `discount_price`, `thumbnail_url`, `level`, `is_published`, `total_duration_seconds`, `total_lessons`, `created_at`)
VALUES 
(1, 'TOEIC_650_INTENSIVE', 'Khóa học TOEIC 650+ Chuyên Sâu', 'toeic-650-chuyen-sau', 'Lộ trình bứt phá điểm số từ 450 lên 650+ trong 60 ngày cùng phương pháp phản xạ độc quyền.', 1200000.00, 899000.00, 'https://cdn.toeicpro.com/courses/toeic650.jpg', 'INTERMEDIATE', TRUE, 14400, 32, NOW(6)),
(2, 'TOEIC_FOUNDATION_450', 'Khóa học TOEIC Nền Tảng Cho Người Mất Gốc', 'toeic-nen-tang-450', 'Xây dựng lại toàn bộ ngữ pháp và từ vựng cốt lõi thường xuất hiện nhất trong bài thi TOEIC.', 800000.00, 599000.00, 'https://cdn.toeicpro.com/courses/toeic450.jpg', 'BEGINNER', TRUE, 10800, 24, NOW(6))
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 2. Thêm các chương học mẫu cho Khóa 1
INSERT INTO `chapter` (`id`, `course_id`, `title`, `description`, `sort_order`)
VALUES 
(1, 1, 'Chương 1: Chiến thuật xử lý Part 1 & Part 2', 'Nắm vững bẫy phát âm và cách loại trừ đáp án nhanh trong 3 giây.', 1),
(2, 1, 'Chương 2: Ngữ pháp cốt lõi Part 5', 'Chinh phục 30 câu trắc nghiệm ngữ pháp và từ loại chỉ với 15 phút.', 2)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 3. Thêm các bài học mẫu
INSERT INTO `lesson` (`id`, `chapter_id`, `title`, `lesson_type`, `video_url`, `duration_seconds`, `document_url`, `content`, `is_free_preview`, `sort_order`)
VALUES 
(1, 1, 'Bài 1: 50 Động từ thường gặp nhất Part 1', 'VIDEO', 'https://cdn.toeicpro.com/videos/ch1_lesson1.mp4', 1200, 'https://cdn.toeicpro.com/docs/ch1_lesson1.pdf', 'Nội dung tóm tắt bài giảng...', TRUE, 1),
(2, 1, 'Bài 2: Bẫy đồng âm và bẫy thì trong Part 1', 'VIDEO', 'https://cdn.toeicpro.com/videos/ch1_lesson2.mp4', 1500, 'https://cdn.toeicpro.com/docs/ch1_lesson2.pdf', 'Phân tích các bẫy thường gặp...', FALSE, 2),
(3, 2, 'Bài 3: Nhận diện nhanh Danh từ, Động từ, Tính từ, Trạng từ', 'VIDEO', 'https://cdn.toeicpro.com/videos/ch2_lesson3.mp4', 1800, 'https://cdn.toeicpro.com/docs/ch2_lesson3.pdf', 'Các đuôi từ nhận diện...', FALSE, 1)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 4. Thêm quyền sở hữu khóa học mẫu cho học viên
INSERT INTO `course_enrollment` (`id`, `user_id`, `course_id`, `status`, `enrolled_at`, `expires_at`)
VALUES 
(1, 'd9b1c7a8-1234-4567-89ab-cdef01234567', 1, 'ACTIVE', NOW(6), NULL)
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);

-- 5. Thêm tiến độ học bài mẫu
INSERT INTO `lesson_progress` (`id`, `user_id`, `lesson_id`, `is_completed`, `last_watched_second`, `completed_at`, `updated_at`)
VALUES 
(1, 'd9b1c7a8-1234-4567-89ab-cdef01234567', 1, TRUE, 1200, NOW(6), NOW(6)),
(2, 'd9b1c7a8-1234-4567-89ab-cdef01234567', 2, FALSE, 450, NULL, NOW(6))
ON DUPLICATE KEY UPDATE `last_watched_second` = VALUES(`last_watched_second`);
