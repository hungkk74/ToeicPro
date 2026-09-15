package com.toeic.notification.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.notification.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationTemplateDTO.class);
        NotificationTemplateDTO notificationTemplateDTO1 = new NotificationTemplateDTO();
        notificationTemplateDTO1.setId(1L);
        NotificationTemplateDTO notificationTemplateDTO2 = new NotificationTemplateDTO();
        assertThat(notificationTemplateDTO1).isNotEqualTo(notificationTemplateDTO2);
        notificationTemplateDTO2.setId(notificationTemplateDTO1.getId());
        assertThat(notificationTemplateDTO1).isEqualTo(notificationTemplateDTO2);
        notificationTemplateDTO2.setId(2L);
        assertThat(notificationTemplateDTO1).isNotEqualTo(notificationTemplateDTO2);
        notificationTemplateDTO1.setId(null);
        assertThat(notificationTemplateDTO1).isNotEqualTo(notificationTemplateDTO2);
    }
}
