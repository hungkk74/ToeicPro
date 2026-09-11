package com.toeic.payment.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.payment.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentWebhookLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentWebhookLogDTO.class);
        PaymentWebhookLogDTO paymentWebhookLogDTO1 = new PaymentWebhookLogDTO();
        paymentWebhookLogDTO1.setId(1L);
        PaymentWebhookLogDTO paymentWebhookLogDTO2 = new PaymentWebhookLogDTO();
        assertThat(paymentWebhookLogDTO1).isNotEqualTo(paymentWebhookLogDTO2);
        paymentWebhookLogDTO2.setId(paymentWebhookLogDTO1.getId());
        assertThat(paymentWebhookLogDTO1).isEqualTo(paymentWebhookLogDTO2);
        paymentWebhookLogDTO2.setId(2L);
        assertThat(paymentWebhookLogDTO1).isNotEqualTo(paymentWebhookLogDTO2);
        paymentWebhookLogDTO1.setId(null);
        assertThat(paymentWebhookLogDTO1).isNotEqualTo(paymentWebhookLogDTO2);
    }
}
