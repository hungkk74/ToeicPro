-- ===================================================================
-- DATABASE INITIALIZATION SCRIPT FOR EXAMSERVICE
-- Toeic Pro Microservices Ecosystem
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `examservice` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `examservice`;

-- 1. Table: exam (Quản lý đề thi)
CREATE TABLE IF NOT EXISTS `exam` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `title` VARCHAR(255) NOT NULL,
    `category` VARCHAR(50) NOT NULL, -- 'FULL_TEST', 'MINI_TEST', 'PRACTICE_PART'
    `duration_minutes` INT NOT NULL,
    `total_questions` INT NOT NULL,
    `audio_full_url` VARCHAR(1000),
    `is_published` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table: part (7 phần thi chuẩn TOEIC)
CREATE TABLE IF NOT EXISTS `part` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `exam_id` BIGINT NOT NULL,
    `part_number` INT NOT NULL, -- 1 đến 7
    `name` VARCHAR(100) NOT NULL,
    `total_questions` INT NOT NULL,
    CONSTRAINT `fk_part_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table: question_group (Đoạn văn đọc hiểu hoặc Audio hội thoại dùng chung)
CREATE TABLE IF NOT EXISTS `question_group` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `part_id` BIGINT NOT NULL,
    `passage_text` LONGTEXT,
    `audio_url` VARCHAR(1000),
    `image_url` VARCHAR(1000),
    CONSTRAINT `fk_question_group_part` FOREIGN KEY (`part_id`) REFERENCES `part` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Table: question (Chi tiết từng câu hỏi trắc nghiệm A, B, C, D)
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `part_id` BIGINT NOT NULL,
    `question_group_id` BIGINT NULL,
    `question_number` INT NOT NULL,
    `content` TEXT,
    `image_url` VARCHAR(1000),
    `audio_url` VARCHAR(1000),
    `option_a` TEXT NOT NULL,
    `option_b` TEXT NOT NULL,
    `option_c` TEXT NOT NULL,
    `option_d` TEXT NULL, -- Part 2 chỉ có 3 đáp án A, B, C
    `correct_option` VARCHAR(5) NOT NULL, -- 'A', 'B', 'C', 'D'
    `explanation` TEXT,
    CONSTRAINT `fk_question_part` FOREIGN KEY (`part_id`) REFERENCES `part` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_question_group` FOREIGN KEY (`question_group_id`) REFERENCES `question_group` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Table: exam_attempt (Lượt thi và điểm số của học viên)
