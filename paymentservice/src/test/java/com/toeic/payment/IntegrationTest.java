package com.toeic.payment;

import com.toeic.payment.config.AsyncSyncConfiguration;
import com.toeic.payment.config.DatabaseTestcontainer;
import com.toeic.payment.config.RedisTestContainer;
import com.toeic.payment.config.TestSecurityConfiguration;
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
        PaymentserviceApp.class,
        AsyncSyncConfiguration.class,
        TestSecurityConfiguration.class,
        com.toeic.payment.config.JacksonHibernateConfiguration.class,
        DatabaseTestcontainer.class,
        RedisTestContainer.class,
    }
)
public @interface IntegrationTest {}
