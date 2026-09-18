package com.toeic.exam.service.dto.take;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QuestionTakeDTO(
    Long id,
    Integer questionNumber,
    String content,
    String imageUrl,
    String audioUrl,
    String optionA,
    String optionB,
    String optionC,
    String optionD
) implements Serializable {}
