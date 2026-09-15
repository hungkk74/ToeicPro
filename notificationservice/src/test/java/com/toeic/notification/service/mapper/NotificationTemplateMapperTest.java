package com.toeic.notification.service.mapper;

import static com.toeic.notification.domain.NotificationTemplateAsserts.*;
import static com.toeic.notification.domain.NotificationTemplateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationTemplateMapperTest {

    private NotificationTemplateMapper notificationTemplateMapper;

    @BeforeEach
    void setUp() {
        notificationTemplateMapper = new NotificationTemplateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotificationTemplateSample1();
        var actual = notificationTemplateMapper.toEntity(notificationTemplateMapper.toDto(expected));
        assertNotificationTemplateAllPropertiesEquals(expected, actual);
    }
}
