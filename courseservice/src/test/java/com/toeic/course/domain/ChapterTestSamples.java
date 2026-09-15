package com.toeic.course.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChapterTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Chapter getChapterSample1() {
        return new Chapter().id(1L).title("title1").sortOrder(1);
    }

    public static Chapter getChapterSample2() {
        return new Chapter().id(2L).title("title2").sortOrder(2);
    }

    public static Chapter getChapterRandomSampleGenerator() {
        return new Chapter().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).sortOrder(intCount.incrementAndGet());
    }
}
