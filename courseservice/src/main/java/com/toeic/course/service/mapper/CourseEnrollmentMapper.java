package com.toeic.course.service.mapper;

import com.toeic.course.domain.Course;
import com.toeic.course.domain.CourseEnrollment;
import com.toeic.course.service.dto.CourseDTO;
import com.toeic.course.service.dto.CourseEnrollmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseEnrollment} and its DTO {@link CourseEnrollmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseEnrollmentMapper extends EntityMapper<CourseEnrollmentDTO, CourseEnrollment> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseTitle")
    CourseEnrollmentDTO toDto(CourseEnrollment s);

    @Named("courseTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    CourseDTO toDtoCourseTitle(Course course);
}
