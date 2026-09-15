package com.toeic.exam.service;

import com.toeic.exam.service.dto.PartDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.toeic.exam.domain.Part}.
 */
public interface PartService {
    /**
     * Save a part.
     *
     * @param partDTO the entity to save.
     * @return the persisted entity.
     */
    PartDTO save(PartDTO partDTO);

    /**
     * Updates a part.
     *
     * @param partDTO the entity to update.
     * @return the persisted entity.
     */
    PartDTO update(PartDTO partDTO);

    /**
     * Partially updates a part.
     *
     * @param partDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PartDTO> partialUpdate(PartDTO partDTO);

    /**
     * Get all the parts.
     *
     * @return the list of entities.
     */
    List<PartDTO> findAll();

    /**
     * Get all the parts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    List<PartDTO> findAllWithEagerRelationships();

    /**
     * Get the "id" part.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PartDTO> findOne(Long id);

    /**
     * Delete the "id" part.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
