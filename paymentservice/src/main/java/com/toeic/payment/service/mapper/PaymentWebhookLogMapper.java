package com.toeic.payment.service.mapper;

import com.toeic.payment.domain.PaymentTransaction;
import com.toeic.payment.domain.PaymentWebhookLog;
import com.toeic.payment.service.dto.PaymentTransactionDTO;
import com.toeic.payment.service.dto.PaymentWebhookLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaymentWebhookLog} and its DTO {@link PaymentWebhookLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentWebhookLogMapper extends EntityMapper<PaymentWebhookLogDTO, PaymentWebhookLog> {
    @Mapping(target = "paymentTransaction", source = "paymentTransaction", qualifiedByName = "paymentTransactionOrderCode")
    PaymentWebhookLogDTO toDto(PaymentWebhookLog s);

    @Named("paymentTransactionOrderCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderCode", source = "orderCode")
    PaymentTransactionDTO toDtoPaymentTransactionOrderCode(PaymentTransaction paymentTransaction);
}
