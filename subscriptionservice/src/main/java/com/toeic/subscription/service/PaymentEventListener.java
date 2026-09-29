package com.toeic.subscription.service;

import com.toeic.subscription.service.dto.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);
    private final SubscriptionService subscriptionService;

    public PaymentEventListener(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "payment-completed-topic", groupId = "subscription-service-group")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        if (event == null || event.orderCode() == null) {
            log.warn("Received empty event or missing orderCode, skipping");
            return;
        }
        log.info("Received PaymentCompletedEvent for user: {}, plan/sub: {}, order: {}", event.userId(), event.subscriptionId(), event.orderCode());

        try {
            subscriptionService.processPaymentCompleted(event);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            log.warn("Payment event already processed for orderCode {} (duplicate event ignored): {}", event.orderCode(), ex.getMessage());
        }
    }
}
