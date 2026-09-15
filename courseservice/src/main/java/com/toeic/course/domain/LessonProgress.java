package com.toeic.course.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Theo dõi tiến độ học tập chi tiết của học viên cho từng bài học
 */
@Entity
@Table(name = "lesson_progress")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonProgress implements Serializable {

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
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Min(value = 0)
    @Column(name = "last_watched_second")
    private Integer lastWatchedSecond;

    @Column(name = "completed_at")
    private Instant completedAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "chapter" }, allowSetters = true)
    private Lesson lesson;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LessonProgress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public LessonProgress userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsCompleted() {
        return this.isCompleted;
    }

    public LessonProgress isCompleted(Boolean isCompleted) {
        this.setIsCompleted(isCompleted);
        return this;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getLastWatchedSecond() {
        return this.lastWatchedSecond;
    }

    public LessonProgress lastWatchedSecond(Integer lastWatchedSecond) {
        this.setLastWatchedSecond(lastWatchedSecond);
        return this;
    }

    public void setLastWatchedSecond(Integer lastWatchedSecond) {
        this.lastWatchedSecond = lastWatchedSecond;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public LessonProgress completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public LessonProgress updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public LessonProgress lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonProgress)) {
            return false;
        }
        return getId() != null && getId().equals(((LessonProgress) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonProgress{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", isCompleted='" + getIsCompleted() + "'" +
            ", lastWatchedSecond=" + getLastWatchedSecond() +
            ", completedAt='" + getCompletedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
