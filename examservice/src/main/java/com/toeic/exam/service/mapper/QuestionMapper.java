package com.toeic.exam.service.mapper;

import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.service.dto.PartDTO;
import com.toeic.exam.service.dto.QuestionDTO;
import com.toeic.exam.service.dto.QuestionGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Question} and its DTO {@link QuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionMapper extends EntityMapper<QuestionDTO, Question> {
    @Mapping(target = "part", source = "part", qualifiedByName = "partName")
    @Mapping(target = "questionGroup", source = "questionGroup", qualifiedByName = "questionGroupId")
    QuestionDTO toDto(Question s);

    @Named("partName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PartDTO toDtoPartName(Part part);

    @Named("questionGroupId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionGroupDTO toDtoQuestionGroupId(QuestionGroup questionGroup);
}
