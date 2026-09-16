package com.toeic.user;

import com.toeic.user.config.AsyncSyncConfiguration;
import com.toeic.user.config.EmbeddedSQL;
import com.toeic.user.config.RedisTestContainer;
import com.toeic.user.config.TestSecurityConfiguration;
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
        UserserviceApp.class,
        AsyncSyncConfiguration.class,
        TestSecurityConfiguration.class,
        com.toeic.user.config.JacksonHibernateConfiguration.class,
        RedisTestContainer.class,
    }
)
@EmbeddedSQL
public @interface IntegrationTest {}
