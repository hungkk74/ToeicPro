package com.toeic.exam.service.mapper;

import static com.toeic.exam.domain.ExamAttemptAsserts.*;
import static com.toeic.exam.domain.ExamAttemptTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExamAttemptMapperTest {

    private ExamAttemptMapper examAttemptMapper;

    @BeforeEach
    void setUp() {
        examAttemptMapper = new ExamAttemptMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExamAttemptSample1();
        var actual = examAttemptMapper.toEntity(examAttemptMapper.toDto(expected));
        assertExamAttemptAllPropertiesEquals(expected, actual);
    }
}
