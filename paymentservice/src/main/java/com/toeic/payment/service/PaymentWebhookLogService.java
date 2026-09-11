package com.toeic.payment.service;

import com.toeic.payment.service.dto.PaymentWebhookLogDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.toeic.payment.domain.PaymentWebhookLog}.
 */
public interface PaymentWebhookLogService {
    /**
     * Save a paymentWebhookLog.
     *
     * @param paymentWebhookLogDTO the entity to save.
     * @return the persisted entity.
     */
    PaymentWebhookLogDTO save(PaymentWebhookLogDTO paymentWebhookLogDTO);

    /**
     * Updates a paymentWebhookLog.
     *
     * @param paymentWebhookLogDTO the entity to update.
     * @return the persisted entity.
     */
    PaymentWebhookLogDTO update(PaymentWebhookLogDTO paymentWebhookLogDTO);

    /**
     * Partially updates a paymentWebhookLog.
     *
     * @param paymentWebhookLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PaymentWebhookLogDTO> partialUpdate(PaymentWebhookLogDTO paymentWebhookLogDTO);

    /**
     * Get all the paymentWebhookLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PaymentWebhookLogDTO> findAll(Pageable pageable);

    /**
     * Get all the paymentWebhookLogs with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PaymentWebhookLogDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" paymentWebhookLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PaymentWebhookLogDTO> findOne(Long id);

    /**
     * Delete the "id" paymentWebhookLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
