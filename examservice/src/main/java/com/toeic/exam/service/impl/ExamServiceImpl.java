package com.toeic.exam.service.impl;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.repository.ExamRepository;
import com.toeic.exam.repository.PartRepository;
import com.toeic.exam.repository.QuestionGroupRepository;
import com.toeic.exam.repository.QuestionRepository;
import com.toeic.exam.service.ExamBulkImportService;
import com.toeic.exam.service.ExamService;
import com.toeic.exam.service.dto.ExamDTO;
import com.toeic.exam.service.dto.create.FullExamCreateDTO;
import com.toeic.exam.service.dto.take.ExamTakeDTO;
import com.toeic.exam.service.mapper.ExamMapper;
import com.toeic.exam.service.util.ExamTakeDtoBuilder;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation điều phối quản lý {@link com.toeic.exam.domain.Exam}.
 */
@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamServiceImpl.class);

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final PartRepository partRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;
    private final ExamBulkImportService examBulkImportService;
    private final ExamTakeDtoBuilder examTakeDtoBuilder;
    private final EntityManager entityManager;

    public ExamServiceImpl(
        ExamRepository examRepository,
        ExamMapper examMapper,
        PartRepository partRepository,
        QuestionGroupRepository questionGroupRepository,
        QuestionRepository questionRepository,
        ExamBulkImportService examBulkImportService,
        ExamTakeDtoBuilder examTakeDtoBuilder,
        EntityManager entityManager
    ) {
        this.examRepository = examRepository;
        this.examMapper = examMapper;
        this.partRepository = partRepository;
        this.questionGroupRepository = questionGroupRepository;
        this.questionRepository = questionRepository;
        this.examBulkImportService = examBulkImportService;
        this.examTakeDtoBuilder = examTakeDtoBuilder;
        this.entityManager = entityManager;
    }

    @Override
    public ExamDTO save(ExamDTO examDTO) {
        LOG.debug("Request to save Exam : {}", examDTO);
        Exam exam = examMapper.toEntity(examDTO);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    @Override
    public ExamDTO createFullExam(FullExamCreateDTO request) {
        return examBulkImportService.createFullExam(request);
    }

    @Override
    public ExamDTO update(ExamDTO examDTO) {
        LOG.debug("Request to update Exam : {}", examDTO);
        Exam exam = examMapper.toEntity(examDTO);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    @Override
    public Optional<ExamDTO> partialUpdate(ExamDTO examDTO) {
        LOG.debug("Request to partially update Exam : {}", examDTO);

        return examRepository
            .findById(examDTO.getId())
            .map(existingExam -> {
                examMapper.partialUpdate(existingExam, examDTO);
                return existingExam;
            })
            .map(examRepository::save)
            .map(examMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Exams");
        return examRepository.findAll(pageable).map(examMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamDTO> search(String keyword, Pageable pageable) {
        LOG.debug("Request to search Exams by keyword : {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(pageable);
        }
        return examRepository.searchExamsFullText(keyword, pageable).map(examMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamDTO> findOne(Long id) {
        LOG.debug("Request to get Exam : {}", id);
        return examRepository.findById(id).map(examMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Exam : {}", id);

        entityManager.createQuery("DELETE FROM UserAnswer u WHERE u.examAttempt.id IN (SELECT a.id FROM ExamAttempt a WHERE a.exam.id = :examId)")
            .setParameter("examId", id)
            .executeUpdate();

        entityManager.createQuery("DELETE FROM ExamAttempt a WHERE a.exam.id = :examId")
            .setParameter("examId", id)
            .executeUpdate();

        entityManager.createQuery("DELETE FROM Question q WHERE q.part.id IN (SELECT p.id FROM Part p WHERE p.exam.id = :examId)")
            .setParameter("examId", id)
            .executeUpdate();

        entityManager.createQuery("DELETE FROM QuestionGroup g WHERE g.part.id IN (SELECT p.id FROM Part p WHERE p.exam.id = :examId)")
            .setParameter("examId", id)
            .executeUpdate();

        entityManager.createQuery("DELETE FROM Part p WHERE p.exam.id = :examId")
            .setParameter("examId", id)
            .executeUpdate();

        examRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamTakeDTO getExamForTaking(Long examId) {
        LOG.debug("Request to get exam for taking: {}", examId);

        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));

        if (Boolean.FALSE.equals(exam.getIsPublished())) {
            throw new IllegalStateException("Exam is not published yet");
        }

        List<Question> questions = questionRepository.findByExamIdOrderByQuestionNumberAsc(examId);
        List<QuestionGroup> groups = questionGroupRepository.findByExamId(examId);
        List<Part> parts = partRepository.findByExamIdOrderByPartNumberAsc(examId);

        return examTakeDtoBuilder.build(exam, parts, groups, questions);
    }
}
