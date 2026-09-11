package com.toeic.payment.domain;

import com.toeic.payment.domain.enumeration.PaymentGateway;
import com.toeic.payment.domain.enumeration.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Giao dịch thanh toán chính
 */
@Entity
@Table(name = "payment_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentTransaction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "order_code", length = 50, nullable = false, unique = true)
    private String orderCode;

    @NotNull
    @Size(max = 36)
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @NotNull
    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Size(max = 10)
    @Column(name = "currency", length = 10, nullable = false)
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false)
    private PaymentGateway gateway;

    @Size(max = 100)
    @Column(name = "gateway_trans_id", length = 100)
    private String gatewayTransId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Size(max = 1000)
    @Column(name = "payment_url", length = 1000)
    private String paymentUrl;

    @Size(max = 50)
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "payment_time")
    private Instant paymentTime;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaymentTransaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return this.orderCode;
    }

    public PaymentTransaction orderCode(String orderCode) {
        this.setOrderCode(orderCode);
        return this;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getUserId() {
        return this.userId;
    }

    public PaymentTransaction userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSubscriptionId() {
        return this.subscriptionId;
    }

    public PaymentTransaction subscriptionId(Long subscriptionId) {
        this.setSubscriptionId(subscriptionId);
        return this;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public PaymentTransaction amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public PaymentTransaction currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentGateway getGateway() {
        return this.gateway;
    }

    public PaymentTransaction gateway(PaymentGateway gateway) {
        this.setGateway(gateway);
        return this;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public String getGatewayTransId() {
        return this.gatewayTransId;
    }

    public PaymentTransaction gatewayTransId(String gatewayTransId) {
        this.setGatewayTransId(gatewayTransId);
        return this;
    }

    public void setGatewayTransId(String gatewayTransId) {
        this.gatewayTransId = gatewayTransId;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public PaymentTransaction status(PaymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getPaymentUrl() {
        return this.paymentUrl;
    }

    public PaymentTransaction paymentUrl(String paymentUrl) {
        this.setPaymentUrl(paymentUrl);
        return this;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public PaymentTransaction ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getPaymentTime() {
        return this.paymentTime;
    }

    public PaymentTransaction paymentTime(Instant paymentTime) {
        this.setPaymentTime(paymentTime);
        return this;
    }

    public void setPaymentTime(Instant paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PaymentTransaction createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentTransaction)) {
            return false;
        }
        return getId() != null && getId().equals(((PaymentTransaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentTransaction{" +
            "id=" + getId() +
            ", orderCode='" + getOrderCode() + "'" +
            ", userId='" + getUserId() + "'" +
            ", subscriptionId=" + getSubscriptionId() +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", gateway='" + getGateway() + "'" +
            ", gatewayTransId='" + getGatewayTransId() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentUrl='" + getPaymentUrl() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", paymentTime='" + getPaymentTime() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
