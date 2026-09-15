package com.toeic.course.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Course getCourseSample1() {
        return new Course()
            .id(1L)
            .code("code1")
            .title("title1")
            .slug("slug1")
            .thumbnailUrl("thumbnailUrl1")
            .totalDurationSeconds(1)
            .totalLessons(1);
    }

    public static Course getCourseSample2() {
        return new Course()
            .id(2L)
            .code("code2")
            .title("title2")
            .slug("slug2")
            .thumbnailUrl("thumbnailUrl2")
            .totalDurationSeconds(2)
            .totalLessons(2);
    }

    public static Course getCourseRandomSampleGenerator() {
        return new Course()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .thumbnailUrl(UUID.randomUUID().toString())
            .totalDurationSeconds(intCount.incrementAndGet())
            .totalLessons(intCount.incrementAndGet());
    }
}
