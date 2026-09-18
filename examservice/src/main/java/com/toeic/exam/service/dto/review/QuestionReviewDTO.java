package com.toeic.exam.service.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.toeic.exam.domain.enumeration.AnswerOption;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QuestionReviewDTO(
    Long questionId,
    Integer questionNumber,
    Integer partNumber,
    String content,
    String imageUrl,
    String audioUrl,
    String optionA,
    String optionB,
    String optionC,
    String optionD,
    AnswerOption selectedOption,
    AnswerOption correctOption,
    Boolean isCorrect,
    String explanation,
    String transcript,
    Integer timeSpentSeconds
) implements Serializable {}
