package com.toeic.course.service.mapper;

import com.toeic.course.domain.Chapter;
import com.toeic.course.domain.Course;
import com.toeic.course.service.dto.ChapterDTO;
import com.toeic.course.service.dto.CourseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Chapter} and its DTO {@link ChapterDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChapterMapper extends EntityMapper<ChapterDTO, Chapter> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseTitle")
    ChapterDTO toDto(Chapter s);

    @Named("courseTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    CourseDTO toDtoCourseTitle(Course course);
}
