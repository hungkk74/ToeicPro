package com.toeic.exam.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExamAttemptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static ExamAttempt getExamAttemptSample1() {
        return new ExamAttempt()
            .id(1L)
            .userId("userId1")
            .listeningScore(1)
            .readingScore(1)
            .totalScore(1)
            .correctAnswers(1)
            .wrongAnswers(1)
            .skippedAnswers(1)
            .timeSpentSeconds(1);
    }

    public static ExamAttempt getExamAttemptSample2() {
        return new ExamAttempt()
            .id(2L)
            .userId("userId2")
            .listeningScore(2)
            .readingScore(2)
            .totalScore(2)
            .correctAnswers(2)
            .wrongAnswers(2)
            .skippedAnswers(2)
            .timeSpentSeconds(2);
    }

    public static ExamAttempt getExamAttemptRandomSampleGenerator() {
        return new ExamAttempt()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .listeningScore(intCount.incrementAndGet())
            .readingScore(intCount.incrementAndGet())
            .totalScore(intCount.incrementAndGet())
            .correctAnswers(intCount.incrementAndGet())
            .wrongAnswers(intCount.incrementAndGet())
            .skippedAnswers(intCount.incrementAndGet())
            .timeSpentSeconds(intCount.incrementAndGet());
    }
}
