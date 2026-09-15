package com.toeic.notification.repository;

import com.toeic.notification.domain.NotificationLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NotificationLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {}
