package com.toeic.exam.domain;

import static com.toeic.exam.domain.ExamTestSamples.*;
import static com.toeic.exam.domain.PartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.exam.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Part.class);
        Part part1 = getPartSample1();
        Part part2 = new Part();
        assertThat(part1).isNotEqualTo(part2);

        part2.setId(part1.getId());
        assertThat(part1).isEqualTo(part2);

        part2 = getPartSample2();
        assertThat(part1).isNotEqualTo(part2);
    }

    @Test
    void examTest() {
        Part part = getPartRandomSampleGenerator();
        Exam examBack = getExamRandomSampleGenerator();

        part.setExam(examBack);
        assertThat(part.getExam()).isEqualTo(examBack);

        part.exam(null);
        assertThat(part.getExam()).isNull();
    }
}
