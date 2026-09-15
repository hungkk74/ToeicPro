package com.toeic.course.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.toeic.course.domain.enumeration.LessonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Chi tiết từng bài học (Video bài giảng, Tài liệu PDF, hoặc Bài tập)
 */
@Entity
@Table(name = "lesson")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Lesson implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", nullable = false)
    private LessonType lessonType;

    @Size(max = 1000)
    @Column(name = "video_url", length = 1000)
    private String videoUrl;

    @Min(value = 0)
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Size(max = 1000)
    @Column(name = "document_url", length = 1000)
    private String documentUrl;

    @Lob
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "is_free_preview", nullable = false)
    private Boolean isFreePreview;

    @NotNull
    @Min(value = 1)
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private Chapter chapter;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Lesson id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Lesson title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LessonType getLessonType() {
        return this.lessonType;
    }

    public Lesson lessonType(LessonType lessonType) {
        this.setLessonType(lessonType);
        return this;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public Lesson videoUrl(String videoUrl) {
        this.setVideoUrl(videoUrl);
        return this;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getDurationSeconds() {
        return this.durationSeconds;
    }

    public Lesson durationSeconds(Integer durationSeconds) {
        this.setDurationSeconds(durationSeconds);
        return this;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getDocumentUrl() {
        return this.documentUrl;
    }

    public Lesson documentUrl(String documentUrl) {
        this.setDocumentUrl(documentUrl);
        return this;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getContent() {
        return this.content;
    }

    public Lesson content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsFreePreview() {
        return this.isFreePreview;
    }

    public Lesson isFreePreview(Boolean isFreePreview) {
        this.setIsFreePreview(isFreePreview);
        return this;
    }

    public void setIsFreePreview(Boolean isFreePreview) {
        this.isFreePreview = isFreePreview;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public Lesson sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Chapter getChapter() {
        return this.chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public Lesson chapter(Chapter chapter) {
        this.setChapter(chapter);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lesson)) {
            return false;
        }
        return getId() != null && getId().equals(((Lesson) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lesson{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", lessonType='" + getLessonType() + "'" +
            ", videoUrl='" + getVideoUrl() + "'" +
            ", durationSeconds=" + getDurationSeconds() +
            ", documentUrl='" + getDocumentUrl() + "'" +
            ", content='" + getContent() + "'" +
            ", isFreePreview='" + getIsFreePreview() + "'" +
            ", sortOrder=" + getSortOrder() +
            "}";
    }
}
