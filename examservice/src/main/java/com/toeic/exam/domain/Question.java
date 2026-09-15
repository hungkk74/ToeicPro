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
 * Chi tiết từng câu hỏi trắc nghiệm
 */
@Entity
@Table(name = "question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 200)
    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @Lob
    @Column(name = "content")
    private String content;

    @Size(max = 1000)
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Size(max = 1000)
    @Column(name = "audio_url", length = 1000)
    private String audioUrl;

    @Lob
    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Lob
    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Lob
    @Column(name = "option_c", nullable = false)
    private String optionC;

    @Lob
    @Column(name = "option_d")
    private String optionD;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "correct_option", nullable = false)
    private AnswerOption correctOption;

    @Lob
    @Column(name = "explanation")
    private String explanation;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "exam" }, allowSetters = true)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "part" }, allowSetters = true)
    private QuestionGroup questionGroup;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuestionNumber() {
        return this.questionNumber;
    }

    public Question questionNumber(Integer questionNumber) {
        this.setQuestionNumber(questionNumber);
        return this;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getContent() {
        return this.content;
    }

    public Question content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Question imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return this.audioUrl;
    }

    public Question audioUrl(String audioUrl) {
        this.setAudioUrl(audioUrl);
        return this;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getOptionA() {
        return this.optionA;
    }

    public Question optionA(String optionA) {
        this.setOptionA(optionA);
        return this;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return this.optionB;
    }

    public Question optionB(String optionB) {
        this.setOptionB(optionB);
        return this;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return this.optionC;
    }

    public Question optionC(String optionC) {
        this.setOptionC(optionC);
        return this;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return this.optionD;
    }

    public Question optionD(String optionD) {
        this.setOptionD(optionD);
        return this;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public AnswerOption getCorrectOption() {
        return this.correctOption;
    }

    public Question correctOption(AnswerOption correctOption) {
        this.setCorrectOption(correctOption);
        return this;
    }

    public void setCorrectOption(AnswerOption correctOption) {
        this.correctOption = correctOption;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public Question explanation(String explanation) {
        this.setExplanation(explanation);
        return this;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Part getPart() {
        return this.part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Question part(Part part) {
        this.setPart(part);
        return this;
    }

    public QuestionGroup getQuestionGroup() {
        return this.questionGroup;
    }

    public void setQuestionGroup(QuestionGroup questionGroup) {
        this.questionGroup = questionGroup;
    }

    public Question questionGroup(QuestionGroup questionGroup) {
        this.setQuestionGroup(questionGroup);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", questionNumber=" + getQuestionNumber() +
            ", content='" + getContent() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", audioUrl='" + getAudioUrl() + "'" +
            ", optionA='" + getOptionA() + "'" +
            ", optionB='" + getOptionB() + "'" +
            ", optionC='" + getOptionC() + "'" +
            ", optionD='" + getOptionD() + "'" +
            ", correctOption='" + getCorrectOption() + "'" +
            ", explanation='" + getExplanation() + "'" +
            "}";
    }
}
