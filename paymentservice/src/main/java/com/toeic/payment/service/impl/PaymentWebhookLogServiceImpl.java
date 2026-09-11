package com.toeic.payment.service.impl;

import com.toeic.payment.domain.PaymentWebhookLog;
import com.toeic.payment.repository.PaymentWebhookLogRepository;
import com.toeic.payment.service.PaymentWebhookLogService;
import com.toeic.payment.service.dto.PaymentWebhookLogDTO;
import com.toeic.payment.service.mapper.PaymentWebhookLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.payment.domain.PaymentWebhookLog}.
 */
@Service
@Transactional
public class PaymentWebhookLogServiceImpl implements PaymentWebhookLogService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentWebhookLogServiceImpl.class);

    private final PaymentWebhookLogRepository paymentWebhookLogRepository;

    private final PaymentWebhookLogMapper paymentWebhookLogMapper;

    public PaymentWebhookLogServiceImpl(
        PaymentWebhookLogRepository paymentWebhookLogRepository,
        PaymentWebhookLogMapper paymentWebhookLogMapper
    ) {
        this.paymentWebhookLogRepository = paymentWebhookLogRepository;
        this.paymentWebhookLogMapper = paymentWebhookLogMapper;
    }

    @Override
    public PaymentWebhookLogDTO save(PaymentWebhookLogDTO paymentWebhookLogDTO) {
        LOG.debug("Request to save PaymentWebhookLog : {}", paymentWebhookLogDTO);
        PaymentWebhookLog paymentWebhookLog = paymentWebhookLogMapper.toEntity(paymentWebhookLogDTO);
        paymentWebhookLog = paymentWebhookLogRepository.save(paymentWebhookLog);
        return paymentWebhookLogMapper.toDto(paymentWebhookLog);
    }

    @Override
    public PaymentWebhookLogDTO update(PaymentWebhookLogDTO paymentWebhookLogDTO) {
        LOG.debug("Request to update PaymentWebhookLog : {}", paymentWebhookLogDTO);
        PaymentWebhookLog paymentWebhookLog = paymentWebhookLogMapper.toEntity(paymentWebhookLogDTO);
        paymentWebhookLog = paymentWebhookLogRepository.save(paymentWebhookLog);
        return paymentWebhookLogMapper.toDto(paymentWebhookLog);
    }

    @Override
    public Optional<PaymentWebhookLogDTO> partialUpdate(PaymentWebhookLogDTO paymentWebhookLogDTO) {
        LOG.debug("Request to partially update PaymentWebhookLog : {}", paymentWebhookLogDTO);

        return paymentWebhookLogRepository
            .findById(paymentWebhookLogDTO.getId())
            .map(existingPaymentWebhookLog -> {
                paymentWebhookLogMapper.partialUpdate(existingPaymentWebhookLog, paymentWebhookLogDTO);

                return existingPaymentWebhookLog;
            })
            .map(paymentWebhookLogRepository::save)
            .map(paymentWebhookLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentWebhookLogDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PaymentWebhookLogs");
        return paymentWebhookLogRepository.findAll(pageable).map(paymentWebhookLogMapper::toDto);
    }

    public Page<PaymentWebhookLogDTO> findAllWithEagerRelationships(Pageable pageable) {
        return paymentWebhookLogRepository.findAllWithEagerRelationships(pageable).map(paymentWebhookLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentWebhookLogDTO> findOne(Long id) {
        LOG.debug("Request to get PaymentWebhookLog : {}", id);
        return paymentWebhookLogRepository.findOneWithEagerRelationships(id).map(paymentWebhookLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PaymentWebhookLog : {}", id);
        paymentWebhookLogRepository.deleteById(id);
    }
}
