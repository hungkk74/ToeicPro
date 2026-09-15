package com.toeic.exam.domain;

import com.toeic.exam.domain.enumeration.ExamCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Danh mục đề thi (Full test 200 câu, Mini test, hoặc Practice)
 */
@Entity
@Table(name = "exam")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Exam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ExamCategory category;

    @NotNull
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Size(max = 1000)
    @Column(name = "audio_full_url", length = 1000)
    private String audioFullUrl;

    @NotNull
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Exam code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return this.title;
    }

    public Exam title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExamCategory getCategory() {
        return this.category;
    }

    public Exam category(ExamCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(ExamCategory category) {
        this.category = category;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public Exam durationMinutes(Integer durationMinutes) {
        this.setDurationMinutes(durationMinutes);
        return this;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getTotalQuestions() {
        return this.totalQuestions;
    }

    public Exam totalQuestions(Integer totalQuestions) {
        this.setTotalQuestions(totalQuestions);
        return this;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getAudioFullUrl() {
        return this.audioFullUrl;
    }

    public Exam audioFullUrl(String audioFullUrl) {
        this.setAudioFullUrl(audioFullUrl);
        return this;
    }

    public void setAudioFullUrl(String audioFullUrl) {
        this.audioFullUrl = audioFullUrl;
    }

    public Boolean getIsPublished() {
        return this.isPublished;
    }

    public Exam isPublished(Boolean isPublished) {
        this.setIsPublished(isPublished);
        return this;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Exam createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exam)) {
            return false;
        }
        return getId() != null && getId().equals(((Exam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exam{" +
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
