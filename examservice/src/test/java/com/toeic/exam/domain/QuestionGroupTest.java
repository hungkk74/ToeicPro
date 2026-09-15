package com.toeic.exam.domain;

import static com.toeic.exam.domain.PartTestSamples.*;
import static com.toeic.exam.domain.QuestionGroupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.exam.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionGroup.class);
        QuestionGroup questionGroup1 = getQuestionGroupSample1();
        QuestionGroup questionGroup2 = new QuestionGroup();
        assertThat(questionGroup1).isNotEqualTo(questionGroup2);

        questionGroup2.setId(questionGroup1.getId());
        assertThat(questionGroup1).isEqualTo(questionGroup2);

        questionGroup2 = getQuestionGroupSample2();
        assertThat(questionGroup1).isNotEqualTo(questionGroup2);
    }

    @Test
    void partTest() {
        QuestionGroup questionGroup = getQuestionGroupRandomSampleGenerator();
        Part partBack = getPartRandomSampleGenerator();

        questionGroup.setPart(partBack);
        assertThat(questionGroup.getPart()).isEqualTo(partBack);

        questionGroup.part(null);
        assertThat(questionGroup.getPart()).isNull();
    }
}
