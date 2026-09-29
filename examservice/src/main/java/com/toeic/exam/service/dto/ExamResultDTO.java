package com.toeic.exam.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO representing the result of an exam attempt after submission.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamResultDTO(
    Long attemptId,
    Long examId,
    String examTitle,
    AttemptStatus status,
    Integer listeningScore,
    Integer readingScore,
    Integer totalScore,
    Integer correctAnswers,
    Integer wrongAnswers,
    Integer skippedAnswers,
    Integer timeSpentSeconds,
    Instant completedAt,
    Boolean canViewAnswers
) implements Serializable {}
