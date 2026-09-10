package com.toeic.subscription.service;

import com.toeic.subscription.service.dto.PlanDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.toeic.subscription.domain.Plan}.
 */
public interface PlanService {
    /**
     * Save a plan.
     *
     * @param planDTO the entity to save.
     * @return the persisted entity.
     */
    PlanDTO save(PlanDTO planDTO);

    /**
     * Updates a plan.
     *
     * @param planDTO the entity to update.
     * @return the persisted entity.
     */
    PlanDTO update(PlanDTO planDTO);

    /**
     * Partially updates a plan.
     *
     * @param planDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PlanDTO> partialUpdate(PlanDTO planDTO);

    /**
     * Get all the plans.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PlanDTO> findAll(Pageable pageable);

    /**
     * Get the "id" plan.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PlanDTO> findOne(Long id);

    /**
     * Delete the "id" plan.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
