package com.toeic.exam.service.dto.take;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartTakeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer partNumber;
    private String name;
    private Integer totalQuestions;
    private List<QuestionGroupTakeDTO> groups = new ArrayList<>();
    private List<QuestionTakeDTO> standaloneQuestions = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getPartNumber() { return partNumber; }
    public void setPartNumber(Integer partNumber) { this.partNumber = partNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public List<QuestionGroupTakeDTO> getGroups() { return groups; }
    public void setGroups(List<QuestionGroupTakeDTO> groups) { this.groups = groups; }

    public List<QuestionTakeDTO> getStandaloneQuestions() { return standaloneQuestions; }
    public void setStandaloneQuestions(List<QuestionTakeDTO> standaloneQuestions) { this.standaloneQuestions = standaloneQuestions; }
}
