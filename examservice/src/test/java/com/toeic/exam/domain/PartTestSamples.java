package com.toeic.exam.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PartTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Part getPartSample1() {
        return new Part().id(1L).partNumber(1).name("name1").totalQuestions(1);
    }

    public static Part getPartSample2() {
        return new Part().id(2L).partNumber(2).name("name2").totalQuestions(2);
    }

    public static Part getPartRandomSampleGenerator() {
        return new Part()
            .id(longCount.incrementAndGet())
            .partNumber(intCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .totalQuestions(intCount.incrementAndGet());
    }
}
