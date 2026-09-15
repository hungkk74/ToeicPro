package com.toeic.exam.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Exam getExamSample1() {
        return new Exam().id(1L).code("code1").title("title1").durationMinutes(1).totalQuestions(1).audioFullUrl("audioFullUrl1");
    }

    public static Exam getExamSample2() {
        return new Exam().id(2L).code("code2").title("title2").durationMinutes(2).totalQuestions(2).audioFullUrl("audioFullUrl2");
    }

    public static Exam getExamRandomSampleGenerator() {
        return new Exam()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .durationMinutes(intCount.incrementAndGet())
            .totalQuestions(intCount.incrementAndGet())
            .audioFullUrl(UUID.randomUUID().toString());
    }
}
