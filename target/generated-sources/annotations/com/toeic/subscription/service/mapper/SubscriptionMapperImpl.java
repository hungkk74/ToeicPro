package com.toeic.subscription.service.mapper;

import com.toeic.subscription.domain.Plan;
import com.toeic.subscription.domain.Subscription;
import com.toeic.subscription.service.dto.PlanDTO;
import com.toeic.subscription.service.dto.SubscriptionDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-10T11:07:57+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class SubscriptionMapperImpl implements SubscriptionMapper {

    @Override
    public Subscription toEntity(SubscriptionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Subscription subscription = new Subscription();

        subscription.setId( dto.getId() );
        subscription.setUserId( dto.getUserId() );
        subscription.setStatus( dto.getStatus() );
        subscription.setStartsAt( dto.getStartsAt() );
        subscription.setExpiresAt( dto.getExpiresAt() );
        subscription.setCreatedAt( dto.getCreatedAt() );
        subscription.plan( planDTOToPlan( dto.getPlan() ) );

        return subscription;
    }

    @Override
    public List<Subscription> toEntity(List<SubscriptionDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Subscription> list = new ArrayList<Subscription>( dtoList.size() );
        for ( SubscriptionDTO subscriptionDTO : dtoList ) {
            list.add( toEntity( subscriptionDTO ) );
        }

        return list;
    }

    @Override
    public List<SubscriptionDTO> toDto(List<Subscription> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<SubscriptionDTO> list = new ArrayList<SubscriptionDTO>( entityList.size() );
        for ( Subscription subscription : entityList ) {
            list.add( toDto( subscription ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Subscription entity, SubscriptionDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getUserId() != null ) {
            entity.setUserId( dto.getUserId() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getStartsAt() != null ) {
            entity.setStartsAt( dto.getStartsAt() );
        }
        if ( dto.getExpiresAt() != null ) {
            entity.setExpiresAt( dto.getExpiresAt() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getPlan() != null ) {
            if ( entity.getPlan() == null ) {
                entity.plan( new Plan() );
            }
            planDTOToPlan1( dto.getPlan(), entity.getPlan() );
        }
    }

    @Override
    public SubscriptionDTO toDto(Subscription s) {
        if ( s == null ) {
            return null;
        }

        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();

        subscriptionDTO.setPlan( toDtoPlanName( s.getPlan() ) );
        subscriptionDTO.setId( s.getId() );
        subscriptionDTO.setUserId( s.getUserId() );
        subscriptionDTO.setStatus( s.getStatus() );
        subscriptionDTO.setStartsAt( s.getStartsAt() );
        subscriptionDTO.setExpiresAt( s.getExpiresAt() );
        subscriptionDTO.setCreatedAt( s.getCreatedAt() );

        return subscriptionDTO;
    }

    @Override
    public PlanDTO toDtoPlanName(Plan plan) {
        if ( plan == null ) {
            return null;
        }

        PlanDTO planDTO = new PlanDTO();

        planDTO.setId( plan.getId() );
        planDTO.setName( plan.getName() );

        return planDTO;
    }

    protected Plan planDTOToPlan(PlanDTO planDTO) {
        if ( planDTO == null ) {
            return null;
        }

        Plan plan = new Plan();

        plan.setId( planDTO.getId() );
        plan.setCode( planDTO.getCode() );
        plan.setName( planDTO.getName() );
        plan.setPrice( planDTO.getPrice() );
        plan.setDurationDays( planDTO.getDurationDays() );
        plan.setFeatures( planDTO.getFeatures() );
        plan.setIsActive( planDTO.getIsActive() );

        return plan;
    }

    protected void planDTOToPlan1(PlanDTO planDTO, Plan mappingTarget) {
        if ( planDTO == null ) {
            return;
        }

        if ( planDTO.getId() != null ) {
            mappingTarget.setId( planDTO.getId() );
        }
        if ( planDTO.getCode() != null ) {
            mappingTarget.setCode( planDTO.getCode() );
        }
        if ( planDTO.getName() != null ) {
            mappingTarget.setName( planDTO.getName() );
        }
        if ( planDTO.getPrice() != null ) {
            mappingTarget.setPrice( planDTO.getPrice() );
        }
        if ( planDTO.getDurationDays() != null ) {
            mappingTarget.setDurationDays( planDTO.getDurationDays() );
        }
        if ( planDTO.getFeatures() != null ) {
            mappingTarget.setFeatures( planDTO.getFeatures() );
        }
        if ( planDTO.getIsActive() != null ) {
            mappingTarget.setIsActive( planDTO.getIsActive() );
        }
    }
}
