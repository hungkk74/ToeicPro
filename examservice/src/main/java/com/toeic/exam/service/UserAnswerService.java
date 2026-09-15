package com.toeic.exam.service;

import com.toeic.exam.service.dto.UserAnswerDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.toeic.exam.domain.UserAnswer}.
 */
public interface UserAnswerService {
    /**
     * Save a userAnswer.
     *
     * @param userAnswerDTO the entity to save.
     * @return the persisted entity.
     */
    UserAnswerDTO save(UserAnswerDTO userAnswerDTO);

    /**
     * Updates a userAnswer.
     *
     * @param userAnswerDTO the entity to update.
     * @return the persisted entity.
     */
    UserAnswerDTO update(UserAnswerDTO userAnswerDTO);

    /**
     * Partially updates a userAnswer.
     *
     * @param userAnswerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserAnswerDTO> partialUpdate(UserAnswerDTO userAnswerDTO);

    /**
     * Get all the userAnswers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserAnswerDTO> findAll(Pageable pageable);

    /**
     * Get all the userAnswers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UserAnswerDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" userAnswer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserAnswerDTO> findOne(Long id);

    /**
     * Delete the "id" userAnswer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
