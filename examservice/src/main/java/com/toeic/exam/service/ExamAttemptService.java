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
 * Service điều phối quản lý và chấm bài {@link com.toeic.exam.domain.ExamAttempt}.
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

    public ExamAttemptDTO save(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to save ExamAttempt : {}", examAttemptDTO);
        String currentUser = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("Yêu cầu đăng nhập để bắt đầu bài thi!"));
        examAttemptDTO.setUserId(currentUser);

        if (examAttemptDTO.getExam() == null || examAttemptDTO.getExam().getId() == null) {
            throw new IllegalArgumentException("Đề thi không hợp lệ");
        }
        Exam exam = examRepository.findById(examAttemptDTO.getExam().getId())
            .orElseThrow(() -> new EntityNotFoundException("Exam not found with id: " + examAttemptDTO.getExam().getId()));
        if (!Boolean.TRUE.equals(exam.getIsPublished())) {
            throw new IllegalStateException("Đề thi chưa được công bố!");
        }

        examAttemptDTO.setStatus(AttemptStatus.IN_PROGRESS);
        examAttemptDTO.setListeningScore(null);
        examAttemptDTO.setReadingScore(null);
        examAttemptDTO.setTotalScore(null);
        examAttemptDTO.setCorrectAnswers(0);
        examAttemptDTO.setWrongAnswers(0);
        examAttemptDTO.setSkippedAnswers(0);
        examAttemptDTO.setStartedAt(Instant.now());
        examAttemptDTO.setCompletedAt(null);

        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    public ExamAttemptDTO update(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to update ExamAttempt : {}", examAttemptDTO);
        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

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
        examAttemptRepository.deleteById(id);
    }

    public ExamResultDTO submitExam(Long attemptId, ExamSubmissionDTO dto) {
        LOG.debug("Request to submit ExamAttempt : {}", attemptId);

        ExamAttempt attempt = examAttemptRepository.findOneForUpdate(attemptId)
            .orElseThrow(() -> new EntityNotFoundException("Attempt not found with id: " + attemptId));
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Bài thi đã được nộp trước đó");
        }

        checkOwnershipOrAdmin(attempt.getUserId(), "Bạn không có quyền nộp bài thi này!");

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

        attempt.setStatus(AttemptStatus.COMPLETED);
        attempt.setListeningScore(scored.listeningScore());
        attempt.setReadingScore(scored.readingScore());
        attempt.setTotalScore(scored.totalScore());
        attempt.setCorrectAnswers(scored.correctAnswers());
        attempt.setWrongAnswers(scored.wrongAnswers());
        attempt.setSkippedAnswers(scored.skippedAnswers());
        attempt.setTimeSpentSeconds(scored.timeSpentSeconds());
        attempt.setCompletedAt(scored.completedAt());
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

        return examAttemptRepository.findCompletedByUserId(currentUserId, pageable)
            .map(a -> new ExamAttemptHistoryDTO(
                a.getId(),
                a.getExam() != null ? a.getExam().getId() : null,
                a.getExam() != null ? a.getExam().getTitle() : null,
                a.getListeningScore(),
                a.getReadingScore(),
                a.getTotalScore(),
                a.getCorrectAnswers(),
                a.getWrongAnswers(),
                a.getSkippedAnswers(),
                a.getTimeSpentSeconds(),
                a.getStartedAt(),
                a.getCompletedAt()
            ));
    }

    public void cancelAttempt(Long attemptId) {
        LOG.debug("Request to cancel in-progress ExamAttempt : {}", attemptId);
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
            .orElseThrow(() -> new EntityNotFoundException("Attempt not found with id: " + attemptId));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Chỉ có thể hủy bài thi đang trong tiến trình làm!");
        }

        checkOwnershipOrAdmin(attempt.getUserId(), "Bạn không có quyền hủy bài thi này!");

        userAnswerRepository.deleteByExamAttemptId(attemptId);
        examAttemptRepository.delete(attempt);
    }

    private ExamResultDTO buildResultDTO(ExamAttempt attempt) {
        int correctCount = attempt.getCorrectAnswers() != null ? attempt.getCorrectAnswers() : 0;
        int wrongCount = attempt.getWrongAnswers() != null ? attempt.getWrongAnswers() : 0;
        int skippedCount = attempt.getSkippedAnswers() != null ? attempt.getSkippedAnswers() : 0;
        int answeredCount = correctCount + wrongCount;
        int totalQuestions = answeredCount + skippedCount;
        boolean canViewAnswers = totalQuestions > 0 && ((long) answeredCount * 100 >= (long) totalQuestions * 80);

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
            canViewAnswers
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
