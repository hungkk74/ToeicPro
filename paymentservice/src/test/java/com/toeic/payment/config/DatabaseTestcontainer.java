package com.toeic.payment.config;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@TestConfiguration(proxyBeanMethods = false)
public class DatabaseTestcontainer {

    private static final MySQLContainer<?> DATABASE_CONTAINER = (MySQLContainer) new MySQLContainer<>("mysql:26.7.0")
        .withDatabaseName("paymentservice")
        .withConfigurationOverride("conf/mysql")
        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(DatabaseTestcontainer.class)))
        .withReuse(true);

    @Bean
    @ServiceConnection
    MySQLContainer<?> databaseContainer() {
        return DATABASE_CONTAINER;
    }
}
