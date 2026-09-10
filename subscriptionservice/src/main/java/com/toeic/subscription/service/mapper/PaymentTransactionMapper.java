package com.toeic.subscription.service.mapper;

import com.toeic.subscription.domain.PaymentTransaction;
import com.toeic.subscription.domain.Subscription;
import com.toeic.subscription.service.dto.PaymentTransactionDTO;
import com.toeic.subscription.service.dto.SubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaymentTransaction} and its DTO {@link PaymentTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper extends EntityMapper<PaymentTransactionDTO, PaymentTransaction> {
    @Mapping(target = "subscription", source = "subscription", qualifiedByName = "subscriptionId")
    PaymentTransactionDTO toDto(PaymentTransaction s);

    @Named("subscriptionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SubscriptionDTO toDtoSubscriptionId(Subscription subscription);
}
