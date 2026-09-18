package com.toeic.exam.service.dto.take;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PartTakeDTO(
    Long id,
    Integer partNumber,
    String name,
    Integer totalQuestions,
    List<QuestionGroupTakeDTO> groups,
    List<QuestionTakeDTO> standaloneQuestions
) implements Serializable {}
