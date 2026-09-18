package com.toeic.exam.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO representing a full exam submission with student answers.
 */
public class ExamSubmissionDTO implements Serializable {

    private Integer timeSpentSeconds;

    @NotEmpty
    @Valid
    private List<QuestionAnswerSubmissionDTO> answers;

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public List<QuestionAnswerSubmissionDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswerSubmissionDTO> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "ExamSubmissionDTO{" +
            "timeSpentSeconds=" + timeSpentSeconds +
            ", answersCount=" + (answers != null ? answers.size() : 0) +
            '}';
    }
}
