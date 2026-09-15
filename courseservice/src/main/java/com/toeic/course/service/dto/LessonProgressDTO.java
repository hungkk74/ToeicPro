package com.toeic.course.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.course.domain.LessonProgress} entity.
 */
@Schema(description = "Theo dõi tiến độ học tập chi tiết của học viên cho từng bài học")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonProgressDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 36)
    private String userId;

    @NotNull
    private Boolean isCompleted;

    @Min(value = 0)
    private Integer lastWatchedSecond;

    private Instant completedAt;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private LessonDTO lesson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getLastWatchedSecond() {
        return lastWatchedSecond;
    }

    public void setLastWatchedSecond(Integer lastWatchedSecond) {
        this.lastWatchedSecond = lastWatchedSecond;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LessonDTO getLesson() {
        return lesson;
    }

    public void setLesson(LessonDTO lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonProgressDTO)) {
            return false;
        }

        LessonProgressDTO lessonProgressDTO = (LessonProgressDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lessonProgressDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonProgressDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", isCompleted='" + getIsCompleted() + "'" +
            ", lastWatchedSecond=" + getLastWatchedSecond() +
            ", completedAt='" + getCompletedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", lesson=" + getLesson() +
            "}";
    }
}
