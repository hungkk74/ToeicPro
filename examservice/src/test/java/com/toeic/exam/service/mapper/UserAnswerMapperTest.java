package com.toeic.exam.service.mapper;

import static com.toeic.exam.domain.UserAnswerAsserts.*;
import static com.toeic.exam.domain.UserAnswerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserAnswerMapperTest {

    private UserAnswerMapper userAnswerMapper;

    @BeforeEach
    void setUp() {
        userAnswerMapper = new UserAnswerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserAnswerSample1();
        var actual = userAnswerMapper.toEntity(userAnswerMapper.toDto(expected));
        assertUserAnswerAllPropertiesEquals(expected, actual);
    }
}
