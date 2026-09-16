package com.toeic.subscription.service;

import com.toeic.subscription.service.dto.SubscriptionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.toeic.subscription.domain.Subscription}.
 */
public interface SubscriptionService {
    /**
     * Save a subscription.
     *
     * @param subscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    SubscriptionDTO save(SubscriptionDTO subscriptionDTO);

    /**
     * Updates a subscription.
     *
     * @param subscriptionDTO the entity to update.
     * @return the persisted entity.
     */
    SubscriptionDTO update(SubscriptionDTO subscriptionDTO);

    /**
     * Partially updates a subscription.
     *
     * @param subscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SubscriptionDTO> partialUpdate(SubscriptionDTO subscriptionDTO);

    /**
     * Get all the subscriptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubscriptionDTO> findAll(Pageable pageable);

    /**
     * Get all the subscriptions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubscriptionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" subscription.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SubscriptionDTO> findOne(Long id);

    /**
     * Delete the "id" subscription.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Activate a subscription.
     *
     * @param userSubscriptionId the id of the entity.
     * @param gatewayTransId the transaction or user reference.
     */
    void activateSubscription(Long userSubscriptionId, String gatewayTransId);

    /**
 * Kiểm tra xem người dùng có gói subscription còn hiệu lực hay không.
 *
 * @param userId ID định danh của người dùng
 * @return true nếu có gói ACTIVE và chưa hết hạn
 */
boolean hasActiveSubscription(String userId);


}
