package com.toeic.exam.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO khởi tạo lần làm bài thi mới.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StartExamAttemptDTO(
    Long id,
    @NotNull(message = "examId không được để trống")
    Long examId
) implements Serializable {

    @JsonCreator
    public StartExamAttemptDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("examId") Long examId,
        @JsonProperty("exam") ExamRef exam
    ) {
        this(id, examId != null ? examId : (exam != null ? exam.id() : null));
    }

    public StartExamAttemptDTO(Long examId) {
        this(null, examId);
    }

    public record ExamRef(@JsonProperty("id") Long id) implements Serializable {}
}
