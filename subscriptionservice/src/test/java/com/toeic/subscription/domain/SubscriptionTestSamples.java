package com.toeic.subscription.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubscriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Subscription getSubscriptionSample1() {
        return new Subscription().id(1L).userId("userId1");
    }

    public static Subscription getSubscriptionSample2() {
        return new Subscription().id(2L).userId("userId2");
    }

    public static Subscription getSubscriptionRandomSampleGenerator() {
        return new Subscription().id(longCount.incrementAndGet()).userId(UUID.randomUUID().toString());
    }
}
