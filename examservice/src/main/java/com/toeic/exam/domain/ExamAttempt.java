package com.toeic.exam.domain;

import com.toeic.exam.domain.enumeration.AttemptStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Lịch sử lần làm bài thi của học viên
 */
@Entity
@Table(name = "exam_attempt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamAttempt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 36)
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttemptStatus status;

    @Min(value = 5)
    @Max(value = 495)
    @Column(name = "listening_score")
    private Integer listeningScore;

    @Min(value = 5)
    @Max(value = 495)
    @Column(name = "reading_score")
    private Integer readingScore;

    @Min(value = 10)
    @Max(value = 990)
    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "wrong_answers")
    private Integer wrongAnswers;

    @Column(name = "skipped_answers")
    private Integer skippedAnswers;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @ManyToOne(optional = false)
    @NotNull
    private Exam exam;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExamAttempt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public ExamAttempt userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AttemptStatus getStatus() {
        return this.status;
    }

    public ExamAttempt status(AttemptStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Integer getListeningScore() {
        return this.listeningScore;
    }

    public ExamAttempt listeningScore(Integer listeningScore) {
        this.setListeningScore(listeningScore);
        return this;
    }

    public void setListeningScore(Integer listeningScore) {
        this.listeningScore = listeningScore;
    }

    public Integer getReadingScore() {
        return this.readingScore;
    }

    public ExamAttempt readingScore(Integer readingScore) {
        this.setReadingScore(readingScore);
        return this;
    }

    public void setReadingScore(Integer readingScore) {
        this.readingScore = readingScore;
    }

    public Integer getTotalScore() {
        return this.totalScore;
    }

    public ExamAttempt totalScore(Integer totalScore) {
        this.setTotalScore(totalScore);
        return this;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getCorrectAnswers() {
        return this.correctAnswers;
    }

    public ExamAttempt correctAnswers(Integer correctAnswers) {
        this.setCorrectAnswers(correctAnswers);
        return this;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getWrongAnswers() {
        return this.wrongAnswers;
    }

    public ExamAttempt wrongAnswers(Integer wrongAnswers) {
        this.setWrongAnswers(wrongAnswers);
        return this;
    }

    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Integer getSkippedAnswers() {
        return this.skippedAnswers;
    }

    public ExamAttempt skippedAnswers(Integer skippedAnswers) {
        this.setSkippedAnswers(skippedAnswers);
        return this;
    }

    public void setSkippedAnswers(Integer skippedAnswers) {
        this.skippedAnswers = skippedAnswers;
    }

    public Integer getTimeSpentSeconds() {
        return this.timeSpentSeconds;
    }

    public ExamAttempt timeSpentSeconds(Integer timeSpentSeconds) {
        this.setTimeSpentSeconds(timeSpentSeconds);
        return this;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public ExamAttempt startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public ExamAttempt completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Exam getExam() {
        return this.exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public ExamAttempt exam(Exam exam) {
        this.setExam(exam);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamAttempt)) {
            return false;
        }
        return getId() != null && getId().equals(((ExamAttempt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamAttempt{" +
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
            "}";
    }
}
