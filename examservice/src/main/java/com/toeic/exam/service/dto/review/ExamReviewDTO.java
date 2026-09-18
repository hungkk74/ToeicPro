package com.toeic.exam.service.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamReviewDTO(
    Long attemptId,
    Long examId,
    String examTitle,
    String userId,
    AttemptStatus status,
    Integer totalScore,
    Integer listeningScore,
    Integer readingScore,
    Integer correctAnswers,
    Integer wrongAnswers,
    Integer skippedAnswers,
    Integer timeSpentSeconds,
    Instant startedAt,
    Instant completedAt,
    List<PartScoreSummaryDTO> partSummaries,
    List<QuestionReviewDTO> questions
) implements Serializable {}
