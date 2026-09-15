package com.toeic.exam.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Nhóm câu hỏi (Dùng cho Part 3, 4, 6, 7 dùng chung bài đọc / audio hội thoại)
 */
@Entity
@Table(name = "question_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "passage_text")
    private String passageText;

    @Size(max = 1000)
    @Column(name = "audio_url", length = 1000)
    private String audioUrl;

    @Size(max = 1000)
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "exam" }, allowSetters = true)
    private Part part;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public QuestionGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassageText() {
        return this.passageText;
    }

    public QuestionGroup passageText(String passageText) {
        this.setPassageText(passageText);
        return this;
    }

    public void setPassageText(String passageText) {
        this.passageText = passageText;
    }

    public String getAudioUrl() {
        return this.audioUrl;
    }

    public QuestionGroup audioUrl(String audioUrl) {
        this.setAudioUrl(audioUrl);
        return this;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public QuestionGroup imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Part getPart() {
        return this.part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public QuestionGroup part(Part part) {
        this.setPart(part);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionGroup)) {
            return false;
        }
        return getId() != null && getId().equals(((QuestionGroup) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionGroup{" +
            "id=" + getId() +
            ", passageText='" + getPassageText() + "'" +
            ", audioUrl='" + getAudioUrl() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
