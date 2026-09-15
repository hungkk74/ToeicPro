package com.toeic.exam.service.dto;

import com.toeic.exam.domain.enumeration.AnswerOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.exam.domain.UserAnswer} entity.
 */
@Schema(description = "Chi tiết đáp án học viên chọn cho từng câu hỏi")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserAnswerDTO implements Serializable {

    private Long id;

    private AnswerOption selectedOption;

    private Boolean isCorrect;

    private Integer timeSpentSeconds;

    @NotNull
    private ExamAttemptDTO examAttempt;

    @NotNull
    private QuestionDTO question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnswerOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(AnswerOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public ExamAttemptDTO getExamAttempt() {
        return examAttempt;
    }

    public void setExamAttempt(ExamAttemptDTO examAttempt) {
        this.examAttempt = examAttempt;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAnswerDTO)) {
            return false;
        }

        UserAnswerDTO userAnswerDTO = (UserAnswerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userAnswerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAnswerDTO{" +
            "id=" + getId() +
            ", selectedOption='" + getSelectedOption() + "'" +
            ", isCorrect='" + getIsCorrect() + "'" +
            ", timeSpentSeconds=" + getTimeSpentSeconds() +
            ", examAttempt=" + getExamAttempt() +
            ", question=" + getQuestion() +
            "}";
    }
}
