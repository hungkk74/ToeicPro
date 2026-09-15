package com.toeic.notification.service.mapper;

import com.toeic.notification.domain.NotificationLog;
import com.toeic.notification.service.dto.NotificationLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NotificationLog} and its DTO {@link NotificationLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationLogMapper extends EntityMapper<NotificationLogDTO, NotificationLog> {}