CREATE TABLE IF NOT EXISTS `exam_attempt` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(36) NOT NULL, -- UUID từ Keycloak
    `exam_id` BIGINT NOT NULL,
    `status` VARCHAR(50) NOT NULL, -- 'IN_PROGRESS', 'COMPLETED', 'ABANDONED'
    `listening_score` INT NULL,
    `reading_score` INT NULL,
    `total_score` INT NULL,
    `correct_answers` INT NULL,
    `wrong_answers` INT NULL,
    `skipped_answers` INT NULL,
    `time_spent_seconds` INT NULL,
    `started_at` DATETIME(6) NOT NULL,
    `completed_at` DATETIME(6) NULL,
    CONSTRAINT `fk_exam_attempt_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Table: user_answer (Chi tiết đáp án từng câu học viên chọn)
CREATE TABLE IF NOT EXISTS `user_answer` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `exam_attempt_id` BIGINT NOT NULL,
    `question_id` BIGINT NOT NULL,
    `selected_option` VARCHAR(5) NULL, -- 'A', 'B', 'C', 'D' hoặc NULL
    `is_correct` BOOLEAN NULL,
    `time_spent_seconds` INT NULL,
    CONSTRAINT `fk_user_answer_attempt` FOREIGN KEY (`exam_attempt_id`) REFERENCES `exam_attempt` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- SEED SAMPLE DATA (Dữ liệu mẫu kiểm thử)
-- ===================================================================

-- 1. Thêm 1 đề thi mẫu ETS 2024 Test 01
INSERT INTO `exam` (`id`, `code`, `title`, `category`, `duration_minutes`, `total_questions`, `audio_full_url`, `is_published`, `created_at`)
VALUES 
(1, 'ETS_2024_TEST_01', 'ETS TOEIC 2024 - Full Test 01', 'FULL_TEST', 120, 200, 'https://cdn.toeicpro.com/audio/ets2024/test01_full.mp3', TRUE, NOW(6))
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 2. Thêm 7 phần thi chuẩn TOEIC cho đề thi số 1
INSERT INTO `part` (`id`, `exam_id`, `part_number`, `name`, `total_questions`)
VALUES 
(1, 1, 1, 'Part 1: Photographs', 6),
(2, 1, 2, 'Part 2: Question-Response', 25),
(3, 1, 3, 'Part 3: Conversations', 39),
(4, 1, 4, 'Part 4: Short Talks', 30),
(5, 1, 5, 'Part 5: Incomplete Sentences', 30),
(6, 1, 6, 'Part 6: Text Completion', 16),
(7, 1, 7, 'Part 7: Reading Comprehension', 54)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 3. Thêm nhóm câu hỏi mẫu (Question Groups)
INSERT INTO `question_group` (`id`, `part_id`, `passage_text`, `audio_url`, `image_url`)
VALUES 
(1, 3, NULL, 'https://cdn.toeicpro.com/audio/ets2024/p3_q32_34.mp3', NULL),
(2, 7, 'Notice to all employees:\nPlease note that the main office will be closed on Friday, October 15, for annual electrical maintenance. All staff members are requested to work remotely from home. Urgent inquiries should be sent to support@toeicpro.com.', NULL, NULL)
ON DUPLICATE KEY UPDATE `passage_text` = VALUES(`passage_text`);

-- 4. Thêm một số câu hỏi mẫu đại diện cho các Part
INSERT INTO `question` (`id`, `part_id`, `question_group_id`, `question_number`, `content`, `image_url`, `audio_url`, `option_a`, `option_b`, `option_c`, `option_d`, `correct_option`, `explanation`)
VALUES 
-- Part 1: Câu 1
(1, 1, NULL, 1, 'Look at the picture marked number 1 in your test book.', 'https://cdn.toeicpro.com/images/ets2024/p1_q1.jpg', 'https://cdn.toeicpro.com/audio/ets2024/p1_q1.mp3', 'A woman is holding a cup.', 'A woman is typing on a keyboard.', 'A woman is walking outdoors.', 'A woman is driving a car.', 'B', 'Bức tranh mô tả một người phụ nữ đang ngồi gõ bàn phím máy tính.'),

-- Part 2: Câu 7 (Không có D)
(2, 2, NULL, 7, 'Where is the marketing team meeting being held?', NULL, 'https://cdn.toeicpro.com/audio/ets2024/p2_q7.mp3', 'At 2:00 PM tomorrow.', 'In Conference Room B.', 'Yes, I received the email.', NULL, 'B', 'Câu hỏi bắt đầu bằng "Where" (Ở đâu), câu trả lời phù hợp chỉ nơi chốn là "In Conference Room B".'),

-- Part 3: Câu 32 thuộc Group 1
(3, 3, 1, 32, 'Where most likely does the conversation take place?', NULL, NULL, 'At a bank.', 'At a hotel.', 'At a bookstore.', 'At a post office.', 'B', 'Dựa vào câu thoại người phụ nữ nhắc đến việc nhận phòng (check-in) và chìa khóa phòng.'),

-- Part 5: Câu 101
(4, 5, NULL, 101, 'Ms. Laura Jenkins _______ submitted the final quarterly budget report yesterday morning.', NULL, NULL, 'successful', 'successfully', 'succeed', 'success', 'B', 'Vị trí giữa trợ động từ (đã lược bỏ) và động từ chính "submitted" cần một trạng từ (Adverb) để bổ nghĩa cho động từ: "successfully".'),

-- Part 7: Câu 147 thuộc Group 2
(5, 7, 2, 147, 'Why will the main office be closed on October 15?', NULL, NULL, 'For holiday celebration.', 'For electrical maintenance.', 'For staff training.', 'For office relocation.', 'B', 'Trong thông báo nêu rõ: "closed on Friday, October 15, for annual electrical maintenance".')
ON DUPLICATE KEY UPDATE `content` = VALUES(`content`);

-- 5. Thêm 1 lần thi mẫu (Exam Attempt) của học viên
INSERT INTO `exam_attempt` (`id`, `user_id`, `exam_id`, `status`, `listening_score`, `reading_score`, `total_score`, `correct_answers`, `wrong_answers`, `skipped_answers`, `time_spent_seconds`, `started_at`, `completed_at`)
VALUES 
(1, 'd9b1c7a8-1234-4567-89ab-cdef01234567', 1, 'COMPLETED', 415, 385, 800, 162, 35, 3, 7120, DATE_SUB(NOW(6), INTERVAL 2 HOUR), NOW(6))
ON DUPLICATE KEY UPDATE `total_score` = VALUES(`total_score`);

-- 6. Thêm chi tiết câu trả lời của lần thi mẫu
INSERT INTO `user_answer` (`id`, `exam_attempt_id`, `question_id`, `selected_option`, `is_correct`, `time_spent_seconds`)
VALUES 
(1, 1, 1, 'B', TRUE, 12),
(2, 1, 2, 'B', TRUE, 8),
(3, 1, 3, 'A', FALSE, 25),
(4, 1, 4, 'B', TRUE, 15),
(5, 1, 5, 'B', TRUE, 30)
ON DUPLICATE KEY UPDATE `selected_option` = VALUES(`selected_option`);
