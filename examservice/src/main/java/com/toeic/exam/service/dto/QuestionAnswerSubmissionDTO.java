package com.toeic.exam.service.dto;

import com.toeic.exam.domain.enumeration.AnswerOption;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO representing a student's answer for a single question.
 */
public class QuestionAnswerSubmissionDTO implements Serializable {

    @NotNull
    private Long questionId;

    private AnswerOption selectedOption;

    private Integer timeSpentSeconds;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public AnswerOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(AnswerOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionAnswerSubmissionDTO that)) return false;
        return Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId);
    }

    @Override
    public String toString() {
        return "QuestionAnswerSubmissionDTO{" +
            "questionId=" + questionId +
            ", selectedOption=" + selectedOption +
            ", timeSpentSeconds=" + timeSpentSeconds +
            '}';
    }
}
