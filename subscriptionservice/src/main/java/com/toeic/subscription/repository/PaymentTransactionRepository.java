package com.toeic.subscription.repository;

import com.toeic.subscription.domain.PaymentTransaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PaymentTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    boolean existsByOrderCode(String orderCode);

    Optional<PaymentTransaction> findByOrderCode(String orderCode);
}
