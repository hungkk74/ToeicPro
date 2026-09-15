package com.toeic.notification.repository;

import com.toeic.notification.domain.NotificationTemplate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NotificationTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {}
