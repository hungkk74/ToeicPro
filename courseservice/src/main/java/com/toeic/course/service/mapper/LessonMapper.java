package com.toeic.course.service.mapper;

import com.toeic.course.domain.Chapter;
import com.toeic.course.domain.Lesson;
import com.toeic.course.service.dto.ChapterDTO;
import com.toeic.course.service.dto.LessonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Lesson} and its DTO {@link LessonDTO}.
 */
@Mapper(componentModel = "spring")
public interface LessonMapper extends EntityMapper<LessonDTO, Lesson> {
    @Mapping(target = "chapter", source = "chapter", qualifiedByName = "chapterTitle")
    LessonDTO toDto(Lesson s);

    @Named("chapterTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    ChapterDTO toDtoChapterTitle(Chapter chapter);
}
