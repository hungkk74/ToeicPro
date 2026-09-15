package com.toeic.notification.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static NotificationLog getNotificationLogSample1() {
        return new NotificationLog().id(1L).userId("userId1").recipient("recipient1").retryCount(1);
    }

    public static NotificationLog getNotificationLogSample2() {
        return new NotificationLog().id(2L).userId("userId2").recipient("recipient2").retryCount(2);
    }

    public static NotificationLog getNotificationLogRandomSampleGenerator() {
        return new NotificationLog()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .recipient(UUID.randomUUID().toString())
            .retryCount(intCount.incrementAndGet());
    }
}
