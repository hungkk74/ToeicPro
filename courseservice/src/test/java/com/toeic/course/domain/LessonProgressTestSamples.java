package com.toeic.course.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LessonProgressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static LessonProgress getLessonProgressSample1() {
        return new LessonProgress().id(1L).userId("userId1").lastWatchedSecond(1);
    }

    public static LessonProgress getLessonProgressSample2() {
        return new LessonProgress().id(2L).userId("userId2").lastWatchedSecond(2);
    }

    public static LessonProgress getLessonProgressRandomSampleGenerator() {
        return new LessonProgress()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .lastWatchedSecond(intCount.incrementAndGet());
    }
}
