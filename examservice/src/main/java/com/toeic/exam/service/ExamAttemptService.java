package com.toeic.exam.service;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.repository.ExamRepository;
import com.toeic.exam.repository.QuestionRepository;
import com.toeic.exam.repository.UserAnswerBatchRepository;
import com.toeic.exam.repository.UserAnswerRepository;
import com.toeic.exam.security.AuthoritiesConstants;
import com.toeic.exam.security.SecurityUtils;
import com.toeic.exam.service.dto.ExamAttemptDTO;
import com.toeic.exam.service.dto.ExamAttemptHistoryDTO;
import com.toeic.exam.service.dto.ExamResultDTO;
import com.toeic.exam.service.dto.ExamSubmissionDTO;
import com.toeic.exam.service.dto.QuestionAnswerSubmissionDTO;
import com.toeic.exam.service.dto.StartExamAttemptDTO;
import com.toeic.exam.service.dto.review.ExamReviewDTO;
import com.toeic.exam.service.mapper.ExamAttemptMapper;
import com.toeic.exam.service.util.ExamScoringEngine;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service điều phối quản lý và chấm bài ExamAttempt
 */
@Service
@Transactional
public class ExamAttemptService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptService.class);

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamAttemptMapper examAttemptMapper;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerBatchRepository userAnswerBatchRepository;
    private final ExamScoringEngine examScoringEngine;
    private final ExamReviewService examReviewService;

    public ExamAttemptService(
        ExamAttemptRepository examAttemptRepository,
        ExamAttemptMapper examAttemptMapper,
        ExamRepository examRepository,
        QuestionRepository questionRepository,
        UserAnswerRepository userAnswerRepository,
        UserAnswerBatchRepository userAnswerBatchRepository,
        ExamScoringEngine examScoringEngine,
        ExamReviewService examReviewService
    ) {
        this.examAttemptRepository = examAttemptRepository;
        this.examAttemptMapper = examAttemptMapper;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.userAnswerBatchRepository = userAnswerBatchRepository;
        this.examScoringEngine = examScoringEngine;
        this.examReviewService = examReviewService;
    }

    public ExamAttemptDTO save(StartExamAttemptDTO startDTO) {
        LOG.debug("Request to start ExamAttempt for examId: {}", startDTO.examId());
        String currentUser = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("Yêu cầu đăng nhập để bắt đầu bài thi!"));

        if (startDTO.examId() == null) {
            throw new IllegalArgumentException("Đề thi không hợp lệ");
        }
        if (!examRepository.existsByIdAndIsPublishedTrue(startDTO.examId())) {
            throw new EntityNotFoundException("Đề thi không tồn tại hoặc chưa được công bố: " + startDTO.examId());
        }

        Exam examRef = examRepository.getReferenceById(startDTO.examId());

        ExamAttempt examAttempt = ExamAttempt.start(currentUser, examRef);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    public ExamAttemptDTO save(ExamAttemptDTO dto) {
        LOG.debug("Request to save ExamAttempt : {}", dto);
        Long examId = dto.getExam() != null ? dto.getExam().getId() : null;
        return save(new StartExamAttemptDTO(dto.getId(), examId));
    }

    public ExamAttemptDTO update(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to update ExamAttempt : {}", examAttemptDTO);
        ExamAttempt existing = examAttemptRepository.findOneWithToOneRelationships(examAttemptDTO.getId())
            .orElseThrow(() -> new EntityNotFoundException("Attempt not found with id: " + examAttemptDTO.getId()));
        checkOwnershipOrAdmin(existing.getUserId(), "Bạn không có quyền cập nhật bài thi này!");
        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    public Optional<ExamAttemptDTO> partialUpdate(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to partially update ExamAttempt : {}", examAttemptDTO);

        return examAttemptRepository
            .findOneWithToOneRelationships(examAttemptDTO.getId())
            .map(existingExamAttempt -> {
                checkOwnershipOrAdmin(existingExamAttempt.getUserId(), "Bạn không có quyền cập nhật bài thi này!");
                examAttemptMapper.partialUpdate(existingExamAttempt, examAttemptDTO);
                return existingExamAttempt;
            })
            .map(examAttemptRepository::save)
            .map(examAttemptMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ExamAttemptDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ExamAttempts");
        return examAttemptRepository.findAll(pageable).map(examAttemptMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ExamAttemptDTO> findAllWithEagerRelationships(Pageable pageable) {
        LOG.debug("Request to get all ExamAttempts with eager relationships");
        return examAttemptRepository.findAllWithEagerRelationships(pageable).map(examAttemptMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ExamAttemptDTO> findOne(Long id) {
        LOG.debug("Request to get ExamAttempt : {}", id);
        return examAttemptRepository.findOneWithEagerRelationships(id).map(examAttemptMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete ExamAttempt : {}", id);
        examAttemptRepository.findOneWithToOneRelationships(id).ifPresent(attempt -> {
            checkOwnershipOrAdmin(attempt.getUserId(), "Bạn không có quyền xóa bài thi này!");
            userAnswerRepository.deleteByExamAttemptId(id);
            examAttemptRepository.deleteById(id);
        });
    }

    @Transactional(timeout = 10)
    public ExamResultDTO submitExam(Long attemptId, ExamSubmissionDTO dto) {
        LOG.debug("Request to submit ExamAttempt : {}", attemptId);

        ExamAttemptRepository.AttemptAuthView auth = examAttemptRepository
            .findAuthById(attemptId)
            .orElseThrow(() -> new EntityNotFoundException("Attempt not found with id: " + attemptId));
        if (auth.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Bài thi đã được nộp trước đó");
        }
        checkOwnershipOrAdmin(auth.getUserId(), "Bạn không có quyền nộp bài thi này!");

        ExamAttempt attempt = examAttemptRepository.findOneForUpdate(attemptId)
            .orElseThrow(() -> new EntityNotFoundException("Attempt not found with id: " + attemptId));

        List<Question> allQuestions;
        if (attempt.getExam() != null && attempt.getExam().getId() != null) {
            allQuestions = questionRepository.findByExamIdForScoring(attempt.getExam().getId());
        } else {
            Set<Long> qIds = dto.getAnswers() != null
                ? dto.getAnswers().stream().map(QuestionAnswerSubmissionDTO::questionId).collect(Collectors.toSet())
                : Set.of();
            allQuestions = questionRepository.findAllByIdInWithPart(qIds);
        }

        ExamScoringEngine.ScoringResult scored = examScoringEngine.calculate(
            attempt,
            allQuestions,
            dto.getAnswers(),
            dto.getTimeSpentSeconds()
        );

        userAnswerRepository.deleteByExamAttemptId(attemptId);
        userAnswerBatchRepository.batchInsert(attemptId, scored.userAnswers());

        attempt.complete(
            scored.listeningScore(),
            scored.readingScore(),
            scored.totalScore(),
            scored.correctAnswers(),
            scored.wrongAnswers(),
            scored.skippedAnswers(),
            scored.timeSpentSeconds(),
            scored.completedAt()
        );
        attempt = examAttemptRepository.saveAndFlush(attempt);

        return buildResultDTO(attempt);
    }

    @Transactional(readOnly = true)
    public ExamReviewDTO getExamReview(Long attemptId) {
        return examReviewService.getExamReview(attemptId);
    }

    @Transactional(readOnly = true)
    public Page<ExamAttemptHistoryDTO> getMyExamHistory(Pageable pageable) {
        String currentUserId = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        return examAttemptRepository.findHistoryByUserId(currentUserId, pageable);
    }

    public void cancelAttempt(Long attemptId) {
        LOG.debug("Request to cancel in-progress ExamAttempt : {}", attemptId);
        ExamAttempt attempt = examAttemptRepository.findOneWithToOneRelationships(attemptId)
            .orElseThrow(() -> new EntityNotFoundException("Attempt not found with id: " + attemptId));

        attempt.validateCanCancel();
        checkOwnershipOrAdmin(attempt.getUserId(), "Bạn không có quyền hủy bài thi này!");

        userAnswerRepository.deleteByExamAttemptId(attemptId);
        examAttemptRepository.deleteById(attemptId);
    }

    private ExamResultDTO buildResultDTO(ExamAttempt attempt) {
        return new ExamResultDTO(
            attempt.getId(),
            attempt.getExam() != null ? attempt.getExam().getId() : null,
            attempt.getExam() != null ? attempt.getExam().getTitle() : null,
            attempt.getStatus(),
            attempt.getListeningScore(),
            attempt.getReadingScore(),
            attempt.getTotalScore(),
            attempt.getCorrectAnswers(),
            attempt.getWrongAnswers(),
            attempt.getSkippedAnswers(),
            attempt.getTimeSpentSeconds(),
            attempt.getCompletedAt(),
            attempt.canViewAnswers()
        );
    }

    private void checkOwnershipOrAdmin(String attemptUserId, String errorMessage) {
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return;
        }

        String currentUser = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("Yêu cầu đăng nhập để truy cập tài nguyên bài thi này!"));

        if (attemptUserId == null || !attemptUserId.equals(currentUser)) {
            throw new AccessDeniedException(errorMessage);
        }
    }
}
