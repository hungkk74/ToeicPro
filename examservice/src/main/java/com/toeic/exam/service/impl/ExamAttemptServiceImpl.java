package com.toeic.exam.service.impl;

import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.UserAnswer;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.repository.QuestionRepository;
import com.toeic.exam.repository.UserAnswerRepository;
import com.toeic.exam.service.ExamAttemptService;
import com.toeic.exam.service.dto.ExamAttemptDTO;
import com.toeic.exam.service.dto.ExamResultDTO;
import com.toeic.exam.service.dto.ExamSubmissionDTO;
import com.toeic.exam.service.dto.QuestionAnswerSubmissionDTO;
import com.toeic.exam.service.mapper.ExamAttemptMapper;
import com.toeic.exam.service.util.ToeicScoreConverter;
import com.toeic.exam.domain.Part;
import com.toeic.exam.security.AuthoritiesConstants;
import com.toeic.exam.security.SecurityUtils;
import com.toeic.exam.service.dto.ExamAttemptHistoryDTO;
import com.toeic.exam.service.dto.review.ExamReviewDTO;
import com.toeic.exam.service.dto.review.PartScoreSummaryDTO;
import com.toeic.exam.service.dto.review.QuestionReviewDTO;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
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
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final JdbcTemplate jdbcTemplate;

    public ExamAttemptServiceImpl(
        ExamAttemptRepository examAttemptRepository,
        ExamAttemptMapper examAttemptMapper,
        QuestionRepository questionRepository,
        UserAnswerRepository userAnswerRepository,
        JdbcTemplate jdbcTemplate
    ) {
        this.examAttemptRepository = examAttemptRepository;
        this.examAttemptMapper = examAttemptMapper;
        this.questionRepository = questionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ExamAttemptDTO save(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to save ExamAttempt : {}", examAttemptDTO);
        String currentUser = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("Yêu cầu đăng nhập để bắt đầu bài thi!"));
        examAttemptDTO.setUserId(currentUser);
        if (examAttemptDTO.getStatus() == null) {
            examAttemptDTO.setStatus(AttemptStatus.IN_PROGRESS);
        }
        if (examAttemptDTO.getStartedAt() == null) {
            examAttemptDTO.setStartedAt(Instant.now());
        }
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

    @Override
    @Transactional(readOnly = true)
    public Page<ExamAttemptDTO> findAllWithEagerRelationships(Pageable pageable) {
        LOG.debug("Request to get all ExamAttempts with eager relationships");
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

    @Override
    @Transactional
    public ExamResultDTO submitExam(Long attemptId, ExamSubmissionDTO dto) {
        // 1. Lấy ExamAttempt và kiểm tra status == IN_PROGRESS
        ExamAttempt attempt = examAttemptRepository.findOneWithToOneRelationships(attemptId)
            .orElseThrow(() -> new EntityNotFoundException("Attempt not found with id: " + attemptId));
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Bài thi đã được nộp trước đó");
        }

        // Chống IDOR: Chỉ chủ nhân bài thi hoặc ADMIN mới được nộp
        checkOwnershipOrAdmin(attempt.getUserId(), "Bạn không có quyền nộp bài thi này!");

        // 2. Lấy toàn bộ câu hỏi của đề thi để lưu đầy đủ cho cả câu làm và câu bỏ qua
        List<Question> allQuestions;
        if (attempt.getExam() != null && attempt.getExam().getId() != null) {
            allQuestions = questionRepository.findByExamIdOrderByQuestionNumberAsc(attempt.getExam().getId());
        } else {
            Set<Long> qIds = dto.getAnswers() != null
                ? dto.getAnswers().stream().map(QuestionAnswerSubmissionDTO::questionId).collect(Collectors.toSet())
                : Set.of();
            allQuestions = questionRepository.findAllByIdInWithPart(qIds);
        }

        Map<Long, QuestionAnswerSubmissionDTO> submittedMap = new HashMap<>();
        if (dto.getAnswers() != null) {
            for (QuestionAnswerSubmissionDTO a : dto.getAnswers()) {
                if (a != null && a.questionId() != null) {
                    submittedMap.put(a.questionId(), a);
                }
            }
        }

        // 3. Duyệt chấm từng câu
        int listeningCorrect = 0, readingCorrect = 0, totalCorrect = 0, totalWrong = 0, totalSkipped = 0;
        int totalListeningQuestions = 0, totalReadingQuestions = 0;
        List<UserAnswer> userAnswers = new ArrayList<>(allQuestions.size());

        for (Question q : allQuestions) {
            boolean isListening = q.getPart() != null && q.getPart().getPartNumber() != null && q.getPart().getPartNumber() <= 4;
            if (isListening) {
                totalListeningQuestions++;
            } else {
                totalReadingQuestions++;
            }

            QuestionAnswerSubmissionDTO ans = submittedMap.get(q.getId());
            boolean hasAnswer = ans != null && ans.selectedOption() != null;
            boolean isCorrect = hasAnswer && ans.selectedOption() == q.getCorrectOption();

            if (!hasAnswer) {
                totalSkipped++;
            } else if (isCorrect) {
                totalCorrect++;
                if (isListening) {
                    listeningCorrect++;
                } else {
                    readingCorrect++;
                }
            } else {
                totalWrong++;
            }

            // Tạo UserAnswer (lưu cả câu bỏ qua để Review hiển thị đầy đủ)
            UserAnswer ua = new UserAnswer()
                .examAttempt(attempt)
                .question(q)
                .selectedOption(hasAnswer ? ans.selectedOption() : null)
                .isCorrect(isCorrect)
                .timeSpentSeconds(ans != null && ans.timeSpentSeconds() != null ? ans.timeSpentSeconds() : 0);
            userAnswers.add(ua);
        }

        // 4. Idempotent cleanup và high-performance JDBC batch insert câu trả lời
        userAnswerRepository.deleteByExamAttemptId(attemptId);
        if (!userAnswers.isEmpty()) {
            final String sql = "INSERT INTO user_answer (selected_option, is_correct, time_spent_seconds, exam_attempt_id, question_id) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    UserAnswer ua = userAnswers.get(i);
                    if (ua.getSelectedOption() != null) {
                        ps.setString(1, ua.getSelectedOption().name());
                    } else {
                        ps.setNull(1, Types.VARCHAR);
                    }
                    if (ua.getIsCorrect() != null) {
                        ps.setBoolean(2, ua.getIsCorrect());
                    } else {
                        ps.setNull(2, Types.BOOLEAN);
                    }
                    ps.setInt(3, ua.getTimeSpentSeconds() != null ? ua.getTimeSpentSeconds() : 0);
                    ps.setLong(4, attemptId);
                    ps.setLong(5, ua.getQuestion().getId());
                }

                @Override
                public int getBatchSize() {
                    return userAnswers.size();
                }
            });
        }

        // 5. Tính điểm theo ToeicScoreConverter (chuẩn hóa động theo số lượng câu hỏi thực tế của đề thi)
        int lScore = totalListeningQuestions > 0
            ? ToeicScoreConverter.toListeningScore(listeningCorrect, totalListeningQuestions)
            : 0;
        int rScore = totalReadingQuestions > 0
            ? ToeicScoreConverter.toReadingScore(readingCorrect, totalReadingQuestions)
            : 0;
        int totalScore = lScore + rScore;

        // Chống gian lận thời gian làm bài (Time Tampering Guard)
        Instant completedAt = Instant.now();
        int finalTimeSpent;
        if (attempt.getStartedAt() != null) {
            long actualElapsedSeconds = Duration.between(attempt.getStartedAt(), completedAt).toSeconds();
            if (dto.getTimeSpentSeconds() != null && dto.getTimeSpentSeconds() > 0) {
                finalTimeSpent = (int) Math.min(dto.getTimeSpentSeconds(), Math.max(1, actualElapsedSeconds));
            } else {
                finalTimeSpent = (int) Math.max(1, actualElapsedSeconds);
            }
        } else {
            finalTimeSpent = dto.getTimeSpentSeconds() != null ? Math.max(1, dto.getTimeSpentSeconds()) : 0;
        }

        attempt.setStatus(AttemptStatus.COMPLETED);
        attempt.setListeningScore(lScore);
        attempt.setReadingScore(rScore);
        attempt.setTotalScore(totalScore);
        attempt.setCorrectAnswers(totalCorrect);
        attempt.setWrongAnswers(totalWrong);
        attempt.setSkippedAnswers(totalSkipped);
        attempt.setTimeSpentSeconds(finalTimeSpent);
        attempt.setCompletedAt(completedAt);
        attempt = examAttemptRepository.saveAndFlush(attempt);

        // 6. Trả về DTO kết quả
        return buildResultDTO(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamReviewDTO getExamReview(Long attemptId) {
        LOG.debug("Request to get review for ExamAttempt : {}", attemptId);

        ExamAttempt attempt = examAttemptRepository.findOneWithToOneRelationships(attemptId)
            .orElseThrow(() -> new EntityNotFoundException("ExamAttempt not found with id: " + attemptId));

        if (attempt.getStatus() != AttemptStatus.COMPLETED) {
            throw new IllegalStateException("Exam attempt is not completed yet. Review is only available after submission.");
        }

        // Chống IDOR: Chỉ chủ nhân bài thi hoặc ADMIN mới được xem lại
        checkOwnershipOrAdmin(attempt.getUserId(), "Bạn không có quyền xem lại bài thi này!");

        List<UserAnswer> userAnswers = userAnswerRepository.findByExamAttemptIdWithQuestion(attemptId);

        Map<Integer, int[]> partStatsMap = new TreeMap<>();
        Map<Integer, String> partNameMap = new HashMap<>();
        List<QuestionReviewDTO> questionReviews = new ArrayList<>(userAnswers.size());

        for (UserAnswer ua : userAnswers) {
            Question q = ua.getQuestion();
            Part part = q != null ? q.getPart() : null;
            Integer partNumber = part != null ? part.getPartNumber() : 0;
            String partName = part != null ? part.getName() : "Part " + partNumber;

            if (partNumber > 0) {
                partNameMap.putIfAbsent(partNumber, partName);
                int[] stats = partStatsMap.computeIfAbsent(partNumber, k -> new int[2]);
                stats[1]++; // total questions
                if (Boolean.TRUE.equals(ua.getIsCorrect())) {
                    stats[0]++; // correct questions
                }
            }

            // Chỉ đính kèm transcript nếu câu làm sai và có passageText
            String transcript = null;
            if (Boolean.FALSE.equals(ua.getIsCorrect()) && q != null && q.getQuestionGroup() != null) {
                transcript = q.getQuestionGroup().getPassageText();
            }

            QuestionReviewDTO qDTO = new QuestionReviewDTO(
                q != null ? q.getId() : null,
                q != null ? q.getQuestionNumber() : null,
                partNumber,
                q != null ? q.getContent() : null,
                q != null ? q.getImageUrl() : null,
                q != null ? q.getAudioUrl() : null,
                q != null ? q.getOptionA() : null,
                q != null ? q.getOptionB() : null,
                q != null ? q.getOptionC() : null,
                q != null ? q.getOptionD() : null,
                ua.getSelectedOption(),
                q != null ? q.getCorrectOption() : null,
                ua.getIsCorrect(),
                q != null ? q.getExplanation() : null,
                transcript,
                ua.getTimeSpentSeconds()
            );
            questionReviews.add(qDTO);
        }

        List<PartScoreSummaryDTO> partSummaries = new ArrayList<>();
        for (Map.Entry<Integer, int[]> entry : partStatsMap.entrySet()) {
            Integer pNum = entry.getKey();
            int[] stats = entry.getValue();
            partSummaries.add(new PartScoreSummaryDTO(
                pNum,
                partNameMap.getOrDefault(pNum, "Part " + pNum),
                stats[0],
                stats[1]
            ));
        }

        return new ExamReviewDTO(
            attempt.getId(),
            attempt.getExam() != null ? attempt.getExam().getId() : null,
            attempt.getExam() != null ? attempt.getExam().getTitle() : null,
            attempt.getUserId(),
            attempt.getStatus(),
            attempt.getTotalScore(),
            attempt.getListeningScore(),
            attempt.getReadingScore(),
            attempt.getCorrectAnswers(),
            attempt.getWrongAnswers(),
            attempt.getSkippedAnswers(),
            attempt.getTimeSpentSeconds(),
            attempt.getStartedAt(),
            attempt.getCompletedAt(),
            partSummaries,
            questionReviews
        );
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
            attempt.getCompletedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamAttemptHistoryDTO> getMyExamHistory() {
        String currentUserId = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        List<ExamAttempt> attempts = examAttemptRepository.findCompletedByUserId(currentUserId);
        return attempts.stream()
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
            ))
            .toList();
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
