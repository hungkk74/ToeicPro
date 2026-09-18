package com.toeic.exam.service.dto.take;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamTakeDTO(
    Long id,
    String code,
    String title,
    Integer durationMinutes,
    Integer totalQuestions,
    String audioFullUrl,
    List<PartTakeDTO> parts
) implements Serializable {}
