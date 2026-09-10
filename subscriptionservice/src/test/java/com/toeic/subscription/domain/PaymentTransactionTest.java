package com.toeic.subscription.domain;

import static com.toeic.subscription.domain.PaymentTransactionTestSamples.*;
import static com.toeic.subscription.domain.SubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.subscription.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentTransaction.class);
        PaymentTransaction paymentTransaction1 = getPaymentTransactionSample1();
        PaymentTransaction paymentTransaction2 = new PaymentTransaction();
        assertThat(paymentTransaction1).isNotEqualTo(paymentTransaction2);

        paymentTransaction2.setId(paymentTransaction1.getId());
        assertThat(paymentTransaction1).isEqualTo(paymentTransaction2);

        paymentTransaction2 = getPaymentTransactionSample2();
        assertThat(paymentTransaction1).isNotEqualTo(paymentTransaction2);
    }

    @Test
    void subscriptionTest() {
        PaymentTransaction paymentTransaction = getPaymentTransactionRandomSampleGenerator();
        Subscription subscriptionBack = getSubscriptionRandomSampleGenerator();

        paymentTransaction.setSubscription(subscriptionBack);
        assertThat(paymentTransaction.getSubscription()).isEqualTo(subscriptionBack);

        paymentTransaction.subscription(null);
        assertThat(paymentTransaction.getSubscription()).isNull();
    }
}
