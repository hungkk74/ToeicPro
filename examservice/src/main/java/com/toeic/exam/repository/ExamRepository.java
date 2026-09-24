package com.toeic.exam.repository;

import com.toeic.exam.domain.Exam;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Exam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    @Query(
        value = "SELECT * FROM exam WHERE MATCH(title, description) AGAINST (:keyword IN BOOLEAN MODE)",
        countQuery = "SELECT count(*) FROM exam WHERE MATCH(title, description) AGAINST (:keyword IN BOOLEAN MODE)",
        nativeQuery = true
    )
    org.springframework.data.domain.Page<Exam> searchExamsFullText(@org.springframework.data.repository.query.Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
}
