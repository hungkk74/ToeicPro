package com.toeic.course.service;

import com.toeic.course.service.dto.LessonProgressDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.toeic.course.domain.LessonProgress}.
 */
public interface LessonProgressService {
    /**
     * Save a lessonProgress.
     *
     * @param lessonProgressDTO the entity to save.
     * @return the persisted entity.
     */
    LessonProgressDTO save(LessonProgressDTO lessonProgressDTO);

    /**
     * Updates a lessonProgress.
     *
     * @param lessonProgressDTO the entity to update.
     * @return the persisted entity.
     */
    LessonProgressDTO update(LessonProgressDTO lessonProgressDTO);

    /**
     * Partially updates a lessonProgress.
     *
     * @param lessonProgressDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LessonProgressDTO> partialUpdate(LessonProgressDTO lessonProgressDTO);

    /**
     * Get all the lessonProgresses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LessonProgressDTO> findAll(Pageable pageable);

    /**
     * Get all the lessonProgresses with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LessonProgressDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" lessonProgress.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LessonProgressDTO> findOne(Long id);

    /**
     * Delete the "id" lessonProgress.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
