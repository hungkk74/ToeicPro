package com.toeic.exam.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.toeic.exam.domain.enumeration.AnswerOption;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Chi tiết đáp án học viên chọn cho từng câu hỏi
 */
@Entity
@Table(name = "user_answer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserAnswer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_option")
    private AnswerOption selectedOption;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "exam" }, allowSetters = true)
    private ExamAttempt examAttempt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "part", "questionGroup" }, allowSetters = true)
    private Question question;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserAnswer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnswerOption getSelectedOption() {
        return this.selectedOption;
    }

    public UserAnswer selectedOption(AnswerOption selectedOption) {
        this.setSelectedOption(selectedOption);
        return this;
    }

    public void setSelectedOption(AnswerOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Boolean getIsCorrect() {
        return this.isCorrect;
    }

    public UserAnswer isCorrect(Boolean isCorrect) {
        this.setIsCorrect(isCorrect);
        return this;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getTimeSpentSeconds() {
        return this.timeSpentSeconds;
    }

    public UserAnswer timeSpentSeconds(Integer timeSpentSeconds) {
        this.setTimeSpentSeconds(timeSpentSeconds);
        return this;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public ExamAttempt getExamAttempt() {
        return this.examAttempt;
    }

    public void setExamAttempt(ExamAttempt examAttempt) {
        this.examAttempt = examAttempt;
    }

    public UserAnswer examAttempt(ExamAttempt examAttempt) {
        this.setExamAttempt(examAttempt);
        return this;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public UserAnswer question(Question question) {
        this.setQuestion(question);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAnswer)) {
            return false;
        }
        return getId() != null && getId().equals(((UserAnswer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAnswer{" +
            "id=" + getId() +
            ", selectedOption='" + getSelectedOption() + "'" +
            ", isCorrect='" + getIsCorrect() + "'" +
            ", timeSpentSeconds=" + getTimeSpentSeconds() +
            "}";
    }
}
