package com.toeic.exam.service;

import com.toeic.exam.domain.UserAnswer;
import com.toeic.exam.repository.UserAnswerRepository;
import com.toeic.exam.service.dto.UserAnswerDTO;
import com.toeic.exam.service.mapper.UserAnswerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing {@link com.toeic.exam.domain.UserAnswer}.
 */
@Service
@Transactional
public class UserAnswerService {

    private static final Logger LOG = LoggerFactory.getLogger(UserAnswerService.class);

    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerMapper userAnswerMapper;

    public UserAnswerService(UserAnswerRepository userAnswerRepository, UserAnswerMapper userAnswerMapper) {
        this.userAnswerRepository = userAnswerRepository;
        this.userAnswerMapper = userAnswerMapper;
    }

    public UserAnswerDTO save(UserAnswerDTO userAnswerDTO) {
        LOG.debug("Request to save UserAnswer : {}", userAnswerDTO);
        UserAnswer userAnswer = userAnswerMapper.toEntity(userAnswerDTO);
        userAnswer = userAnswerRepository.save(userAnswer);
        return userAnswerMapper.toDto(userAnswer);
    }

    public UserAnswerDTO update(UserAnswerDTO userAnswerDTO) {
        LOG.debug("Request to update UserAnswer : {}", userAnswerDTO);
        UserAnswer userAnswer = userAnswerMapper.toEntity(userAnswerDTO);
        userAnswer = userAnswerRepository.save(userAnswer);
        return userAnswerMapper.toDto(userAnswer);
    }

    public Optional<UserAnswerDTO> partialUpdate(UserAnswerDTO userAnswerDTO) {
        LOG.debug("Request to partially update UserAnswer : {}", userAnswerDTO);

        return userAnswerRepository
            .findById(userAnswerDTO.getId())
            .map(existingUserAnswer -> {
                userAnswerMapper.partialUpdate(existingUserAnswer, userAnswerDTO);
                return existingUserAnswer;
            })
            .map(userAnswerRepository::save)
            .map(userAnswerMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<UserAnswerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserAnswers");
        return findAllWithEagerRelationships(pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserAnswerDTO> findAllWithEagerRelationships(Pageable pageable) {
        LOG.debug("Request to get all UserAnswers with eager relationships");
        return userAnswerRepository.findAllWithEagerRelationships(pageable).map(userAnswerMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserAnswerDTO> findOne(Long id) {
        LOG.debug("Request to get UserAnswer : {}", id);
        return userAnswerRepository.findOneWithEagerRelationships(id).map(userAnswerMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete UserAnswer : {}", id);
        userAnswerRepository.deleteById(id);
    }
}
