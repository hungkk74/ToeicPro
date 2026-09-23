package com.toeic.exam.service.dto.create;

import java.io.Serializable;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PartCreateDTO implements Serializable {

    @NotNull
    private Integer partNumber;

    @NotNull
    @Size(max = 255)
    private String name;

    private List<QuestionGroupCreateDTO> questionGroups;

    public Integer getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Integer partNumber) {
        this.partNumber = partNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QuestionGroupCreateDTO> getQuestionGroups() {
        return questionGroups;
    }

    public void setQuestionGroups(List<QuestionGroupCreateDTO> questionGroups) {
        this.questionGroups = questionGroups;
    }
}
