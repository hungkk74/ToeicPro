package com.toeic.course.service;

import com.toeic.course.service.dto.CourseEnrollmentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.toeic.course.domain.CourseEnrollment}.
 */
public interface CourseEnrollmentService {
    /**
     * Save a courseEnrollment.
     *
     * @param courseEnrollmentDTO the entity to save.
     * @return the persisted entity.
     */
    CourseEnrollmentDTO save(CourseEnrollmentDTO courseEnrollmentDTO);

    /**
     * Updates a courseEnrollment.
     *
     * @param courseEnrollmentDTO the entity to update.
     * @return the persisted entity.
     */
    CourseEnrollmentDTO update(CourseEnrollmentDTO courseEnrollmentDTO);

    /**
     * Partially updates a courseEnrollment.
     *
     * @param courseEnrollmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CourseEnrollmentDTO> partialUpdate(CourseEnrollmentDTO courseEnrollmentDTO);

    /**
     * Get all the courseEnrollments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CourseEnrollmentDTO> findAll(Pageable pageable);

    /**
     * Get all the courseEnrollments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CourseEnrollmentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" courseEnrollment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CourseEnrollmentDTO> findOne(Long id);

    /**
     * Delete the "id" courseEnrollment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
