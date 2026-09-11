package com.toeic.payment.service;

import com.toeic.payment.service.dto.PaymentTransactionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.toeic.payment.domain.PaymentTransaction}.
 */
public interface PaymentTransactionService {
    /**
     * Save a paymentTransaction.
     *
     * @param paymentTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    PaymentTransactionDTO save(PaymentTransactionDTO paymentTransactionDTO);

    /**
     * Updates a paymentTransaction.
     *
     * @param paymentTransactionDTO the entity to update.
     * @return the persisted entity.
     */
    PaymentTransactionDTO update(PaymentTransactionDTO paymentTransactionDTO);

    /**
     * Partially updates a paymentTransaction.
     *
     * @param paymentTransactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PaymentTransactionDTO> partialUpdate(PaymentTransactionDTO paymentTransactionDTO);

    /**
     * Get all the paymentTransactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PaymentTransactionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" paymentTransaction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PaymentTransactionDTO> findOne(Long id);

    /**
     * Delete the "id" paymentTransaction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
