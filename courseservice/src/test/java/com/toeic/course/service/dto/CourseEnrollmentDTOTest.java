package com.toeic.course.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseEnrollmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseEnrollmentDTO.class);
        CourseEnrollmentDTO courseEnrollmentDTO1 = new CourseEnrollmentDTO();
        courseEnrollmentDTO1.setId(1L);
        CourseEnrollmentDTO courseEnrollmentDTO2 = new CourseEnrollmentDTO();
        assertThat(courseEnrollmentDTO1).isNotEqualTo(courseEnrollmentDTO2);
        courseEnrollmentDTO2.setId(courseEnrollmentDTO1.getId());
        assertThat(courseEnrollmentDTO1).isEqualTo(courseEnrollmentDTO2);
        courseEnrollmentDTO2.setId(2L);
        assertThat(courseEnrollmentDTO1).isNotEqualTo(courseEnrollmentDTO2);
        courseEnrollmentDTO1.setId(null);
        assertThat(courseEnrollmentDTO1).isNotEqualTo(courseEnrollmentDTO2);
    }
}
