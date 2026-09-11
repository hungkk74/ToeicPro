package com.toeic.payment.service.dto;

import com.toeic.payment.domain.enumeration.PaymentGateway;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.payment.domain.PaymentWebhookLog} entity.
 */
@Schema(description = "Log Webhook/IPN từ cổng thanh toán (dùng để đối soát & chống replay)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentWebhookLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String orderCode;

    @NotNull
    private PaymentGateway gateway;

    @Lob
    private String rawPayload;

    @Size(max = 255)
    private String signature;

    @NotNull
    private Boolean isVerified;

    @Size(max = 20)
    private String responseCode;

    @NotNull
    private Instant createdAt;

    private PaymentTransactionDTO paymentTransaction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public PaymentGateway getGateway() {
        return gateway;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public PaymentTransactionDTO getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransactionDTO paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentWebhookLogDTO)) {
            return false;
        }

        PaymentWebhookLogDTO paymentWebhookLogDTO = (PaymentWebhookLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentWebhookLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentWebhookLogDTO{" +
            "id=" + getId() +
            ", orderCode='" + getOrderCode() + "'" +
            ", gateway='" + getGateway() + "'" +
            ", rawPayload='" + getRawPayload() + "'" +
            ", signature='" + getSignature() + "'" +
            ", isVerified='" + getIsVerified() + "'" +
            ", responseCode='" + getResponseCode() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", paymentTransaction=" + getPaymentTransaction() +
            "}";
    }
}
