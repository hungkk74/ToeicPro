package com.toeic.exam.service.impl;

import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.repository.QuestionGroupRepository;
import com.toeic.exam.service.QuestionGroupService;
import com.toeic.exam.service.dto.QuestionGroupDTO;
import com.toeic.exam.service.mapper.QuestionGroupMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.exam.domain.QuestionGroup}.
 */
@Service
@Transactional
public class QuestionGroupServiceImpl implements QuestionGroupService {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionGroupServiceImpl.class);

    private final QuestionGroupRepository questionGroupRepository;

    private final QuestionGroupMapper questionGroupMapper;

    public QuestionGroupServiceImpl(QuestionGroupRepository questionGroupRepository, QuestionGroupMapper questionGroupMapper) {
        this.questionGroupRepository = questionGroupRepository;
        this.questionGroupMapper = questionGroupMapper;
    }

    @Override
    public QuestionGroupDTO save(QuestionGroupDTO questionGroupDTO) {
        LOG.debug("Request to save QuestionGroup : {}", questionGroupDTO);
        QuestionGroup questionGroup = questionGroupMapper.toEntity(questionGroupDTO);
        questionGroup = questionGroupRepository.save(questionGroup);
        return questionGroupMapper.toDto(questionGroup);
    }

    @Override
    public QuestionGroupDTO update(QuestionGroupDTO questionGroupDTO) {
        LOG.debug("Request to update QuestionGroup : {}", questionGroupDTO);
        QuestionGroup questionGroup = questionGroupMapper.toEntity(questionGroupDTO);
        questionGroup = questionGroupRepository.save(questionGroup);
        return questionGroupMapper.toDto(questionGroup);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public List<QuestionGroupDTO> findAll() {
        LOG.debug("Request to get all QuestionGroups");
        return questionGroupRepository.findAll().stream().map(questionGroupMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public List<QuestionGroupDTO> findAllWithEagerRelationships() {
        return questionGroupRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(questionGroupMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionGroupDTO> findOne(Long id) {
        LOG.debug("Request to get QuestionGroup : {}", id);
        return questionGroupRepository.findOneWithEagerRelationships(id).map(questionGroupMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete QuestionGroup : {}", id);
        questionGroupRepository.deleteById(id);
    }
}
