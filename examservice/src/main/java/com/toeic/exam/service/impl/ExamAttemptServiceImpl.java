package com.toeic.exam.service.impl;

import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.service.ExamAttemptService;
import com.toeic.exam.service.dto.ExamAttemptDTO;
import com.toeic.exam.service.mapper.ExamAttemptMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.exam.domain.ExamAttempt}.
 */
@Service
@Transactional
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptServiceImpl.class);

    private final ExamAttemptRepository examAttemptRepository;

    private final ExamAttemptMapper examAttemptMapper;

    public ExamAttemptServiceImpl(ExamAttemptRepository examAttemptRepository, ExamAttemptMapper examAttemptMapper) {
        this.examAttemptRepository = examAttemptRepository;
        this.examAttemptMapper = examAttemptMapper;
    }

    @Override
    public ExamAttemptDTO save(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to save ExamAttempt : {}", examAttemptDTO);
        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    @Override
    public ExamAttemptDTO update(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to update ExamAttempt : {}", examAttemptDTO);
        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    @Override
    public Optional<ExamAttemptDTO> partialUpdate(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to partially update ExamAttempt : {}", examAttemptDTO);

        return examAttemptRepository
            .findById(examAttemptDTO.getId())
            .map(existingExamAttempt -> {
                examAttemptMapper.partialUpdate(existingExamAttempt, examAttemptDTO);

                return existingExamAttempt;
            })
            .map(examAttemptRepository::save)
            .map(examAttemptMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamAttemptDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ExamAttempts");
        return examAttemptRepository.findAll(pageable).map(examAttemptMapper::toDto);
    }

    public Page<ExamAttemptDTO> findAllWithEagerRelationships(Pageable pageable) {
        return examAttemptRepository.findAllWithEagerRelationships(pageable).map(examAttemptMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamAttemptDTO> findOne(Long id) {
        LOG.debug("Request to get ExamAttempt : {}", id);
        return examAttemptRepository.findOneWithEagerRelationships(id).map(examAttemptMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ExamAttempt : {}", id);
        examAttemptRepository.deleteById(id);
    }
}
