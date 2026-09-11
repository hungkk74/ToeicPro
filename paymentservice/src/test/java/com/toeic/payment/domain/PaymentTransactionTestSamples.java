package com.toeic.payment.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static PaymentTransaction getPaymentTransactionSample1() {
        return new PaymentTransaction()
            .id(1L)
            .orderCode("orderCode1")
            .userId("userId1")
            .subscriptionId(1L)
            .currency("currency1")
            .gatewayTransId("gatewayTransId1")
            .paymentUrl("paymentUrl1")
            .ipAddress("ipAddress1");
    }

    public static PaymentTransaction getPaymentTransactionSample2() {
        return new PaymentTransaction()
            .id(2L)
            .orderCode("orderCode2")
            .userId("userId2")
            .subscriptionId(2L)
            .currency("currency2")
            .gatewayTransId("gatewayTransId2")
            .paymentUrl("paymentUrl2")
            .ipAddress("ipAddress2");
    }

    public static PaymentTransaction getPaymentTransactionRandomSampleGenerator() {
        return new PaymentTransaction()
            .id(longCount.incrementAndGet())
            .orderCode(UUID.randomUUID().toString())
            .userId(UUID.randomUUID().toString())
            .subscriptionId(longCount.incrementAndGet())
            .currency(UUID.randomUUID().toString())
            .gatewayTransId(UUID.randomUUID().toString())
            .paymentUrl(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString());
    }
}
