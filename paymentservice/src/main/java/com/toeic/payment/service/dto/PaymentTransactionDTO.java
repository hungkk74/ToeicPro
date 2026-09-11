package com.toeic.payment.service.dto;

import com.toeic.payment.domain.enumeration.PaymentGateway;
import com.toeic.payment.domain.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.payment.domain.PaymentTransaction} entity.
 */
@Schema(description = "Giao dịch thanh toán chính")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentTransactionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String orderCode;

    @NotNull
    @Size(max = 36)
    private String userId;

    @NotNull
    private Long subscriptionId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Size(max = 10)
    private String currency;

    @NotNull
    private PaymentGateway gateway;

    @Size(max = 100)
    private String gatewayTransId;

    @NotNull
    private PaymentStatus status;

    @Size(max = 1000)
    private String paymentUrl;

    @Size(max = 50)
    private String ipAddress;

    private Instant paymentTime;

    @NotNull
    private Instant createdAt;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentGateway getGateway() {
        return gateway;
    }

    public void setGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public String getGatewayTransId() {
        return gatewayTransId;
    }

    public void setGatewayTransId(String gatewayTransId) {
        this.gatewayTransId = gatewayTransId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Instant paymentTime) {
        this.paymentTime = paymentTime;
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
        if (!(o instanceof PaymentTransactionDTO)) {
            return false;
        }

        PaymentTransactionDTO paymentTransactionDTO = (PaymentTransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentTransactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentTransactionDTO{" +
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
