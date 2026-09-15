package com.toeic.course.service;

import com.toeic.course.service.dto.ChapterDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.toeic.course.domain.Chapter}.
 */
public interface ChapterService {
    /**
     * Save a chapter.
     *
     * @param chapterDTO the entity to save.
     * @return the persisted entity.
     */
    ChapterDTO save(ChapterDTO chapterDTO);

    /**
     * Updates a chapter.
     *
     * @param chapterDTO the entity to update.
     * @return the persisted entity.
     */
    ChapterDTO update(ChapterDTO chapterDTO);

    /**
     * Partially updates a chapter.
     *
     * @param chapterDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ChapterDTO> partialUpdate(ChapterDTO chapterDTO);

    /**
     * Get all the chapters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ChapterDTO> findAll(Pageable pageable);

    /**
     * Get all the chapters with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ChapterDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" chapter.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ChapterDTO> findOne(Long id);

    /**
     * Delete the "id" chapter.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
