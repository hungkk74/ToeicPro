package com.toeic.exam.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionGroupTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static QuestionGroup getQuestionGroupSample1() {
        return new QuestionGroup().id(1L).audioUrl("audioUrl1").imageUrl("imageUrl1");
    }

    public static QuestionGroup getQuestionGroupSample2() {
        return new QuestionGroup().id(2L).audioUrl("audioUrl2").imageUrl("imageUrl2");
    }

    public static QuestionGroup getQuestionGroupRandomSampleGenerator() {
        return new QuestionGroup()
            .id(longCount.incrementAndGet())
            .audioUrl(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString());
    }
}
