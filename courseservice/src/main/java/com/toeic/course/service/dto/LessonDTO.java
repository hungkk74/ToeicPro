package com.toeic.course.service.dto;

import com.toeic.course.domain.enumeration.LessonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.course.domain.Lesson} entity.
 */
@Schema(description = "Chi tiết từng bài học (Video bài giảng, Tài liệu PDF, hoặc Bài tập)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    private LessonType lessonType;

    @Size(max = 1000)
    private String videoUrl;

    @Min(value = 0)
    private Integer durationSeconds;

    @Size(max = 1000)
    private String documentUrl;

    @Lob
    private String content;

    @NotNull
    private Boolean isFreePreview;

    @NotNull
    @Min(value = 1)
    private Integer sortOrder;

    @NotNull
    private ChapterDTO chapter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsFreePreview() {
        return isFreePreview;
    }

    public void setIsFreePreview(Boolean isFreePreview) {
        this.isFreePreview = isFreePreview;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public ChapterDTO getChapter() {
        return chapter;
    }

    public void setChapter(ChapterDTO chapter) {
        this.chapter = chapter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonDTO)) {
            return false;
        }

        LessonDTO lessonDTO = (LessonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lessonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", lessonType='" + getLessonType() + "'" +
            ", videoUrl='" + getVideoUrl() + "'" +
            ", durationSeconds=" + getDurationSeconds() +
            ", documentUrl='" + getDocumentUrl() + "'" +
            ", content='" + getContent() + "'" +
            ", isFreePreview='" + getIsFreePreview() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", chapter=" + getChapter() +
            "}";
    }
}
