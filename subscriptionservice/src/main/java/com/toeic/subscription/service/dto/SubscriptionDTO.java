package com.toeic.subscription.service.dto;

import com.toeic.subscription.domain.enumeration.SubscriptionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.subscription.domain.Subscription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubscriptionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 36)
    private String userId;

    @NotNull
    private SubscriptionStatus status;

    @NotNull
    private Instant startsAt;

    @NotNull
    private Instant expiresAt;

    private Instant createdAt;

    private PlanDTO plan;

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

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public PlanDTO getPlan() {
        return plan;
    }

    public void setPlan(PlanDTO plan) {
        this.plan = plan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionDTO)) {
            return false;
        }

        SubscriptionDTO subscriptionDTO = (SubscriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subscriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubscriptionDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", status='" + getStatus() + "'" +
            ", startsAt='" + getStartsAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", plan=" + getPlan() +
            "}";
    }
}
