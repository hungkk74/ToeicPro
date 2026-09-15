package com.toeic.course.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonProgressDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LessonProgressDTO.class);
        LessonProgressDTO lessonProgressDTO1 = new LessonProgressDTO();
        lessonProgressDTO1.setId(1L);
        LessonProgressDTO lessonProgressDTO2 = new LessonProgressDTO();
        assertThat(lessonProgressDTO1).isNotEqualTo(lessonProgressDTO2);
        lessonProgressDTO2.setId(lessonProgressDTO1.getId());
        assertThat(lessonProgressDTO1).isEqualTo(lessonProgressDTO2);
        lessonProgressDTO2.setId(2L);
        assertThat(lessonProgressDTO1).isNotEqualTo(lessonProgressDTO2);
        lessonProgressDTO1.setId(null);
        assertThat(lessonProgressDTO1).isNotEqualTo(lessonProgressDTO2);
    }
}
