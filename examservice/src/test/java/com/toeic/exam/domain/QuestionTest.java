package com.toeic.exam.domain;

import static com.toeic.exam.domain.PartTestSamples.*;
import static com.toeic.exam.domain.QuestionGroupTestSamples.*;
import static com.toeic.exam.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.exam.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = getQuestionSample1();
        Question question2 = new Question();
        assertThat(question1).isNotEqualTo(question2);

        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);

        question2 = getQuestionSample2();
        assertThat(question1).isNotEqualTo(question2);
    }

    @Test
    void partTest() {
        Question question = getQuestionRandomSampleGenerator();
        Part partBack = getPartRandomSampleGenerator();

        question.setPart(partBack);
        assertThat(question.getPart()).isEqualTo(partBack);

        question.part(null);
        assertThat(question.getPart()).isNull();
    }

    @Test
    void questionGroupTest() {
        Question question = getQuestionRandomSampleGenerator();
        QuestionGroup questionGroupBack = getQuestionGroupRandomSampleGenerator();

        question.setQuestionGroup(questionGroupBack);
        assertThat(question.getQuestionGroup()).isEqualTo(questionGroupBack);

        question.questionGroup(null);
        assertThat(question.getQuestionGroup()).isNull();
    }
}
