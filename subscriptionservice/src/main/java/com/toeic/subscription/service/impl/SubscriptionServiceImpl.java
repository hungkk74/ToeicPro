package com.toeic.subscription.service.impl;

import com.toeic.subscription.domain.Subscription;
import com.toeic.subscription.repository.SubscriptionRepository;
import com.toeic.subscription.service.SubscriptionService;
import com.toeic.subscription.service.dto.SubscriptionDTO;
import com.toeic.subscription.service.mapper.SubscriptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.subscription.domain.Subscription}.
 */
@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private final SubscriptionRepository subscriptionRepository;

    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, SubscriptionMapper subscriptionMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionMapper = subscriptionMapper;
    }

    @Override
    public SubscriptionDTO save(SubscriptionDTO subscriptionDTO) {
        LOG.debug("Request to save Subscription : {}", subscriptionDTO);
        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO);
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDto(subscription);
    }

    @Override
    public SubscriptionDTO update(SubscriptionDTO subscriptionDTO) {
        LOG.debug("Request to update Subscription : {}", subscriptionDTO);
        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO);
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDto(subscription);
    }

    @Override
    public Optional<SubscriptionDTO> partialUpdate(SubscriptionDTO subscriptionDTO) {
        LOG.debug("Request to partially update Subscription : {}", subscriptionDTO);

        return subscriptionRepository
            .findById(subscriptionDTO.getId())
            .map(existingSubscription -> {
                subscriptionMapper.partialUpdate(existingSubscription, subscriptionDTO);

                return existingSubscription;
            })
            .map(subscriptionRepository::save)
            .map(subscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Subscriptions");
        return subscriptionRepository.findAll(pageable).map(subscriptionMapper::toDto);
    }

    public Page<SubscriptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return subscriptionRepository.findAllWithEagerRelationships(pageable).map(subscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubscriptionDTO> findOne(Long id) {
        LOG.debug("Request to get Subscription : {}", id);
        return subscriptionRepository.findOneWithEagerRelationships(id).map(subscriptionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Subscription : {}", id);
        subscriptionRepository.deleteById(id);
    }
}
