package com.toeic.subscription.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.subscription.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTransactionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentTransactionDTO.class);
        PaymentTransactionDTO paymentTransactionDTO1 = new PaymentTransactionDTO();
        paymentTransactionDTO1.setId(1L);
        PaymentTransactionDTO paymentTransactionDTO2 = new PaymentTransactionDTO();
        assertThat(paymentTransactionDTO1).isNotEqualTo(paymentTransactionDTO2);
        paymentTransactionDTO2.setId(paymentTransactionDTO1.getId());
        assertThat(paymentTransactionDTO1).isEqualTo(paymentTransactionDTO2);
        paymentTransactionDTO2.setId(2L);
        assertThat(paymentTransactionDTO1).isNotEqualTo(paymentTransactionDTO2);
        paymentTransactionDTO1.setId(null);
        assertThat(paymentTransactionDTO1).isNotEqualTo(paymentTransactionDTO2);
    }
}
