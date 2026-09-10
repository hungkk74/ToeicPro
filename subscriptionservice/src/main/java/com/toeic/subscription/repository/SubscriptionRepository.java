package com.toeic.subscription.repository;

import com.toeic.subscription.domain.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Subscription entity.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    default Optional<Subscription> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Subscription> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Subscription> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select subscription from Subscription subscription left join fetch subscription.plan",
        countQuery = "select count(subscription) from Subscription subscription"
    )
    Page<Subscription> findAllWithToOneRelationships(Pageable pageable);

    @Query("select subscription from Subscription subscription left join fetch subscription.plan")
    List<Subscription> findAllWithToOneRelationships();

    @Query("select subscription from Subscription subscription left join fetch subscription.plan where subscription.id =:id")
    Optional<Subscription> findOneWithToOneRelationships(@Param("id") Long id);
}
