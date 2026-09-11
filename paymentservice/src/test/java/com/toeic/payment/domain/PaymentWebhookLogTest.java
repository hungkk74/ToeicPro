package com.toeic.payment.domain;

import static com.toeic.payment.domain.PaymentTransactionTestSamples.*;
import static com.toeic.payment.domain.PaymentWebhookLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.payment.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentWebhookLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentWebhookLog.class);
        PaymentWebhookLog paymentWebhookLog1 = getPaymentWebhookLogSample1();
        PaymentWebhookLog paymentWebhookLog2 = new PaymentWebhookLog();
        assertThat(paymentWebhookLog1).isNotEqualTo(paymentWebhookLog2);

        paymentWebhookLog2.setId(paymentWebhookLog1.getId());
        assertThat(paymentWebhookLog1).isEqualTo(paymentWebhookLog2);

        paymentWebhookLog2 = getPaymentWebhookLogSample2();
        assertThat(paymentWebhookLog1).isNotEqualTo(paymentWebhookLog2);
    }

    @Test
    void paymentTransactionTest() {
        PaymentWebhookLog paymentWebhookLog = getPaymentWebhookLogRandomSampleGenerator();
        PaymentTransaction paymentTransactionBack = getPaymentTransactionRandomSampleGenerator();

        paymentWebhookLog.setPaymentTransaction(paymentTransactionBack);
        assertThat(paymentWebhookLog.getPaymentTransaction()).isEqualTo(paymentTransactionBack);

        paymentWebhookLog.paymentTransaction(null);
        assertThat(paymentWebhookLog.getPaymentTransaction()).isNull();
    }
}
