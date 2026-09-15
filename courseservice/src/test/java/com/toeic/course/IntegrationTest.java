package com.toeic.course;

import com.toeic.course.config.AsyncSyncConfiguration;
import com.toeic.course.config.DatabaseTestcontainer;
import com.toeic.course.config.RedisTestContainer;
import com.toeic.course.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        CourseserviceApp.class,
        AsyncSyncConfiguration.class,
        TestSecurityConfiguration.class,
        com.toeic.course.config.JacksonHibernateConfiguration.class,
        DatabaseTestcontainer.class,
        RedisTestContainer.class,
    }
)
public @interface IntegrationTest {}
