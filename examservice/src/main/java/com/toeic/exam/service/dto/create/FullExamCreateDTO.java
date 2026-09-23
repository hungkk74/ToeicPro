package com.toeic.exam.service.dto.create;

import com.toeic.exam.domain.enumeration.ExamCategory;
import java.io.Serializable;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FullExamCreateDTO implements Serializable {

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

    private List<PartCreateDTO> parts;

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

    public List<PartCreateDTO> getParts() {
        return parts;
    }

    public void setParts(List<PartCreateDTO> parts) {
        this.parts = parts;
    }
}
