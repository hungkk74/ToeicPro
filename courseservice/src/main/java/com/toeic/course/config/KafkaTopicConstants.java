package com.toeic.course.config;

/**
 * Hằng số định danh các Kafka Topic cho courseservice.
 * Các topic được quản lý và khởi tạo tập trung từ hạ tầng (Docker Compose / GitOps).
 */
public final class KafkaTopicConstants {

    private KafkaTopicConstants() {}

    /**
     * Topic nhận/gửi sự kiện học viên hoàn thành bài học.
     */
    public static final String LESSON_COMPLETED_TOPIC = "lesson-completed-topic";
}
