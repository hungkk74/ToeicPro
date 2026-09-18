package com.toeic.exam.service.dto.take;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamTakeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String title;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private String audioFullUrl;
    private List<PartTakeDTO> parts = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public String getAudioFullUrl() { return audioFullUrl; }
    public void setAudioFullUrl(String audioFullUrl) { this.audioFullUrl = audioFullUrl; }

    public List<PartTakeDTO> getParts() { return parts; }
    public void setParts(List<PartTakeDTO> parts) { this.parts = parts; }
}
