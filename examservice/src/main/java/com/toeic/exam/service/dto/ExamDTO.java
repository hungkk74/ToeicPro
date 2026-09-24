package com.toeic.exam.service.dto;

import com.toeic.exam.domain.enumeration.ExamCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.exam.domain.Exam} entity.
 */
@Schema(description = "Danh mục đề thi (Full test 200 câu, Mini test, hoặc Practice)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class    ExamDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 255)
    private String title;   

    @NotNull
    private ExamCategory category;

    @NotNull
    private Integer durationMinutes;

    @NotNull
    private Integer totalQuestions;

    @Size(max = 1000)
    private String audioFullUrl;

    @NotNull
    private Boolean isPublished;

    @NotNull
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExamCategory getCategory() {
        return category;
    }

    public void setCategory(ExamCategory category) {
        this.category = category;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getAudioFullUrl() {
        return audioFullUrl;
    }

    public void setAudioFullUrl(String audioFullUrl) {
        this.audioFullUrl = audioFullUrl;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamDTO)) {
            return false;
        }

        ExamDTO examDTO = (ExamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, examDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", title='" + getTitle() + "'" +
            ", category='" + getCategory() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", totalQuestions=" + getTotalQuestions() +
            ", audioFullUrl='" + getAudioFullUrl() + "'" +
            ", isPublished='" + getIsPublished() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
