package com.toeic.exam.service;

import com.toeic.exam.domain.Part;
import com.toeic.exam.repository.PartRepository;
import com.toeic.exam.service.dto.PartDTO;
import com.toeic.exam.service.mapper.PartMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing {@link com.toeic.exam.domain.Part}.
 */
@Service
@Transactional
public class PartService {

    private static final Logger LOG = LoggerFactory.getLogger(PartService.class);

    private final PartRepository partRepository;
    private final PartMapper partMapper;

    public PartService(PartRepository partRepository, PartMapper partMapper) {
        this.partRepository = partRepository;
        this.partMapper = partMapper;
    }

    public PartDTO save(PartDTO partDTO) {
        LOG.debug("Request to save Part : {}", partDTO);
        Part part = partMapper.toEntity(partDTO);
        part = partRepository.save(part);
        return partMapper.toDto(part);
    }

    public PartDTO update(PartDTO partDTO) {
        LOG.debug("Request to update Part : {}", partDTO);
        Part part = partMapper.toEntity(partDTO);
        part = partRepository.save(part);
        return partMapper.toDto(part);
    }

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

    @Transactional(readOnly = true)
    public List<PartDTO> findAll() {
        LOG.debug("Request to get all Parts");
        return partRepository.findAll().stream().map(partMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<PartDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get a page of Parts");
        return partRepository.findAll(pageable).map(partMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<PartDTO> findAllWithEagerRelationships() {
        LOG.debug("Request to get all Parts with eager relationships");
        return partRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(partMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<PartDTO> findAllWithEagerRelationships(Pageable pageable) {
        LOG.debug("Request to get a page of Parts with eager relationships");
        return partRepository.findAllWithEagerRelationships(pageable).map(partMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PartDTO> findOne(Long id) {
        LOG.debug("Request to get Part : {}", id);
        return partRepository.findOneWithEagerRelationships(id).map(partMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Part : {}", id);
        partRepository.deleteById(id);
    }
}
