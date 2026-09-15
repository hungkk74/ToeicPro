package com.toeic.notification.service.dto;

import com.toeic.notification.domain.enumeration.NotificationChannel;
import com.toeic.notification.domain.enumeration.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.notification.domain.NotificationLog} entity.
 */
@Schema(description = "Nhật ký lưu vết và audit gửi thông báo qua kênh ngoài (Email / FCM / SMS)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 36)
    private String userId;

    @NotNull
    @Size(max = 255)
    private String recipient;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private NotificationStatus status;

    @Lob
    private String errorMessage;

    private Integer retryCount;

    private Instant sentAt;

    @NotNull
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationLogDTO)) {
            return false;
        }

        NotificationLogDTO notificationLogDTO = (NotificationLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationLogDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", recipient='" + getRecipient() + "'" +
            ", channel='" + getChannel() + "'" +
            ", status='" + getStatus() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", retryCount=" + getRetryCount() +
            ", sentAt='" + getSentAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
