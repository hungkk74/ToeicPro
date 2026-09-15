package com.toeic.exam.service.mapper;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.Part;
import com.toeic.exam.service.dto.ExamDTO;
import com.toeic.exam.service.dto.PartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Part} and its DTO {@link PartDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartMapper extends EntityMapper<PartDTO, Part> {
    @Mapping(target = "exam", source = "exam", qualifiedByName = "examTitle")
    PartDTO toDto(Part s);

    @Named("examTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ExamDTO toDtoExamTitle(Exam exam);
}
