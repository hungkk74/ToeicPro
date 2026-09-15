package com.toeic.exam.service.mapper;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.service.dto.ExamDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Exam} and its DTO {@link ExamDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamMapper extends EntityMapper<ExamDTO, Exam> {}
