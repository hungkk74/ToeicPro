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
import com.toeic.subscription.domain.PaymentTransaction;
import com.toeic.subscription.domain.enumeration.PaymentGateway;
import com.toeic.subscription.domain.enumeration.PaymentStatus;
import com.toeic.subscription.domain.enumeration.SubscriptionStatus;
import com.toeic.subscription.repository.PaymentTransactionRepository;
import com.toeic.subscription.service.dto.PaymentCompletedEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service Implementation for managing {@link com.toeic.subscription.domain.Subscription}.
 */
@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private final SubscriptionRepository subscriptionRepository;

    private final SubscriptionMapper subscriptionMapper;

    private final PaymentTransactionRepository paymentTransactionRepository;

    public SubscriptionServiceImpl(
        SubscriptionRepository subscriptionRepository,
        SubscriptionMapper subscriptionMapper,
        PaymentTransactionRepository paymentTransactionRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionMapper = subscriptionMapper;
        this.paymentTransactionRepository = paymentTransactionRepository;
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

    @Override
    public void activateSubscription(Long userSubscriptionId, String gatewayTransId) {
        LOG.debug("Request to activate Subscription : {}, gatewayTransId : {}", userSubscriptionId, gatewayTransId);
        if (userSubscriptionId == null) {
            LOG.warn("Subscription id is null, skipping activation");
            return;
        }
        if (gatewayTransId != null && !gatewayTransId.isBlank() && paymentTransactionRepository.existsByOrderCode(gatewayTransId)) {
            LOG.warn("Transaction with orderCode {} already processed, skipping duplicate activation (Idempotency Guard)", gatewayTransId);
            return;
        }
        subscriptionRepository
            .findOneWithToOneRelationships(userSubscriptionId)
            .ifPresentOrElse(
                subscription -> {
                    subscription.setStatus(SubscriptionStatus.ACTIVE);
                    Instant now = Instant.now();
                    if (subscription.getStartsAt() == null) {
                        subscription.setStartsAt(now);
                    }
                    int durationDays = 30;
                    if (subscription.getPlan() != null && subscription.getPlan().getDurationDays() != null) {
                        durationDays = subscription.getPlan().getDurationDays();
                    }
                    Instant baseTime = (subscription.getExpiresAt() != null && subscription.getExpiresAt().isAfter(now))
                        ? subscription.getExpiresAt()
                        : now;
                    subscription.setExpiresAt(baseTime.plus(durationDays, ChronoUnit.DAYS));
                    subscriptionRepository.save(subscription);
                    LOG.info("Subscription {} activated successfully for user {}", subscription.getId(), subscription.getUserId());
                },
                () -> LOG.warn("Subscription not found for id: {}", userSubscriptionId)
            );
    }

    @Override
    public void processPaymentCompleted(PaymentCompletedEvent event) {
        if (event == null || event.orderCode() == null || event.orderCode().isBlank()) {
            LOG.warn("PaymentCompletedEvent or orderCode is null, skipping");
            return;
        }
        if (paymentTransactionRepository.existsByOrderCode(event.orderCode())) {
            LOG.warn("Duplicate PaymentCompletedEvent received for orderCode: {}. Skipping activation to ensure idempotency.", event.orderCode());
            return;
        }
        if (event.subscriptionId() == null) {
            LOG.warn("PaymentCompletedEvent has null subscriptionId for orderCode: {}", event.orderCode());
            return;
        }

        subscriptionRepository
            .findOneWithToOneRelationships(event.subscriptionId())
            .ifPresentOrElse(
                subscription -> {
                    subscription.setStatus(SubscriptionStatus.ACTIVE);
                    Instant now = Instant.now();
                    if (subscription.getStartsAt() == null) {
                        subscription.setStartsAt(now);
                    }
                    int durationDays = 30;
                    if (subscription.getPlan() != null && subscription.getPlan().getDurationDays() != null) {
                        durationDays = subscription.getPlan().getDurationDays();
                    }
                    Instant baseTime = (subscription.getExpiresAt() != null && subscription.getExpiresAt().isAfter(now))
                        ? subscription.getExpiresAt()
                        : now;
                    subscription.setExpiresAt(baseTime.plus(durationDays, ChronoUnit.DAYS));
                    subscriptionRepository.save(subscription);

                    PaymentTransaction tx = new PaymentTransaction();
                    tx.setOrderCode(event.orderCode());
                    tx.setGatewayTransId(event.orderCode());
                    tx.setAmount(event.amount() != null ? event.amount() : BigDecimal.ZERO);
                    PaymentGateway gateway = PaymentGateway.VNPAY;
                    if (event.gateway() != null) {
                        try {
                            gateway = PaymentGateway.valueOf(event.gateway().trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            LOG.warn("Unknown gateway '{}' in PaymentCompletedEvent, defaulting to VNPAY", event.gateway());
                        }
                    }
                    tx.setGateway(gateway);
                    tx.setStatus(PaymentStatus.SUCCESS);
                    tx.setCreatedAt(event.paidAt() != null ? event.paidAt() : now);
                    tx.setSubscription(subscription);
                    paymentTransactionRepository.save(tx);

                    LOG.info("Subscription {} activated and PaymentTransaction created for orderCode: {}", subscription.getId(), event.orderCode());
                },
                () -> LOG.warn("Subscription not found for id: {} on orderCode: {}", event.subscriptionId(), event.orderCode())
            );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return subscriptionRepository.hasActiveSubscription(userId, Instant.now());
    }

}
