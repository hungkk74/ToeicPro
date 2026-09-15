package com.toeic.notification.domain;

import static com.toeic.notification.domain.NotificationLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.notification.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationLog.class);
        NotificationLog notificationLog1 = getNotificationLogSample1();
        NotificationLog notificationLog2 = new NotificationLog();
        assertThat(notificationLog1).isNotEqualTo(notificationLog2);

        notificationLog2.setId(notificationLog1.getId());
        assertThat(notificationLog1).isEqualTo(notificationLog2);

        notificationLog2 = getNotificationLogSample2();
        assertThat(notificationLog1).isNotEqualTo(notificationLog2);
    }
}
