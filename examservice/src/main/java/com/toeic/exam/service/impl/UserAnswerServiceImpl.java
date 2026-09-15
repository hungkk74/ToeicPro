package com.toeic.exam.service.impl;

import com.toeic.exam.domain.UserAnswer;
import com.toeic.exam.repository.UserAnswerRepository;
import com.toeic.exam.service.UserAnswerService;
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
 * Service Implementation for managing {@link com.toeic.exam.domain.UserAnswer}.
 */
@Service
@Transactional
public class UserAnswerServiceImpl implements UserAnswerService {

    private static final Logger LOG = LoggerFactory.getLogger(UserAnswerServiceImpl.class);

    private final UserAnswerRepository userAnswerRepository;

    private final UserAnswerMapper userAnswerMapper;

    public UserAnswerServiceImpl(UserAnswerRepository userAnswerRepository, UserAnswerMapper userAnswerMapper) {
        this.userAnswerRepository = userAnswerRepository;
        this.userAnswerMapper = userAnswerMapper;
    }

    @Override
    public UserAnswerDTO save(UserAnswerDTO userAnswerDTO) {
        LOG.debug("Request to save UserAnswer : {}", userAnswerDTO);
        UserAnswer userAnswer = userAnswerMapper.toEntity(userAnswerDTO);
        userAnswer = userAnswerRepository.save(userAnswer);
        return userAnswerMapper.toDto(userAnswer);
    }

    @Override
    public UserAnswerDTO update(UserAnswerDTO userAnswerDTO) {
        LOG.debug("Request to update UserAnswer : {}", userAnswerDTO);
        UserAnswer userAnswer = userAnswerMapper.toEntity(userAnswerDTO);
        userAnswer = userAnswerRepository.save(userAnswer);
        return userAnswerMapper.toDto(userAnswer);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Page<UserAnswerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserAnswers");
        return userAnswerRepository.findAll(pageable).map(userAnswerMapper::toDto);
    }

    public Page<UserAnswerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userAnswerRepository.findAllWithEagerRelationships(pageable).map(userAnswerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAnswerDTO> findOne(Long id) {
        LOG.debug("Request to get UserAnswer : {}", id);
        return userAnswerRepository.findOneWithEagerRelationships(id).map(userAnswerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserAnswer : {}", id);
        userAnswerRepository.deleteById(id);
    }
}
