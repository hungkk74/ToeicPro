package com.toeic.exam.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Question getQuestionSample1() {
        return new Question().id(1L).questionNumber(1).imageUrl("imageUrl1").audioUrl("audioUrl1");
    }

    public static Question getQuestionSample2() {
        return new Question().id(2L).questionNumber(2).imageUrl("imageUrl2").audioUrl("audioUrl2");
    }

    public static Question getQuestionRandomSampleGenerator() {
        return new Question()
            .id(longCount.incrementAndGet())
            .questionNumber(intCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString())
            .audioUrl(UUID.randomUUID().toString());
    }
}
