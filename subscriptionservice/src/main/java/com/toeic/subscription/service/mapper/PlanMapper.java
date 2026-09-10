package com.toeic.subscription.service.mapper;

import com.toeic.subscription.domain.Plan;
import com.toeic.subscription.service.dto.PlanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Plan} and its DTO {@link PlanDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlanMapper extends EntityMapper<PlanDTO, Plan> {}
