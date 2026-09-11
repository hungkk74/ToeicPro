package com.toeic.payment.repository;

import com.toeic.payment.domain.PaymentWebhookLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PaymentWebhookLog entity.
 */
@Repository
public interface PaymentWebhookLogRepository extends JpaRepository<PaymentWebhookLog, Long> {
    default Optional<PaymentWebhookLog> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PaymentWebhookLog> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PaymentWebhookLog> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select paymentWebhookLog from PaymentWebhookLog paymentWebhookLog left join fetch paymentWebhookLog.paymentTransaction",
        countQuery = "select count(paymentWebhookLog) from PaymentWebhookLog paymentWebhookLog"
    )
    Page<PaymentWebhookLog> findAllWithToOneRelationships(Pageable pageable);

    @Query("select paymentWebhookLog from PaymentWebhookLog paymentWebhookLog left join fetch paymentWebhookLog.paymentTransaction")
    List<PaymentWebhookLog> findAllWithToOneRelationships();

    @Query(
        "select paymentWebhookLog from PaymentWebhookLog paymentWebhookLog left join fetch paymentWebhookLog.paymentTransaction where paymentWebhookLog.id =:id"
    )
    Optional<PaymentWebhookLog> findOneWithToOneRelationships(@Param("id") Long id);
}
