package com.toeic.exam.config;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@TestConfiguration(proxyBeanMethods = false)
public class RedisTestContainer {

    private static final GenericContainer REDIS_CONTAINER = new GenericContainer("redis:8.10.1")
        .withExposedPorts(6379)
        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(RedisTestContainer.class)))
        .withReuse(true);

    @Bean
    GenericContainer redisContainer() {
        return REDIS_CONTAINER;
    }

    @Bean
    DynamicPropertyRegistrar redisProperties(GenericContainer redisContainer) {
        return registry ->
            registry.add(
                "jhipster.cache.redis.server",
                () -> "redis://" + redisContainer.getContainerIpAddress() + ":" + redisContainer.getMappedPort(6379)
            );
    }
}
