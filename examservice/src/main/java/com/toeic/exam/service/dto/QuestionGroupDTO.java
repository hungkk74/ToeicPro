package com.toeic.exam.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.exam.domain.QuestionGroup} entity.
 */
@Schema(description = "Nhóm câu hỏi (Dùng cho Part 3, 4, 6, 7 dùng chung bài đọc / audio hội thoại)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QuestionGroupDTO implements Serializable {

    private Long id;

    @Lob
    private String passageText;

    @Size(max = 1000)
    private String audioUrl;

    @Size(max = 1000)
    private String imageUrl;

    @NotNull
    private PartDTO part;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassageText() {
        return passageText;
    }

    public void setPassageText(String passageText) {
        this.passageText = passageText;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public PartDTO getPart() {
        return part;
    }

    public void setPart(PartDTO part) {
        this.part = part;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuestionGroupDTO)) {
            return false;
        }

        QuestionGroupDTO questionGroupDTO = (QuestionGroupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, questionGroupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QuestionGroupDTO{" +
            "id=" + getId() +
            ", passageText='" + getPassageText() + "'" +
            ", audioUrl='" + getAudioUrl() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", part=" + getPart() +
            "}";
    }
}
