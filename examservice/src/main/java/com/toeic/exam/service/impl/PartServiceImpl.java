package com.toeic.exam.service.impl;

import com.toeic.exam.domain.Part;
import com.toeic.exam.repository.PartRepository;
import com.toeic.exam.service.PartService;
import com.toeic.exam.service.dto.PartDTO;
import com.toeic.exam.service.mapper.PartMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.exam.domain.Part}.
 */
@Service
@Transactional
public class PartServiceImpl implements PartService {

    private static final Logger LOG = LoggerFactory.getLogger(PartServiceImpl.class);

    private final PartRepository partRepository;

    private final PartMapper partMapper;

    public PartServiceImpl(PartRepository partRepository, PartMapper partMapper) {
        this.partRepository = partRepository;
        this.partMapper = partMapper;
    }

    @Override
    public PartDTO save(PartDTO partDTO) {
        LOG.debug("Request to save Part : {}", partDTO);
        Part part = partMapper.toEntity(partDTO);
        part = partRepository.save(part);
        return partMapper.toDto(part);
    }

    @Override
    public PartDTO update(PartDTO partDTO) {
        LOG.debug("Request to update Part : {}", partDTO);
        Part part = partMapper.toEntity(partDTO);
        part = partRepository.save(part);
        return partMapper.toDto(part);
    }

    @Override
    public Optional<PartDTO> partialUpdate(PartDTO partDTO) {
        LOG.debug("Request to partially update Part : {}", partDTO);

        return partRepository
            .findById(partDTO.getId())
            .map(existingPart -> {
                partMapper.partialUpdate(existingPart, partDTO);

                return existingPart;
            })
            .map(partRepository::save)
            .map(partMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartDTO> findAll() {
        LOG.debug("Request to get all Parts");
        return partRepository.findAll().stream().map(partMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public List<PartDTO> findAllWithEagerRelationships() {
        return partRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(partMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PartDTO> findOne(Long id) {
        LOG.debug("Request to get Part : {}", id);
        return partRepository.findOneWithEagerRelationships(id).map(partMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Part : {}", id);
        partRepository.deleteById(id);
    }
}
