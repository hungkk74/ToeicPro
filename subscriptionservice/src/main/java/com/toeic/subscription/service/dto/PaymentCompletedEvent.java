package com.toeic.subscription.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCompletedEvent(
    String orderCode,
    String userId,
    Long subscriptionId,
    BigDecimal amount,
    String gateway,
    Instant paidAt
) implements Serializable {}
