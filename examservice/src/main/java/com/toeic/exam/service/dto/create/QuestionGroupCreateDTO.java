package com.toeic.exam.service.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

public class QuestionGroupCreateDTO implements Serializable {

    private String passageText;

    @Size(max = 1000)
    private String imageUrl;

    @Size(max = 1000)
    private String audioUrl;

    @Valid
    private List<QuestionCreateDTO> questions;

    public String getPassageText() {
        return passageText;
    }

    public void setPassageText(String passageText) {
        this.passageText = passageText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public List<QuestionCreateDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionCreateDTO> questions) {
        this.questions = questions;
    }
}
