package com.toeic.exam.service;

import com.toeic.exam.service.dto.QuestionGroupDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.toeic.exam.domain.QuestionGroup}.
 */
public interface QuestionGroupService {
    /**
     * Save a questionGroup.
     *
     * @param questionGroupDTO the entity to save.
     * @return the persisted entity.
     */
    QuestionGroupDTO save(QuestionGroupDTO questionGroupDTO);

    /**
     * Updates a questionGroup.
     *
     * @param questionGroupDTO the entity to update.
     * @return the persisted entity.
     */
    QuestionGroupDTO update(QuestionGroupDTO questionGroupDTO);

    /**
     * Partially updates a questionGroup.
     *
     * @param questionGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<QuestionGroupDTO> partialUpdate(QuestionGroupDTO questionGroupDTO);

    /**
     * Get all the questionGroups.
     *
     * @return the list of entities.
     */
    List<QuestionGroupDTO> findAll();

    /**
     * Get all the questionGroups with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    List<QuestionGroupDTO> findAllWithEagerRelationships();

    /**
     * Get the "id" questionGroup.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<QuestionGroupDTO> findOne(Long id);

    /**
     * Delete the "id" questionGroup.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
