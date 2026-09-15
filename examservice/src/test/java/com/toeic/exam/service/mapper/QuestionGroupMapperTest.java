package com.toeic.exam.service.mapper;

import static com.toeic.exam.domain.QuestionGroupAsserts.*;
import static com.toeic.exam.domain.QuestionGroupTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionGroupMapperTest {

    private QuestionGroupMapper questionGroupMapper;

    @BeforeEach
    void setUp() {
        questionGroupMapper = new QuestionGroupMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getQuestionGroupSample1();
        var actual = questionGroupMapper.toEntity(questionGroupMapper.toDto(expected));
        assertQuestionGroupAllPropertiesEquals(expected, actual);
    }
}
