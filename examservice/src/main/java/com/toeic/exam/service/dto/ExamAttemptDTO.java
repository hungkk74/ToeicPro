package com.toeic.exam.service.dto;

import com.toeic.exam.domain.enumeration.AttemptStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.exam.domain.ExamAttempt} entity.
 */
@Schema(description = "Lịch sử lần làm bài thi của học viên")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamAttemptDTO implements Serializable {

    private Long id;

    @Size(max = 36)
    private String userId;

    private AttemptStatus status;

    @Min(value = 5)
    @Max(value = 495)
    private Integer listeningScore;

    @Min(value = 5)
    @Max(value = 495)
    private Integer readingScore;

    @Min(value = 10)
    @Max(value = 990)
    private Integer totalScore;

    private Integer correctAnswers;

    private Integer wrongAnswers;

    private Integer skippedAnswers;

    private Integer timeSpentSeconds;

    private Instant startedAt;

    private Instant completedAt;

    @NotNull
    private ExamDTO exam;

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

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Integer getListeningScore() {
        return listeningScore;
    }

    public void setListeningScore(Integer listeningScore) {
        this.listeningScore = listeningScore;
    }

    public Integer getReadingScore() {
        return readingScore;
    }

    public void setReadingScore(Integer readingScore) {
        this.readingScore = readingScore;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Integer getSkippedAnswers() {
        return skippedAnswers;
    }

    public void setSkippedAnswers(Integer skippedAnswers) {
        this.skippedAnswers = skippedAnswers;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
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
        if (!(o instanceof ExamAttemptDTO)) {
            return false;
        }

        ExamAttemptDTO examAttemptDTO = (ExamAttemptDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, examAttemptDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamAttemptDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", status='" + getStatus() + "'" +
            ", listeningScore=" + getListeningScore() +
            ", readingScore=" + getReadingScore() +
            ", totalScore=" + getTotalScore() +
            ", correctAnswers=" + getCorrectAnswers() +
            ", wrongAnswers=" + getWrongAnswers() +
            ", skippedAnswers=" + getSkippedAnswers() +
            ", timeSpentSeconds=" + getTimeSpentSeconds() +
            ", startedAt='" + getStartedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", exam=" + getExam() +
            "}";
    }
}
