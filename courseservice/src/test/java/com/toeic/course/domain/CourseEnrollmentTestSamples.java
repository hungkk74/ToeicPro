package com.toeic.course.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CourseEnrollmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CourseEnrollment getCourseEnrollmentSample1() {
        return new CourseEnrollment().id(1L).userId("userId1");
    }

    public static CourseEnrollment getCourseEnrollmentSample2() {
        return new CourseEnrollment().id(2L).userId("userId2");
    }

    public static CourseEnrollment getCourseEnrollmentRandomSampleGenerator() {
        return new CourseEnrollment().id(longCount.incrementAndGet()).userId(UUID.randomUUID().toString());
    }
}
