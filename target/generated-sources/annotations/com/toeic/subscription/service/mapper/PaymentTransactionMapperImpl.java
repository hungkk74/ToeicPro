package com.toeic.subscription.service.mapper;

import com.toeic.subscription.domain.PaymentTransaction;
import com.toeic.subscription.domain.Plan;
import com.toeic.subscription.domain.Subscription;
import com.toeic.subscription.service.dto.PaymentTransactionDTO;
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
public class PaymentTransactionMapperImpl implements PaymentTransactionMapper {

    @Override
    public PaymentTransaction toEntity(PaymentTransactionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PaymentTransaction paymentTransaction = new PaymentTransaction();

        paymentTransaction.setId( dto.getId() );
        paymentTransaction.setOrderCode( dto.getOrderCode() );
        paymentTransaction.setGateway( dto.getGateway() );
        paymentTransaction.setGatewayTransId( dto.getGatewayTransId() );
        paymentTransaction.setAmount( dto.getAmount() );
        paymentTransaction.setStatus( dto.getStatus() );
        paymentTransaction.setCreatedAt( dto.getCreatedAt() );
        paymentTransaction.subscription( subscriptionDTOToSubscription( dto.getSubscription() ) );

        return paymentTransaction;
    }

    @Override
    public List<PaymentTransaction> toEntity(List<PaymentTransactionDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<PaymentTransaction> list = new ArrayList<PaymentTransaction>( dtoList.size() );
        for ( PaymentTransactionDTO paymentTransactionDTO : dtoList ) {
            list.add( toEntity( paymentTransactionDTO ) );
        }

        return list;
    }

    @Override
    public List<PaymentTransactionDTO> toDto(List<PaymentTransaction> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PaymentTransactionDTO> list = new ArrayList<PaymentTransactionDTO>( entityList.size() );
        for ( PaymentTransaction paymentTransaction : entityList ) {
            list.add( toDto( paymentTransaction ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(PaymentTransaction entity, PaymentTransactionDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getOrderCode() != null ) {
            entity.setOrderCode( dto.getOrderCode() );
        }
        if ( dto.getGateway() != null ) {
            entity.setGateway( dto.getGateway() );
        }
        if ( dto.getGatewayTransId() != null ) {
            entity.setGatewayTransId( dto.getGatewayTransId() );
        }
        if ( dto.getAmount() != null ) {
            entity.setAmount( dto.getAmount() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getSubscription() != null ) {
            if ( entity.getSubscription() == null ) {
                entity.subscription( new Subscription() );
            }
            subscriptionDTOToSubscription1( dto.getSubscription(), entity.getSubscription() );
        }
    }

    @Override
    public PaymentTransactionDTO toDto(PaymentTransaction s) {
        if ( s == null ) {
            return null;
        }

        PaymentTransactionDTO paymentTransactionDTO = new PaymentTransactionDTO();

        paymentTransactionDTO.setSubscription( toDtoSubscriptionId( s.getSubscription() ) );
        paymentTransactionDTO.setId( s.getId() );
        paymentTransactionDTO.setOrderCode( s.getOrderCode() );
        paymentTransactionDTO.setGateway( s.getGateway() );
        paymentTransactionDTO.setGatewayTransId( s.getGatewayTransId() );
        paymentTransactionDTO.setAmount( s.getAmount() );
        paymentTransactionDTO.setStatus( s.getStatus() );
        paymentTransactionDTO.setCreatedAt( s.getCreatedAt() );

        return paymentTransactionDTO;
    }

    @Override
    public SubscriptionDTO toDtoSubscriptionId(Subscription subscription) {
        if ( subscription == null ) {
            return null;
        }

        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();

        subscriptionDTO.setId( subscription.getId() );

        return subscriptionDTO;
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

    protected Subscription subscriptionDTOToSubscription(SubscriptionDTO subscriptionDTO) {
        if ( subscriptionDTO == null ) {
            return null;
        }

        Subscription subscription = new Subscription();

        subscription.setId( subscriptionDTO.getId() );
        subscription.setUserId( subscriptionDTO.getUserId() );
        subscription.setStatus( subscriptionDTO.getStatus() );
        subscription.setStartsAt( subscriptionDTO.getStartsAt() );
        subscription.setExpiresAt( subscriptionDTO.getExpiresAt() );
        subscription.setCreatedAt( subscriptionDTO.getCreatedAt() );
        subscription.plan( planDTOToPlan( subscriptionDTO.getPlan() ) );

        return subscription;
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

    protected void subscriptionDTOToSubscription1(SubscriptionDTO subscriptionDTO, Subscription mappingTarget) {
        if ( subscriptionDTO == null ) {
            return;
        }

        if ( subscriptionDTO.getId() != null ) {
            mappingTarget.setId( subscriptionDTO.getId() );
        }
        if ( subscriptionDTO.getUserId() != null ) {
            mappingTarget.setUserId( subscriptionDTO.getUserId() );
        }
        if ( subscriptionDTO.getStatus() != null ) {
            mappingTarget.setStatus( subscriptionDTO.getStatus() );
        }
        if ( subscriptionDTO.getStartsAt() != null ) {
            mappingTarget.setStartsAt( subscriptionDTO.getStartsAt() );
        }
        if ( subscriptionDTO.getExpiresAt() != null ) {
            mappingTarget.setExpiresAt( subscriptionDTO.getExpiresAt() );
        }
        if ( subscriptionDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( subscriptionDTO.getCreatedAt() );
        }
        if ( subscriptionDTO.getPlan() != null ) {
            if ( mappingTarget.getPlan() == null ) {
                mappingTarget.plan( new Plan() );
            }
            planDTOToPlan1( subscriptionDTO.getPlan(), mappingTarget.getPlan() );
        }
    }
}
