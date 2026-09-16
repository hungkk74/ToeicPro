package com.toeic.user.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static UserProfile getUserProfileSample1() {
        return new UserProfile().id(1L).userId("userId1").name("name1").email("email1").phone("phone1").address("address1");
    }

    public static UserProfile getUserProfileSample2() {
        return new UserProfile().id(2L).userId("userId2").name("name2").email("email2").phone("phone2").address("address2");
    }

    public static UserProfile getUserProfileRandomSampleGenerator() {
        return new UserProfile()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
