package com.toeic.exam.service;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.repository.ExamRepository;
import com.toeic.exam.repository.PartRepository;
import com.toeic.exam.repository.QuestionGroupRepository;
import com.toeic.exam.repository.QuestionRepository;
import com.toeic.exam.security.AuthoritiesConstants;
import com.toeic.exam.security.SecurityUtils;
import com.toeic.exam.service.dto.ExamDTO;
import com.toeic.exam.service.dto.create.FullExamCreateDTO;
import com.toeic.exam.service.dto.take.ExamTakeDTO;
import com.toeic.exam.service.mapper.ExamMapper;
import com.toeic.exam.service.util.ExamTakeDtoBuilder;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Service điều phối quản lý {@link com.toeic.exam.domain.Exam}.
 */
@Service
@Transactional
public class ExamService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamService.class);

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final PartRepository partRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;
    private final ExamBulkImportService examBulkImportService;
    private final ExamTakeDtoBuilder examTakeDtoBuilder;
    private final EntityManager entityManager;
    private final FileStorageService fileStorageService;

    public ExamService(
        ExamRepository examRepository,
        ExamMapper examMapper,
        PartRepository partRepository,
        QuestionGroupRepository questionGroupRepository,
        QuestionRepository questionRepository,
        ExamBulkImportService examBulkImportService,
        ExamTakeDtoBuilder examTakeDtoBuilder,
        EntityManager entityManager,
        FileStorageService fileStorageService
    ) {
        this.examRepository = examRepository;
        this.examMapper = examMapper;
        this.partRepository = partRepository;
        this.questionGroupRepository = questionGroupRepository;
        this.questionRepository = questionRepository;
        this.examBulkImportService = examBulkImportService;
        this.examTakeDtoBuilder = examTakeDtoBuilder;
        this.entityManager = entityManager;
        this.fileStorageService = fileStorageService;
    }

    public ExamDTO save(ExamDTO examDTO) {
        LOG.debug("Request to save Exam : {}", examDTO);
        Exam exam = examMapper.toEntity(examDTO);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    public ExamDTO createFullExam(FullExamCreateDTO request) {
        return examBulkImportService.createFullExam(request);
    }

    public ExamDTO update(ExamDTO examDTO) {
        LOG.debug("Request to update Exam : {}", examDTO);
        Exam exam = examMapper.toEntity(examDTO);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

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

    @Transactional(readOnly = true)
    public Page<ExamDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Exams");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return examRepository.findAll(pageable).map(examMapper::toDto);
        }
        return examRepository.findByIsPublishedTrue(pageable).map(examMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ExamDTO> search(String keyword, Pageable pageable) {
        LOG.debug("Request to search Exams by keyword : {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(pageable);
        }
        String cleanKeyword = keyword.replaceAll("[+\\-*~\"()<>]", " ").trim();
        if (cleanKeyword.isEmpty()) {
            return findAll(pageable);
        }
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return examRepository.searchExamsFullText(cleanKeyword + "*", pageable).map(examMapper::toDto);
        }
        return examRepository.searchExamsFullTextPublished(cleanKeyword + "*", pageable).map(examMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ExamDTO> findOne(Long id) {
        LOG.debug("Request to get Exam : {}", id);
        return examRepository
            .findById(id)
            .filter(exam -> SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN) || Boolean.TRUE.equals(exam.getIsPublished()))
            .map(examMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Exam : {}", id);

        // 1. Thu thập danh sách file URLs/keys trên R2 trước khi xóa dữ liệu DB
        Set<String> filesToDelete = new HashSet<>();

        List<String> examAudios = entityManager.createQuery(
            "SELECT e.audioFullUrl FROM Exam e WHERE e.id = :examId AND e.audioFullUrl IS NOT NULL",
            String.class
        ).setParameter("examId", id).getResultList();
        filesToDelete.addAll(examAudios);

        List<String> groupAudios = entityManager.createQuery(
            "SELECT g.audioUrl FROM QuestionGroup g WHERE g.part.exam.id = :examId AND g.audioUrl IS NOT NULL",
            String.class
        ).setParameter("examId", id).getResultList();
        filesToDelete.addAll(groupAudios);

        List<String> groupImages = entityManager.createQuery(
            "SELECT g.imageUrl FROM QuestionGroup g WHERE g.part.exam.id = :examId AND g.imageUrl IS NOT NULL",
            String.class
        ).setParameter("examId", id).getResultList();
        filesToDelete.addAll(groupImages);

        List<String> questionAudios = entityManager.createQuery(
            "SELECT q.audioUrl FROM Question q WHERE q.part.exam.id = :examId AND q.audioUrl IS NOT NULL",
            String.class
        ).setParameter("examId", id).getResultList();
        filesToDelete.addAll(questionAudios);

        List<String> questionImages = entityManager.createQuery(
            "SELECT q.imageUrl FROM Question q WHERE q.part.exam.id = :examId AND q.imageUrl IS NOT NULL",
            String.class
        ).setParameter("examId", id).getResultList();
        filesToDelete.addAll(questionImages);

        // 2. Xóa các thực thể con theo thứ tự khóa ngoại trong DB
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

        // 3. Sau khi commit DB thành công, kích hoạt async xóa file trên R2 (không block và không gọi HTTP trong transaction)
        if (!filesToDelete.isEmpty()) {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        fileStorageService.deleteFilesAsync(filesToDelete);
                    }
                });
            } else {
                fileStorageService.deleteFilesAsync(filesToDelete);
            }
        }
    }

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
