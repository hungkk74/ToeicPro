package com.toeic.exam.service.dto;

import com.toeic.exam.domain.enumeration.AnswerOption;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO representing a student's answer for a single question.
 */
public record QuestionAnswerSubmissionDTO(
    @NotNull
    Long questionId,
    AnswerOption selectedOption,
    Integer timeSpentSeconds
) implements Serializable {}
