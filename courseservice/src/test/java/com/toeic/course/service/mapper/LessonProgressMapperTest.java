package com.toeic.course.service.mapper;

import static com.toeic.course.domain.LessonProgressAsserts.*;
import static com.toeic.course.domain.LessonProgressTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LessonProgressMapperTest {

    private LessonProgressMapper lessonProgressMapper;

    @BeforeEach
    void setUp() {
        lessonProgressMapper = new LessonProgressMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLessonProgressSample1();
        var actual = lessonProgressMapper.toEntity(lessonProgressMapper.toDto(expected));
        assertLessonProgressAllPropertiesEquals(expected, actual);
    }
}
