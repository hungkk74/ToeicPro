package com.toeic.subscription.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.subscription.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanDTO.class);
        PlanDTO planDTO1 = new PlanDTO();
        planDTO1.setId(1L);
        PlanDTO planDTO2 = new PlanDTO();
        assertThat(planDTO1).isNotEqualTo(planDTO2);
        planDTO2.setId(planDTO1.getId());
        assertThat(planDTO1).isEqualTo(planDTO2);
        planDTO2.setId(2L);
        assertThat(planDTO1).isNotEqualTo(planDTO2);
        planDTO1.setId(null);
        assertThat(planDTO1).isNotEqualTo(planDTO2);
    }
}
