package com.toeic.payment.domain;

import com.toeic.payment.domain.enumeration.PaymentGateway;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Log Webhook/IPN từ cổng thanh toán (dùng để đối soát & chống replay)
 */
@Entity
@Table(name = "payment_webhook_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentWebhookLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "order_code", length = 50, nullable = false)
    private String orderCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false)
    private PaymentGateway gateway;

    @Lob
    @Column(name = "raw_payload", nullable = false)
    private String rawPayload;

    @Size(max = 255)
    @Column(name = "signature", length = 255)
    private String signature;

    @NotNull
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Size(max = 20)
    @Column(name = "response_code", length = 20)
    private String responseCode;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentTransaction paymentTransaction;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaymentWebhookLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return this.orderCode;
    }

    public PaymentWebhookLog orderCode(String orderCode) {
        this.setOrderCode(orderCode);
        return this;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public PaymentGateway getGateway() {
        return this.gateway;
    }

    public PaymentWebhookLog gateway(PaymentGateway gateway) {
        this.setGateway(gateway);
        return this;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public String getRawPayload() {
        return this.rawPayload;
    }

    public PaymentWebhookLog rawPayload(String rawPayload) {
        this.setRawPayload(rawPayload);
        return this;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public String getSignature() {
        return this.signature;
    }

    public PaymentWebhookLog signature(String signature) {
        this.setSignature(signature);
        return this;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean getIsVerified() {
        return this.isVerified;
    }

    public PaymentWebhookLog isVerified(Boolean isVerified) {
        this.setIsVerified(isVerified);
        return this;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public PaymentWebhookLog responseCode(String responseCode) {
        this.setResponseCode(responseCode);
        return this;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PaymentWebhookLog createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public PaymentTransaction getPaymentTransaction() {
        return this.paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    public PaymentWebhookLog paymentTransaction(PaymentTransaction paymentTransaction) {
        this.setPaymentTransaction(paymentTransaction);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentWebhookLog)) {
            return false;
        }
        return getId() != null && getId().equals(((PaymentWebhookLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentWebhookLog{" +
            "id=" + getId() +
            ", orderCode='" + getOrderCode() + "'" +
            ", gateway='" + getGateway() + "'" +
            ", rawPayload='" + getRawPayload() + "'" +
            ", signature='" + getSignature() + "'" +
            ", isVerified='" + getIsVerified() + "'" +
            ", responseCode='" + getResponseCode() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
