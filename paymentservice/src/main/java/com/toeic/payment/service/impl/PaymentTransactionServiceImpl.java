package com.toeic.payment.service.impl;

import com.toeic.payment.domain.PaymentTransaction;
import com.toeic.payment.repository.PaymentTransactionRepository;
import com.toeic.payment.service.PaymentTransactionService;
import com.toeic.payment.service.dto.PaymentTransactionDTO;
import com.toeic.payment.service.mapper.PaymentTransactionMapper;
import java.util.Optional;
import com.toeic.payment.domain.enumeration.PaymentStatus;
import com.toeic.payment.service.PaymentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.payment.domain.PaymentTransaction}.
 */
@Service
@Transactional
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentTransactionServiceImpl.class);

    private final PaymentTransactionRepository paymentTransactionRepository;

    private final PaymentTransactionMapper paymentTransactionMapper;

    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentTransactionServiceImpl(
        PaymentTransactionRepository paymentTransactionRepository,
        PaymentTransactionMapper paymentTransactionMapper,
        PaymentEventPublisher paymentEventPublisher
    ) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentTransactionMapper = paymentTransactionMapper;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Override
    public PaymentTransactionDTO save(PaymentTransactionDTO paymentTransactionDTO) {
        LOG.debug("Request to save PaymentTransaction : {}", paymentTransactionDTO);
        PaymentTransaction paymentTransaction = paymentTransactionMapper.toEntity(paymentTransactionDTO);
        paymentTransaction = paymentTransactionRepository.save(paymentTransaction);
        paymentEventPublisher.publishIfSuccess(paymentTransaction);
        return paymentTransactionMapper.toDto(paymentTransaction);
    }

    @Override
    public PaymentTransactionDTO update(PaymentTransactionDTO paymentTransactionDTO) {
        LOG.debug("Request to update PaymentTransaction : {}", paymentTransactionDTO);
        PaymentStatus oldStatus = paymentTransactionRepository
            .findById(paymentTransactionDTO.getId())
            .map(PaymentTransaction::getStatus)
            .orElse(null);

        PaymentTransaction paymentTransaction = paymentTransactionMapper.toEntity(paymentTransactionDTO);
        paymentTransaction = paymentTransactionRepository.save(paymentTransaction);

        if (paymentTransaction.getStatus() == PaymentStatus.SUCCESS && oldStatus != PaymentStatus.SUCCESS) {
            paymentEventPublisher.publishIfSuccess(paymentTransaction);
        }
        return paymentTransactionMapper.toDto(paymentTransaction);
    }

    @Override
    public Optional<PaymentTransactionDTO> partialUpdate(PaymentTransactionDTO paymentTransactionDTO) {
        LOG.debug("Request to partially update PaymentTransaction : {}", paymentTransactionDTO);

        return paymentTransactionRepository
            .findById(paymentTransactionDTO.getId())
            .map(existingPaymentTransaction -> {
                PaymentStatus oldStatus = existingPaymentTransaction.getStatus();
                paymentTransactionMapper.partialUpdate(existingPaymentTransaction, paymentTransactionDTO);
                PaymentTransaction saved = paymentTransactionRepository.save(existingPaymentTransaction);
                if (saved.getStatus() == PaymentStatus.SUCCESS && oldStatus != PaymentStatus.SUCCESS) {
                    paymentEventPublisher.publishIfSuccess(saved);
                }
                return saved;
            })
            .map(paymentTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentTransactionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PaymentTransactions");
        return paymentTransactionRepository.findAll(pageable).map(paymentTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentTransactionDTO> findOne(Long id) {
        LOG.debug("Request to get PaymentTransaction : {}", id);
        return paymentTransactionRepository.findById(id).map(paymentTransactionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PaymentTransaction : {}", id);
        paymentTransactionRepository.deleteById(id);
    }
}
