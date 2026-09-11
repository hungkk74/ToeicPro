package com.toeic.payment.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentWebhookLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static PaymentWebhookLog getPaymentWebhookLogSample1() {
        return new PaymentWebhookLog().id(1L).orderCode("orderCode1").signature("signature1").responseCode("responseCode1");
    }

    public static PaymentWebhookLog getPaymentWebhookLogSample2() {
        return new PaymentWebhookLog().id(2L).orderCode("orderCode2").signature("signature2").responseCode("responseCode2");
    }

    public static PaymentWebhookLog getPaymentWebhookLogRandomSampleGenerator() {
        return new PaymentWebhookLog()
            .id(longCount.incrementAndGet())
            .orderCode(UUID.randomUUID().toString())
            .signature(UUID.randomUUID().toString())
            .responseCode(UUID.randomUUID().toString());
    }
}
