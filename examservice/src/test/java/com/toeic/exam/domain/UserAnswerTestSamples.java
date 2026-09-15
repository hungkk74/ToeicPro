package com.toeic.exam.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserAnswerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static UserAnswer getUserAnswerSample1() {
        return new UserAnswer().id(1L).timeSpentSeconds(1);
    }

    public static UserAnswer getUserAnswerSample2() {
        return new UserAnswer().id(2L).timeSpentSeconds(2);
    }

    public static UserAnswer getUserAnswerRandomSampleGenerator() {
        return new UserAnswer().id(longCount.incrementAndGet()).timeSpentSeconds(intCount.incrementAndGet());
    }
}
