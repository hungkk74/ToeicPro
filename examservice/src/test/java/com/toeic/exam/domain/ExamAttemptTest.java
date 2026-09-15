package com.toeic.exam.domain;

import static com.toeic.exam.domain.ExamAttemptTestSamples.*;
import static com.toeic.exam.domain.ExamTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.exam.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamAttemptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamAttempt.class);
        ExamAttempt examAttempt1 = getExamAttemptSample1();
        ExamAttempt examAttempt2 = new ExamAttempt();
        assertThat(examAttempt1).isNotEqualTo(examAttempt2);

        examAttempt2.setId(examAttempt1.getId());
        assertThat(examAttempt1).isEqualTo(examAttempt2);

        examAttempt2 = getExamAttemptSample2();
        assertThat(examAttempt1).isNotEqualTo(examAttempt2);
    }

    @Test
    void examTest() {
        ExamAttempt examAttempt = getExamAttemptRandomSampleGenerator();
        Exam examBack = getExamRandomSampleGenerator();

        examAttempt.setExam(examBack);
        assertThat(examAttempt.getExam()).isEqualTo(examBack);

        examAttempt.exam(null);
        assertThat(examAttempt.getExam()).isNull();
    }
}
