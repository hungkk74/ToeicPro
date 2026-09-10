package com.toeic.subscription.service.impl;

import com.toeic.subscription.domain.Plan;
import com.toeic.subscription.repository.PlanRepository;
import com.toeic.subscription.service.PlanService;
import com.toeic.subscription.service.dto.PlanDTO;
import com.toeic.subscription.service.mapper.PlanMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.subscription.domain.Plan}.
 */
@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    private static final Logger LOG = LoggerFactory.getLogger(PlanServiceImpl.class);

    private final PlanRepository planRepository;

    private final PlanMapper planMapper;

    public PlanServiceImpl(PlanRepository planRepository, PlanMapper planMapper) {
        this.planRepository = planRepository;
        this.planMapper = planMapper;
    }

    @Override
    public PlanDTO save(PlanDTO planDTO) {
        LOG.debug("Request to save Plan : {}", planDTO);
        Plan plan = planMapper.toEntity(planDTO);
        plan = planRepository.save(plan);
        return planMapper.toDto(plan);
    }

    @Override
    public PlanDTO update(PlanDTO planDTO) {
        LOG.debug("Request to update Plan : {}", planDTO);
        Plan plan = planMapper.toEntity(planDTO);
        plan = planRepository.save(plan);
        return planMapper.toDto(plan);
    }

    @Override
    public Optional<PlanDTO> partialUpdate(PlanDTO planDTO) {
        LOG.debug("Request to partially update Plan : {}", planDTO);

        return planRepository
            .findById(planDTO.getId())
            .map(existingPlan -> {
                planMapper.partialUpdate(existingPlan, planDTO);

                return existingPlan;
            })
            .map(planRepository::save)
            .map(planMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlanDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Plans");
        return planRepository.findAll(pageable).map(planMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanDTO> findOne(Long id) {
        LOG.debug("Request to get Plan : {}", id);
        return planRepository.findById(id).map(planMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Plan : {}", id);
        planRepository.deleteById(id);
    }
}
