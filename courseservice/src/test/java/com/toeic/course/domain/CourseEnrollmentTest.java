package com.toeic.course.domain;

import static com.toeic.course.domain.CourseEnrollmentTestSamples.*;
import static com.toeic.course.domain.CourseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseEnrollmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseEnrollment.class);
        CourseEnrollment courseEnrollment1 = getCourseEnrollmentSample1();
        CourseEnrollment courseEnrollment2 = new CourseEnrollment();
        assertThat(courseEnrollment1).isNotEqualTo(courseEnrollment2);

        courseEnrollment2.setId(courseEnrollment1.getId());
        assertThat(courseEnrollment1).isEqualTo(courseEnrollment2);

        courseEnrollment2 = getCourseEnrollmentSample2();
        assertThat(courseEnrollment1).isNotEqualTo(courseEnrollment2);
    }

    @Test
    void courseTest() {
        CourseEnrollment courseEnrollment = getCourseEnrollmentRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        courseEnrollment.setCourse(courseBack);
        assertThat(courseEnrollment.getCourse()).isEqualTo(courseBack);

        courseEnrollment.course(null);
        assertThat(courseEnrollment.getCourse()).isNull();
    }
}
