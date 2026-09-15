package com.toeic.course.service.impl;

import com.toeic.course.domain.LessonProgress;
import com.toeic.course.repository.LessonProgressRepository;
import com.toeic.course.service.LessonProgressService;
import com.toeic.course.service.dto.LessonProgressDTO;
import com.toeic.course.service.mapper.LessonProgressMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.course.domain.LessonProgress}.
 */
@Service
@Transactional
public class LessonProgressServiceImpl implements LessonProgressService {

    private static final Logger LOG = LoggerFactory.getLogger(LessonProgressServiceImpl.class);

    private final LessonProgressRepository lessonProgressRepository;

    private final LessonProgressMapper lessonProgressMapper;

    public LessonProgressServiceImpl(LessonProgressRepository lessonProgressRepository, LessonProgressMapper lessonProgressMapper) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonProgressMapper = lessonProgressMapper;
    }

    @Override
    public LessonProgressDTO save(LessonProgressDTO lessonProgressDTO) {
        LOG.debug("Request to save LessonProgress : {}", lessonProgressDTO);
        LessonProgress lessonProgress = lessonProgressMapper.toEntity(lessonProgressDTO);
        lessonProgress = lessonProgressRepository.save(lessonProgress);
        return lessonProgressMapper.toDto(lessonProgress);
    }

    @Override
    public LessonProgressDTO update(LessonProgressDTO lessonProgressDTO) {
        LOG.debug("Request to update LessonProgress : {}", lessonProgressDTO);
        LessonProgress lessonProgress = lessonProgressMapper.toEntity(lessonProgressDTO);
        lessonProgress = lessonProgressRepository.save(lessonProgress);
        return lessonProgressMapper.toDto(lessonProgress);
    }

    @Override
    public Optional<LessonProgressDTO> partialUpdate(LessonProgressDTO lessonProgressDTO) {
        LOG.debug("Request to partially update LessonProgress : {}", lessonProgressDTO);

        return lessonProgressRepository
            .findById(lessonProgressDTO.getId())
            .map(existingLessonProgress -> {
                lessonProgressMapper.partialUpdate(existingLessonProgress, lessonProgressDTO);

                return existingLessonProgress;
            })
            .map(lessonProgressRepository::save)
            .map(lessonProgressMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonProgressDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all LessonProgresses");
        return lessonProgressRepository.findAll(pageable).map(lessonProgressMapper::toDto);
    }

    public Page<LessonProgressDTO> findAllWithEagerRelationships(Pageable pageable) {
        return lessonProgressRepository.findAllWithEagerRelationships(pageable).map(lessonProgressMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LessonProgressDTO> findOne(Long id) {
        LOG.debug("Request to get LessonProgress : {}", id);
        return lessonProgressRepository.findOneWithEagerRelationships(id).map(lessonProgressMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete LessonProgress : {}", id);
        lessonProgressRepository.deleteById(id);
    }
}
