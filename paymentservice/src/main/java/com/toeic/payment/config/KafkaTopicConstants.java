package com.toeic.payment.config;

/**
 * Hằng số định danh các Kafka Topic cho paymentservice.
 * Các topic được quản lý và khởi tạo tập trung từ hạ tầng (Docker Compose / GitOps).
 */
public final class KafkaTopicConstants {

    private KafkaTopicConstants() {}

    /**
     * Topic xuất bản sự kiện thanh toán thành công.
     */
    public static final String PAYMENT_COMPLETED_TOPIC = "payment-completed-topic";

    /**
     * Dead Letter Topic xử lý các sự kiện thanh toán lỗi không phục hồi được.
     */
    public static final String PAYMENT_COMPLETED_DLT_TOPIC = "payment-completed-topic.DLT";
}
