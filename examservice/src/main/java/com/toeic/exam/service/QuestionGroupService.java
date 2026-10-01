package com.toeic.exam.service;

import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.repository.QuestionGroupRepository;
import com.toeic.exam.service.dto.QuestionGroupDTO;
import com.toeic.exam.service.mapper.QuestionGroupMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing {@link com.toeic.exam.domain.QuestionGroup}.
 */
@Service
@Transactional
public class QuestionGroupService {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionGroupService.class);

    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionGroupMapper questionGroupMapper;

    public QuestionGroupService(QuestionGroupRepository questionGroupRepository, QuestionGroupMapper questionGroupMapper) {
        this.questionGroupRepository = questionGroupRepository;
        this.questionGroupMapper = questionGroupMapper;
    }

    public QuestionGroupDTO save(QuestionGroupDTO questionGroupDTO) {
        LOG.debug("Request to save QuestionGroup : {}", questionGroupDTO);
        QuestionGroup questionGroup = questionGroupMapper.toEntity(questionGroupDTO);
        questionGroup = questionGroupRepository.save(questionGroup);
        return questionGroupMapper.toDto(questionGroup);
    }

    public QuestionGroupDTO update(QuestionGroupDTO questionGroupDTO) {
        LOG.debug("Request to update QuestionGroup : {}", questionGroupDTO);
        QuestionGroup questionGroup = questionGroupMapper.toEntity(questionGroupDTO);
        questionGroup = questionGroupRepository.save(questionGroup);
        return questionGroupMapper.toDto(questionGroup);
    }

    public Optional<QuestionGroupDTO> partialUpdate(QuestionGroupDTO questionGroupDTO) {
        LOG.debug("Request to partially update QuestionGroup : {}", questionGroupDTO);

        return questionGroupRepository
            .findById(questionGroupDTO.getId())
            .map(existingQuestionGroup -> {
                questionGroupMapper.partialUpdate(existingQuestionGroup, questionGroupDTO);
                return existingQuestionGroup;
            })
            .map(questionGroupRepository::save)
            .map(questionGroupMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<QuestionGroupDTO> findAll() {
        LOG.debug("Request to get all QuestionGroups");
        return questionGroupRepository.findAll().stream().map(questionGroupMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionGroupDTO> findAllWithEagerRelationships() {
        LOG.debug("Request to get all QuestionGroups with eager relationships");
        return questionGroupRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(questionGroupMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<QuestionGroupDTO> findOne(Long id) {
        LOG.debug("Request to get QuestionGroup : {}", id);
        return questionGroupRepository.findOneWithEagerRelationships(id).map(questionGroupMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete QuestionGroup : {}", id);
        questionGroupRepository.deleteById(id);
    }
}
