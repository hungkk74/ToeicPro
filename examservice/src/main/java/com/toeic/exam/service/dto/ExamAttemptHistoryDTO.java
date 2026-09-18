package com.toeic.exam.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO record representing a summary of a completed exam attempt in the user's personal history.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamAttemptHistoryDTO(
    Long attemptId,
    Long examId,
    String examTitle,
    Integer listeningScore,
    Integer readingScore,
    Integer totalScore,
    Integer correctAnswers,
    Integer wrongAnswers,
    Integer skippedAnswers,
    Integer timeSpentSeconds,
    Instant startedAt,
    Instant completedAt
) implements Serializable {}
