package com.toeic.notification.service.impl;

import com.toeic.notification.domain.Notification;
import com.toeic.notification.repository.NotificationRepository;
import com.toeic.notification.service.NotificationService;
import com.toeic.notification.service.dto.NotificationDTO;
import com.toeic.notification.service.mapper.NotificationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.notification.domain.Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        LOG.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public NotificationDTO update(NotificationDTO notificationDTO) {
        LOG.debug("Request to update Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        LOG.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .map(notificationRepository::save)
            .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Notifications");
        return notificationRepository.findAll(pageable).map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        LOG.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> findByUserId(String userId, Pageable pageable) {
        LOG.debug("Request to get Notifications for user : {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByUserId(String userId) {
        LOG.debug("Request to count unread Notifications for user : {}", userId);
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public Optional<NotificationDTO> markAsRead(Long id) {
        LOG.debug("Request to mark Notification as read : {}", id);
        return notificationRepository
            .findById(id)
            .map(notification -> {
                notification.setIsRead(true);
                notification.setReadAt(java.time.Instant.now());
                return notificationRepository.save(notification);
            })
            .map(notificationMapper::toDto);
    }

    @Override
    public int markAllAsRead(String userId) {
        LOG.debug("Request to mark all Notifications as read for user : {}", userId);
        return notificationRepository.markAllAsReadForUser(userId);
    }
}
