package com.toeic.exam.config;

/**
 * Hằng số định danh các Kafka Topic cho examservice.
 * Các topic được quản lý và khởi tạo tập trung từ hạ tầng (Docker Compose / GitOps).
 */
public final class KafkaTopicConstants {

    private KafkaTopicConstants() {}

    /**
     * Topic nhận/gửi sự kiện học viên hoàn thành bài thi TOEIC.
     */
    public static final String EXAM_FINISHED_TOPIC = "exam-finished-topic";
}
