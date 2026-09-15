package com.toeic.notification.service.mapper;

import com.toeic.notification.domain.NotificationTemplate;
import com.toeic.notification.service.dto.NotificationTemplateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NotificationTemplate} and its DTO {@link NotificationTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationTemplateMapper extends EntityMapper<NotificationTemplateDTO, NotificationTemplate> {}
