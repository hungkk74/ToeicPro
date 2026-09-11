package com.toeic.subscription.service;

import com.toeic.subscription.service.dto.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);
    private final SubscriptionService subscriptionService;

    public PaymentEventListener(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @KafkaListener(topics = "payment-completed-topic", groupId = "subscription-service-group")
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent for user: {}, plan/sub: {}", event.userId(), event.subscriptionId());

        // Cập nhật trạng thái subscription sang ACTIVE
        subscriptionService.activateSubscription(event.subscriptionId(), event.userId());
    }
}
