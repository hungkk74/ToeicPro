package com.toeic.subscription.service.mapper;

import com.toeic.subscription.domain.Plan;
import com.toeic.subscription.service.dto.PlanDTO;
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
public class PlanMapperImpl implements PlanMapper {

    @Override
    public Plan toEntity(PlanDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Plan plan = new Plan();

        plan.setId( dto.getId() );
        plan.setCode( dto.getCode() );
        plan.setName( dto.getName() );
        plan.setPrice( dto.getPrice() );
        plan.setDurationDays( dto.getDurationDays() );
        plan.setFeatures( dto.getFeatures() );
        plan.setIsActive( dto.getIsActive() );

        return plan;
    }

    @Override
    public PlanDTO toDto(Plan entity) {
        if ( entity == null ) {
            return null;
        }

        PlanDTO planDTO = new PlanDTO();

        planDTO.setId( entity.getId() );
        planDTO.setCode( entity.getCode() );
        planDTO.setName( entity.getName() );
        planDTO.setPrice( entity.getPrice() );
        planDTO.setDurationDays( entity.getDurationDays() );
        planDTO.setFeatures( entity.getFeatures() );
        planDTO.setIsActive( entity.getIsActive() );

        return planDTO;
    }

    @Override
    public List<Plan> toEntity(List<PlanDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Plan> list = new ArrayList<Plan>( dtoList.size() );
        for ( PlanDTO planDTO : dtoList ) {
            list.add( toEntity( planDTO ) );
        }

        return list;
    }

    @Override
    public List<PlanDTO> toDto(List<Plan> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PlanDTO> list = new ArrayList<PlanDTO>( entityList.size() );
        for ( Plan plan : entityList ) {
            list.add( toDto( plan ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Plan entity, PlanDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getCode() != null ) {
            entity.setCode( dto.getCode() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getPrice() != null ) {
            entity.setPrice( dto.getPrice() );
        }
        if ( dto.getDurationDays() != null ) {
            entity.setDurationDays( dto.getDurationDays() );
        }
        if ( dto.getFeatures() != null ) {
            entity.setFeatures( dto.getFeatures() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
    }
}
