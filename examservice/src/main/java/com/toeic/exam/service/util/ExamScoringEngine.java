package com.toeic.exam.service.util;

import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.UserAnswer;
import com.toeic.exam.service.dto.QuestionAnswerSubmissionDTO;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Engine chuyên trách chấm điểm thi TOEIC và bảo vệ tính toàn vẹn thời gian làm bài.
 */
@Component
public class ExamScoringEngine {

    public record ScoringResult(
        List<UserAnswer> userAnswers,
        int listeningScore,
        int readingScore,
        int totalScore,
        int correctAnswers,
        int wrongAnswers,
        int skippedAnswers,
        int timeSpentSeconds,
        Instant completedAt
    ) {}

    public ScoringResult calculate(
        ExamAttempt attempt,
        List<Question> allQuestions,
        List<QuestionAnswerSubmissionDTO> submittedAnswers,
        Integer submittedTimeSpentSeconds
    ) {
        Map<Long, QuestionAnswerSubmissionDTO> submittedMap = new HashMap<>();
        if (submittedAnswers != null) {
            for (QuestionAnswerSubmissionDTO a : submittedAnswers) {
                if (a != null && a.questionId() != null) {
                    submittedMap.put(a.questionId(), a);
                }
            }
        }

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
            boolean isCorrect = hasAnswer && ans != null && ans.selectedOption() == q.getCorrectOption();

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

            UserAnswer ua = new UserAnswer()
                .examAttempt(attempt)
                .question(q)
                .selectedOption(hasAnswer && ans != null ? ans.selectedOption() : null)
                .isCorrect(isCorrect)
                .timeSpentSeconds(ans != null && ans.timeSpentSeconds() != null ? ans.timeSpentSeconds() : 0);
            userAnswers.add(ua);
        }

        int lScore = totalListeningQuestions > 0
            ? ToeicScoreConverter.toListeningScore(listeningCorrect, totalListeningQuestions)
            : 0;
        int rScore = totalReadingQuestions > 0
            ? ToeicScoreConverter.toReadingScore(readingCorrect, totalReadingQuestions)
            : 0;
        int totalScore = lScore + rScore;

        Instant completedAt = Instant.now();
        int finalTimeSpent;
        if (attempt.getStartedAt() != null) {
            long actualElapsedSeconds = Duration.between(attempt.getStartedAt(), completedAt).toSeconds();
            if (submittedTimeSpentSeconds != null && submittedTimeSpentSeconds > 0) {
                finalTimeSpent = (int) Math.min(submittedTimeSpentSeconds, Math.max(1, actualElapsedSeconds));
            } else {
                finalTimeSpent = (int) Math.max(1, actualElapsedSeconds);
            }
        } else {
            finalTimeSpent = submittedTimeSpentSeconds != null ? Math.max(1, submittedTimeSpentSeconds) : 0;
        }

        return new ScoringResult(
            userAnswers,
            lScore,
            rScore,
            totalScore,
            totalCorrect,
            totalWrong,
            totalSkipped,
            finalTimeSpent,
            completedAt
        );
    }
}
