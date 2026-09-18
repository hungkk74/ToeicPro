package com.toeic.exam.service.dto;

import com.toeic.exam.domain.enumeration.AttemptStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO representing the result of an exam attempt after submission.
 */
public class ExamResultDTO implements Serializable {

    private Long attemptId;
    private Long examId;
    private String examTitle;
    private AttemptStatus status;
    private Integer listeningScore;
    private Integer readingScore;
    private Integer totalScore;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer skippedAnswers;
    private Integer timeSpentSeconds;
    private Instant completedAt;

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
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

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExamResultDTO that)) return false;
        return Objects.equals(attemptId, that.attemptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attemptId);
    }

    @Override
    public String toString() {
        return "ExamResultDTO{" +
            "attemptId=" + attemptId +
            ", examId=" + examId +
            ", examTitle='" + examTitle + '\'' +
            ", status=" + status +
            ", listeningScore=" + listeningScore +
            ", readingScore=" + readingScore +
            ", totalScore=" + totalScore +
            ", correctAnswers=" + correctAnswers +
            ", wrongAnswers=" + wrongAnswers +
            ", skippedAnswers=" + skippedAnswers +
            ", timeSpentSeconds=" + timeSpentSeconds +
            ", completedAt=" + completedAt +
            '}';
    }
}
