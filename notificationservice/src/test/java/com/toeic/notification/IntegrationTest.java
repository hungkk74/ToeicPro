package com.toeic.notification;

import com.toeic.notification.config.AsyncSyncConfiguration;
import com.toeic.notification.config.DatabaseTestcontainer;
import com.toeic.notification.config.RedisTestContainer;
import com.toeic.notification.config.TestSecurityConfiguration;
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
        NotificationserviceApp.class,
        AsyncSyncConfiguration.class,
        TestSecurityConfiguration.class,
        com.toeic.notification.config.JacksonHibernateConfiguration.class,
        DatabaseTestcontainer.class,
        RedisTestContainer.class,
    }
)
public @interface IntegrationTest {}
