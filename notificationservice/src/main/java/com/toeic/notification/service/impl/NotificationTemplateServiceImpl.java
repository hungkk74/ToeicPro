package com.toeic.notification.service.impl;

import com.toeic.notification.domain.NotificationTemplate;
import com.toeic.notification.repository.NotificationTemplateRepository;
import com.toeic.notification.service.NotificationTemplateService;
import com.toeic.notification.service.dto.NotificationTemplateDTO;
import com.toeic.notification.service.mapper.NotificationTemplateMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.notification.domain.NotificationTemplate}.
 */
@Service
@Transactional
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationTemplateServiceImpl.class);

    private final NotificationTemplateRepository notificationTemplateRepository;

    private final NotificationTemplateMapper notificationTemplateMapper;

    public NotificationTemplateServiceImpl(
        NotificationTemplateRepository notificationTemplateRepository,
        NotificationTemplateMapper notificationTemplateMapper
    ) {
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.notificationTemplateMapper = notificationTemplateMapper;
    }

    @Override
    public NotificationTemplateDTO save(NotificationTemplateDTO notificationTemplateDTO) {
        LOG.debug("Request to save NotificationTemplate : {}", notificationTemplateDTO);
        NotificationTemplate notificationTemplate = notificationTemplateMapper.toEntity(notificationTemplateDTO);
        notificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        return notificationTemplateMapper.toDto(notificationTemplate);
    }

    @Override
    public NotificationTemplateDTO update(NotificationTemplateDTO notificationTemplateDTO) {
        LOG.debug("Request to update NotificationTemplate : {}", notificationTemplateDTO);
        NotificationTemplate notificationTemplate = notificationTemplateMapper.toEntity(notificationTemplateDTO);
        notificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        return notificationTemplateMapper.toDto(notificationTemplate);
    }

    @Override
    public Optional<NotificationTemplateDTO> partialUpdate(NotificationTemplateDTO notificationTemplateDTO) {
        LOG.debug("Request to partially update NotificationTemplate : {}", notificationTemplateDTO);

        return notificationTemplateRepository
            .findById(notificationTemplateDTO.getId())
            .map(existingNotificationTemplate -> {
                notificationTemplateMapper.partialUpdate(existingNotificationTemplate, notificationTemplateDTO);

                return existingNotificationTemplate;
            })
            .map(notificationTemplateRepository::save)
            .map(notificationTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTemplateDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NotificationTemplates");
        return notificationTemplateRepository.findAll(pageable).map(notificationTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationTemplateDTO> findOne(Long id) {
        LOG.debug("Request to get NotificationTemplate : {}", id);
        return notificationTemplateRepository.findById(id).map(notificationTemplateMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete NotificationTemplate : {}", id);
        notificationTemplateRepository.deleteById(id);
    }
}
