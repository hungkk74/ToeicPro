package com.toeic.exam.service.mapper;

import static com.toeic.exam.domain.PartAsserts.*;
import static com.toeic.exam.domain.PartTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartMapperTest {

    private PartMapper partMapper;

    @BeforeEach
    void setUp() {
        partMapper = new PartMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPartSample1();
        var actual = partMapper.toEntity(partMapper.toDto(expected));
        assertPartAllPropertiesEquals(expected, actual);
    }
}
