-- ===================================================================
-- DATABASE INITIALIZATION SCRIPT FOR NOTIFICATIONSERVICE
-- Toeic Pro Microservices Ecosystem
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `notificationservice` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `notificationservice`;

-- 1. Table: notification_template (Quản lý mẫu thông báo đa kênh)
CREATE TABLE IF NOT EXISTS `notification_template` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(100) NOT NULL UNIQUE,
    `channel` VARCHAR(50) NOT NULL, -- 'EMAIL', 'PUSH_NOTIFICATION', 'IN_APP', 'SMS'
    `subject` VARCHAR(255) NOT NULL,
    `body_template` LONGTEXT NOT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table: notification (Hòm thư thông báo trong ứng dụng cho người dùng)
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(36) NOT NULL, -- Keycloak UUID
    `title` VARCHAR(255) NOT NULL,
    `content` LONGTEXT NOT NULL,
    `type` VARCHAR(50) NOT NULL, -- 'PAYMENT_SUCCESS', 'EXAM_RESULT', 'COURSE_ENROLLED', 'SYSTEM_ANNOUNCEMENT', 'STUDY_REMINDER'
    `target_url` VARCHAR(500),
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `read_at` DATETIME(6) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table: notification_log (Nhật ký gửi thông báo kênh ngoài)
CREATE TABLE IF NOT EXISTS `notification_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(36) NOT NULL,
    `recipient` VARCHAR(255) NOT NULL,
    `channel` VARCHAR(50) NOT NULL,
    `status` VARCHAR(50) NOT NULL, -- 'PENDING', 'SENT', 'FAILED'
    `error_message` LONGTEXT,
    `retry_count` INT DEFAULT 0,
    `sent_at` DATETIME(6) NULL,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- SEED SAMPLE NOTIFICATION TEMPLATES
-- ===================================================================

INSERT INTO `notification_template` (`id`, `code`, `channel`, `subject`, `body_template`, `is_active`, `created_at`, `updated_at`)
VALUES 
(1, 'EMAIL_PAYMENT_SUCCESS', 'EMAIL', 'Xác nhận thanh toán thành công đơn hàng TOEIC Pro #{{transactionId}}', 
'<div style="font-family: sans-serif; padding: 20px;">
  <h2 style="color: #4F46E5;">Cảm ơn bạn đã tin tưởng TOEIC Pro!</h2>
  <p>Xin chào <strong>{{studentName}}</strong>,</p>
  <p>Đơn hàng <strong>#{{transactionId}}</strong> của bạn đã được thanh toán thành công.</p>
  <ul>
    <li>Sản phẩm: <strong>{{itemName}}</strong></li>
    <li>Số tiền: <strong>{{amount}} VNĐ</strong></li>
    <li>Thời gian: <strong>{{paymentTime}}</strong></li>
  </ul>
  <p>Quyền lợi học tập / thi thử của bạn đã được kích hoạt ngay lập tức trên hệ thống.</p>
  <a href="{{accessUrl}}" style="background: #4F46E5; color: white; padding: 10px 20px; text-decoration: none; border-radius: 6px; display: inline-block;">Vào học ngay</a>
</div>', TRUE, NOW(6), NOW(6)),

(2, 'EMAIL_EXAM_RESULT', 'EMAIL', 'Kết quả bài thi thử TOEIC của bạn: {{score}}/990 điểm', 
'<div style="font-family: sans-serif; padding: 20px;">
  <h2 style="color: #059669;">Báo cáo kết quả thi TOEIC</h2>
  <p>Xin chào <strong>{{studentName}}</strong>,</p>
  <p>Chúc mừng bạn đã hoàn thành bài thi <strong>{{examTitle}}</strong>.</p>
  <div style="background: #F3F4F6; padding: 15px; border-radius: 8px; margin: 15px 0;">
    <h3 style="margin: 0; color: #1F2937;">Tổng điểm: <span style="color: #059669; font-size: 24px;">{{score}}/990</span></h3>
    <p style="margin: 5px 0;">Listening: <strong>{{listeningScore}}/495</strong> (Đúng {{listeningCorrect}}/100)</p>
    <p style="margin: 5px 0;">Reading: <strong>{{readingScore}}/495</strong> (Đúng {{readingCorrect}}/100)</p>
  </div>
  <p>Xem lại chi tiết đáp án và lời giải giải thích tại liên kết bên dưới:</p>
  <a href="{{reviewUrl}}" style="background: #059669; color: white; padding: 10px 20px; text-decoration: none; border-radius: 6px; display: inline-block;">Xem lời giải chi tiết</a>
</div>', TRUE, NOW(6), NOW(6)),

(3, 'PUSH_STUDY_REMINDER', 'PUSH_NOTIFICATION', 'Đã đến giờ luyện thi TOEIC hôm nay! 🎯', 
'Dành 15 phút mỗi ngày làm 1 bài Part 5 để duy trì phản xạ từ vựng và ngữ pháp. Bấm để luyện ngay!', TRUE, NOW(6), NOW(6)),

(4, 'IN_APP_WELCOME', 'IN_APP', 'Chào mừng bạn đến với Hệ sinh thái TOEIC Pro! 🚀', 
'Khám phá ngân hàng đề thi chuẩn ETS 2024 và kho bài giảng chuyên sâu ngay hôm nay. Chúc bạn sớm đạt mục tiêu!', TRUE, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE `subject` = VALUES(`subject`), `body_template` = VALUES(`body_template`);
