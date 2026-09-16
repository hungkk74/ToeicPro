package com.toeic.payment.service;

import com.toeic.payment.config.KafkaTopicConstants;
import com.toeic.payment.domain.PaymentTransaction;
import com.toeic.payment.domain.enumeration.PaymentStatus;
import com.toeic.payment.service.dto.PaymentCompletedEvent;
import com.toeic.payment.service.dto.PaymentTransactionDTO;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service chịu trách nhiệm xuất bản (publish) các sự kiện thanh toán qua Kafka Broker.
 */
@Service
public class PaymentEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Xuất bản sự kiện thanh toán thành công tới Kafka topic.
     *
     * @param event payload sự kiện thanh toán thành công
     */
    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        if (event == null) {
            LOG.warn("PaymentCompletedEvent is null, skip publishing");
            return;
        }

        LOG.info(
            "Publishing PaymentCompletedEvent to topic '{}' - orderCode: {}, userId: {}, subscriptionId: {}, amount: {}",
            KafkaTopicConstants.PAYMENT_COMPLETED_TOPIC,
            event.orderCode(),
            event.userId(),
            event.subscriptionId(),
            event.amount()
        );

        kafkaTemplate
            .send(KafkaTopicConstants.PAYMENT_COMPLETED_TOPIC, event.orderCode(), event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    LOG.info(
                        "Successfully published PaymentCompletedEvent for order: {} to partition: {}, offset: {}",
                        event.orderCode(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                    );
                } else {
                    LOG.error("Failed to publish PaymentCompletedEvent for order: {}", event.orderCode(), ex);
                }
            });
    }

    /**
     * Tiện ích chuyển đổi và xuất bản từ Entity PaymentTransaction nếu trạng thái là SUCCESS.
     *
     * @param transaction đối tượng giao dịch thanh toán
     */
    public void publishIfSuccess(PaymentTransaction transaction) {
        if (transaction != null && transaction.getStatus() == PaymentStatus.SUCCESS) {
            PaymentCompletedEvent event = new PaymentCompletedEvent(
                transaction.getOrderCode(),
                transaction.getUserId(),
                transaction.getSubscriptionId(),
                transaction.getAmount(),
                transaction.getGateway() != null ? transaction.getGateway().name() : null,
                transaction.getPaymentTime() != null ? transaction.getPaymentTime() : Instant.now()
            );
            publishPaymentCompleted(event);
        }
    }

    /**
     * Tiện ích chuyển đổi và xuất bản từ PaymentTransactionDTO nếu trạng thái là SUCCESS.
     *
     * @param dto đối tượng DTO giao dịch thanh toán
     */
    public void publishIfSuccess(PaymentTransactionDTO dto) {
        if (dto != null && dto.getStatus() == PaymentStatus.SUCCESS) {
            PaymentCompletedEvent event = new PaymentCompletedEvent(
                dto.getOrderCode(),
                dto.getUserId(),
                dto.getSubscriptionId(),
                dto.getAmount(),
                dto.getGateway() != null ? dto.getGateway().name() : null,
                dto.getPaymentTime() != null ? dto.getPaymentTime() : Instant.now()
            );
            publishPaymentCompleted(event);
        }
    }
}
