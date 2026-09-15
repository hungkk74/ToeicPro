package com.toeic.course.service.impl;

import com.toeic.course.domain.CourseEnrollment;
import com.toeic.course.repository.CourseEnrollmentRepository;
import com.toeic.course.service.CourseEnrollmentService;
import com.toeic.course.service.dto.CourseEnrollmentDTO;
import com.toeic.course.service.mapper.CourseEnrollmentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.course.domain.CourseEnrollment}.
 */
@Service
@Transactional
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseEnrollmentServiceImpl.class);

    private final CourseEnrollmentRepository courseEnrollmentRepository;

    private final CourseEnrollmentMapper courseEnrollmentMapper;

    public CourseEnrollmentServiceImpl(
        CourseEnrollmentRepository courseEnrollmentRepository,
        CourseEnrollmentMapper courseEnrollmentMapper
    ) {
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.courseEnrollmentMapper = courseEnrollmentMapper;
    }

    @Override
    public CourseEnrollmentDTO save(CourseEnrollmentDTO courseEnrollmentDTO) {
        LOG.debug("Request to save CourseEnrollment : {}", courseEnrollmentDTO);
        CourseEnrollment courseEnrollment = courseEnrollmentMapper.toEntity(courseEnrollmentDTO);
        courseEnrollment = courseEnrollmentRepository.save(courseEnrollment);
        return courseEnrollmentMapper.toDto(courseEnrollment);
    }

    @Override
    public CourseEnrollmentDTO update(CourseEnrollmentDTO courseEnrollmentDTO) {
        LOG.debug("Request to update CourseEnrollment : {}", courseEnrollmentDTO);
        CourseEnrollment courseEnrollment = courseEnrollmentMapper.toEntity(courseEnrollmentDTO);
        courseEnrollment = courseEnrollmentRepository.save(courseEnrollment);
        return courseEnrollmentMapper.toDto(courseEnrollment);
    }

    @Override
    public Optional<CourseEnrollmentDTO> partialUpdate(CourseEnrollmentDTO courseEnrollmentDTO) {
        LOG.debug("Request to partially update CourseEnrollment : {}", courseEnrollmentDTO);

        return courseEnrollmentRepository
            .findById(courseEnrollmentDTO.getId())
            .map(existingCourseEnrollment -> {
                courseEnrollmentMapper.partialUpdate(existingCourseEnrollment, courseEnrollmentDTO);

                return existingCourseEnrollment;
            })
            .map(courseEnrollmentRepository::save)
            .map(courseEnrollmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseEnrollmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CourseEnrollments");
        return courseEnrollmentRepository.findAll(pageable).map(courseEnrollmentMapper::toDto);
    }

    public Page<CourseEnrollmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseEnrollmentRepository.findAllWithEagerRelationships(pageable).map(courseEnrollmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseEnrollmentDTO> findOne(Long id) {
        LOG.debug("Request to get CourseEnrollment : {}", id);
        return courseEnrollmentRepository.findOneWithEagerRelationships(id).map(courseEnrollmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CourseEnrollment : {}", id);
        courseEnrollmentRepository.deleteById(id);
    }
}
