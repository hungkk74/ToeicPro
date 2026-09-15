package com.toeic.exam.service.mapper;

import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.service.dto.PartDTO;
import com.toeic.exam.service.dto.QuestionGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link QuestionGroup} and its DTO {@link QuestionGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionGroupMapper extends EntityMapper<QuestionGroupDTO, QuestionGroup> {
    @Mapping(target = "part", source = "part", qualifiedByName = "partName")
    QuestionGroupDTO toDto(QuestionGroup s);

    @Named("partName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PartDTO toDtoPartName(Part part);
}
