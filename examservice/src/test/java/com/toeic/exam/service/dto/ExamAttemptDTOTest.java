package com.toeic.exam.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.exam.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamAttemptDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamAttemptDTO.class);
        ExamAttemptDTO examAttemptDTO1 = new ExamAttemptDTO();
        examAttemptDTO1.setId(1L);
        ExamAttemptDTO examAttemptDTO2 = new ExamAttemptDTO();
        assertThat(examAttemptDTO1).isNotEqualTo(examAttemptDTO2);
        examAttemptDTO2.setId(examAttemptDTO1.getId());
        assertThat(examAttemptDTO1).isEqualTo(examAttemptDTO2);
        examAttemptDTO2.setId(2L);
        assertThat(examAttemptDTO1).isNotEqualTo(examAttemptDTO2);
        examAttemptDTO1.setId(null);
        assertThat(examAttemptDTO1).isNotEqualTo(examAttemptDTO2);
    }
}
