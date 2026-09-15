package com.toeic.course.service.mapper;

import com.toeic.course.domain.Lesson;
import com.toeic.course.domain.LessonProgress;
import com.toeic.course.service.dto.LessonDTO;
import com.toeic.course.service.dto.LessonProgressDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LessonProgress} and its DTO {@link LessonProgressDTO}.
 */
@Mapper(componentModel = "spring")
public interface LessonProgressMapper extends EntityMapper<LessonProgressDTO, LessonProgress> {
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonTitle")
    LessonProgressDTO toDto(LessonProgress s);

    @Named("lessonTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    LessonDTO toDtoLessonTitle(Lesson lesson);
}
