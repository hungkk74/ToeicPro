package com.toeic.exam.service.impl;

import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.UserAnswer;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.repository.UserAnswerRepository;
import com.toeic.exam.security.AuthoritiesConstants;
import com.toeic.exam.security.SecurityUtils;
import com.toeic.exam.service.ExamReviewService;
import com.toeic.exam.service.dto.review.ExamReviewDTO;
import com.toeic.exam.service.dto.review.PartScoreSummaryDTO;
import com.toeic.exam.service.dto.review.QuestionReviewDTO;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation chuyên biệt xử lý xem lại và thống kê bài thi.
 */
@Service
@Transactional(readOnly = true)
public class ExamReviewServiceImpl implements ExamReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamReviewServiceImpl.class);

    private final ExamAttemptRepository examAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;

    public ExamReviewServiceImpl(
        ExamAttemptRepository examAttemptRepository,
        UserAnswerRepository userAnswerRepository
    ) {
        this.examAttemptRepository = examAttemptRepository;
        this.userAnswerRepository = userAnswerRepository;
    }

    @Override
    public ExamReviewDTO getExamReview(Long attemptId) {
        LOG.debug("Request to get review for ExamAttempt : {}", attemptId);

        ExamAttempt attempt = examAttemptRepository.findOneWithToOneRelationships(attemptId)
            .orElseThrow(() -> new EntityNotFoundException("ExamAttempt not found with id: " + attemptId));

        if (attempt.getStatus() != AttemptStatus.COMPLETED) {
            throw new IllegalStateException("Exam attempt is not completed yet. Review is only available after submission.");
        }

        checkOwnershipOrAdmin(attempt.getUserId(), "Bạn không có quyền xem lại bài thi này!");

        int correctCount = attempt.getCorrectAnswers() != null ? attempt.getCorrectAnswers() : 0;
        int wrongCount = attempt.getWrongAnswers() != null ? attempt.getWrongAnswers() : 0;
        int skippedCount = attempt.getSkippedAnswers() != null ? attempt.getSkippedAnswers() : 0;
        int answeredCount = correctCount + wrongCount;
        int totalQuestions = answeredCount + skippedCount;
        boolean canViewAnswers = totalQuestions > 0 && ((long) answeredCount * 100 >= (long) totalQuestions * 80);

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

            // Chỉ đính kèm transcript nếu được phép xem đáp án và câu làm sai và có passageText
            String transcript = null;
            if (canViewAnswers && Boolean.FALSE.equals(ua.getIsCorrect()) && q != null && q.getQuestionGroup() != null) {
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
                canViewAnswers && q != null ? q.getCorrectOption() : null,
                canViewAnswers ? ua.getIsCorrect() : null,
                canViewAnswers && q != null ? q.getExplanation() : null,
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
            questionReviews,
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
