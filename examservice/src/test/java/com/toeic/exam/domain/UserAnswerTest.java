package com.toeic.exam.domain;

import static com.toeic.exam.domain.ExamAttemptTestSamples.*;
import static com.toeic.exam.domain.QuestionTestSamples.*;
import static com.toeic.exam.domain.UserAnswerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.exam.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserAnswerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAnswer.class);
        UserAnswer userAnswer1 = getUserAnswerSample1();
        UserAnswer userAnswer2 = new UserAnswer();
        assertThat(userAnswer1).isNotEqualTo(userAnswer2);

        userAnswer2.setId(userAnswer1.getId());
        assertThat(userAnswer1).isEqualTo(userAnswer2);

        userAnswer2 = getUserAnswerSample2();
        assertThat(userAnswer1).isNotEqualTo(userAnswer2);
    }

    @Test
    void examAttemptTest() {
        UserAnswer userAnswer = getUserAnswerRandomSampleGenerator();
        ExamAttempt examAttemptBack = getExamAttemptRandomSampleGenerator();

        userAnswer.setExamAttempt(examAttemptBack);
        assertThat(userAnswer.getExamAttempt()).isEqualTo(examAttemptBack);

        userAnswer.examAttempt(null);
        assertThat(userAnswer.getExamAttempt()).isNull();
    }

    @Test
    void questionTest() {
        UserAnswer userAnswer = getUserAnswerRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        userAnswer.setQuestion(questionBack);
        assertThat(userAnswer.getQuestion()).isEqualTo(questionBack);

        userAnswer.question(null);
        assertThat(userAnswer.getQuestion()).isNull();
    }
}
