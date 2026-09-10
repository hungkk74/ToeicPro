package com.toeic.subscription;

import com.toeic.subscription.config.AsyncSyncConfiguration;
import com.toeic.subscription.config.DatabaseTestcontainer;
import com.toeic.subscription.config.RedisTestContainer;
import com.toeic.subscription.config.TestSecurityConfiguration;
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
        SubscriptionserviceApp.class,
        AsyncSyncConfiguration.class,
        TestSecurityConfiguration.class,
        com.toeic.subscription.config.JacksonHibernateConfiguration.class,
        DatabaseTestcontainer.class,
        RedisTestContainer.class,
    }
)
public @interface IntegrationTest {}
