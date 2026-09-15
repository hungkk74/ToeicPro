package com.toeic.exam.service.mapper;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.service.dto.ExamAttemptDTO;
import com.toeic.exam.service.dto.ExamDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExamAttempt} and its DTO {@link ExamAttemptDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamAttemptMapper extends EntityMapper<ExamAttemptDTO, ExamAttempt> {
    @Mapping(target = "exam", source = "exam", qualifiedByName = "examTitle")
    ExamAttemptDTO toDto(ExamAttempt s);

    @Named("examTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ExamDTO toDtoExamTitle(Exam exam);
}
