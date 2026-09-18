package com.toeic.exam.service.dto.take;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QuestionGroupTakeDTO(
    Long id,
    String passageText,
    String audioUrl,
    String imageUrl,
    List<QuestionTakeDTO> questions
) implements Serializable {}
