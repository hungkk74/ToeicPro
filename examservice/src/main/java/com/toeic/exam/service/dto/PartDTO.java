package com.toeic.exam.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.exam.domain.Part} entity.
 */
@Schema(description = "7 Phần thi TOEIC (Part 1 -> Part 7)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 7)
    private Integer partNumber;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private Integer totalQuestions;

    @NotNull
    private ExamDTO exam;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Integer partNumber) {
        this.partNumber = partNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public ExamDTO getExam() {
        return exam;
    }

    public void setExam(ExamDTO exam) {
        this.exam = exam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartDTO)) {
            return false;
        }

        PartDTO partDTO = (PartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartDTO{" +
            "id=" + getId() +
            ", partNumber=" + getPartNumber() +
            ", name='" + getName() + "'" +
            ", totalQuestions=" + getTotalQuestions() +
            ", exam=" + getExam() +
            "}";
    }
}
