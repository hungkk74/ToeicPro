package com.toeic.subscription.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.subscription.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubscriptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubscriptionDTO.class);
        SubscriptionDTO subscriptionDTO1 = new SubscriptionDTO();
        subscriptionDTO1.setId(1L);
        SubscriptionDTO subscriptionDTO2 = new SubscriptionDTO();
        assertThat(subscriptionDTO1).isNotEqualTo(subscriptionDTO2);
        subscriptionDTO2.setId(subscriptionDTO1.getId());
        assertThat(subscriptionDTO1).isEqualTo(subscriptionDTO2);
        subscriptionDTO2.setId(2L);
        assertThat(subscriptionDTO1).isNotEqualTo(subscriptionDTO2);
        subscriptionDTO1.setId(null);
        assertThat(subscriptionDTO1).isNotEqualTo(subscriptionDTO2);
    }
}
