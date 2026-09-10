package com.toeic.subscription.service.mapper;

import com.toeic.subscription.domain.Plan;
import com.toeic.subscription.domain.Subscription;
import com.toeic.subscription.service.dto.PlanDTO;
import com.toeic.subscription.service.dto.SubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Subscription} and its DTO {@link SubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubscriptionMapper extends EntityMapper<SubscriptionDTO, Subscription> {
    @Mapping(target = "plan", source = "plan", qualifiedByName = "planName")
    SubscriptionDTO toDto(Subscription s);

    @Named("planName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PlanDTO toDtoPlanName(Plan plan);
}
