package com.toeic.notification.service.impl;

import com.toeic.notification.domain.NotificationLog;
import com.toeic.notification.repository.NotificationLogRepository;
import com.toeic.notification.service.NotificationLogService;
import com.toeic.notification.service.dto.NotificationLogDTO;
import com.toeic.notification.service.mapper.NotificationLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.notification.domain.NotificationLog}.
 */
@Service
@Transactional
public class NotificationLogServiceImpl implements NotificationLogService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationLogServiceImpl.class);

    private final NotificationLogRepository notificationLogRepository;

    private final NotificationLogMapper notificationLogMapper;

    public NotificationLogServiceImpl(NotificationLogRepository notificationLogRepository, NotificationLogMapper notificationLogMapper) {
        this.notificationLogRepository = notificationLogRepository;
        this.notificationLogMapper = notificationLogMapper;
    }

    @Override
    public NotificationLogDTO save(NotificationLogDTO notificationLogDTO) {
        LOG.debug("Request to save NotificationLog : {}", notificationLogDTO);
        NotificationLog notificationLog = notificationLogMapper.toEntity(notificationLogDTO);
        notificationLog = notificationLogRepository.save(notificationLog);
        return notificationLogMapper.toDto(notificationLog);
    }

    @Override
    public NotificationLogDTO update(NotificationLogDTO notificationLogDTO) {
        LOG.debug("Request to update NotificationLog : {}", notificationLogDTO);
        NotificationLog notificationLog = notificationLogMapper.toEntity(notificationLogDTO);
        notificationLog = notificationLogRepository.save(notificationLog);
        return notificationLogMapper.toDto(notificationLog);
    }

    @Override
    public Optional<NotificationLogDTO> partialUpdate(NotificationLogDTO notificationLogDTO) {
        LOG.debug("Request to partially update NotificationLog : {}", notificationLogDTO);

        return notificationLogRepository
            .findById(notificationLogDTO.getId())
            .map(existingNotificationLog -> {
                notificationLogMapper.partialUpdate(existingNotificationLog, notificationLogDTO);

                return existingNotificationLog;
            })
            .map(notificationLogRepository::save)
            .map(notificationLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationLogDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NotificationLogs");
        return notificationLogRepository.findAll(pageable).map(notificationLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationLogDTO> findOne(Long id) {
        LOG.debug("Request to get NotificationLog : {}", id);
        return notificationLogRepository.findById(id).map(notificationLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete NotificationLog : {}", id);
        notificationLogRepository.deleteById(id);
    }
}
