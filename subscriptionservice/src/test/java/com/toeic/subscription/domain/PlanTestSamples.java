package com.toeic.subscription.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PlanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Plan getPlanSample1() {
        return new Plan().id(1L).code("code1").name("name1").durationDays(1).features("features1");
    }

    public static Plan getPlanSample2() {
        return new Plan().id(2L).code("code2").name("name2").durationDays(2).features("features2");
    }

    public static Plan getPlanRandomSampleGenerator() {
        return new Plan()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .durationDays(intCount.incrementAndGet())
            .features(UUID.randomUUID().toString());
    }
}
