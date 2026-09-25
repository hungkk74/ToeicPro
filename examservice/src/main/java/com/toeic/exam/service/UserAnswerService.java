package com.toeic.exam.service;

import com.toeic.exam.service.dto.UserAnswerDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserAnswerService {
    
    UserAnswerDTO save(UserAnswerDTO userAnswerDTO);

    
    UserAnswerDTO update(UserAnswerDTO userAnswerDTO);

    
    Optional<UserAnswerDTO> partialUpdate(UserAnswerDTO userAnswerDTO);

    
    Page<UserAnswerDTO> findAll(Pageable pageable);

    
    Page<UserAnswerDTO> findAllWithEagerRelationships(Pageable pageable);

   
    Optional<UserAnswerDTO> findOne(Long id);

    
    void delete(Long id);
}
