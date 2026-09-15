package com.toeic.course.domain;

import static com.toeic.course.domain.LessonProgressTestSamples.*;
import static com.toeic.course.domain.LessonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LessonProgress.class);
        LessonProgress lessonProgress1 = getLessonProgressSample1();
        LessonProgress lessonProgress2 = new LessonProgress();
        assertThat(lessonProgress1).isNotEqualTo(lessonProgress2);

        lessonProgress2.setId(lessonProgress1.getId());
        assertThat(lessonProgress1).isEqualTo(lessonProgress2);

        lessonProgress2 = getLessonProgressSample2();
        assertThat(lessonProgress1).isNotEqualTo(lessonProgress2);
    }

    @Test
    void lessonTest() {
        LessonProgress lessonProgress = getLessonProgressRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        lessonProgress.setLesson(lessonBack);
        assertThat(lessonProgress.getLesson()).isEqualTo(lessonBack);

        lessonProgress.lesson(null);
        assertThat(lessonProgress.getLesson()).isNull();
    }
}
