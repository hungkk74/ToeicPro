package com.toeic.exam.repository;

import com.toeic.exam.domain.Exam;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Exam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query(
        value = "SELECT * FROM exam WHERE MATCH(title) AGAINST (:keyword IN BOOLEAN MODE)",
        countQuery = "SELECT count(*) FROM exam WHERE MATCH(title) AGAINST (:keyword IN BOOLEAN MODE)",
        nativeQuery = true
    )
    Page<Exam> searchExamsFullText(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByIdAndIsPublishedTrue(Long id);

    Exam getReferenceById(Long id);

    Page<Exam> findByIsPublishedTrue(Pageable pageable);

    @Query(
        value = "SELECT * FROM exam WHERE MATCH(title) AGAINST (:keyword IN BOOLEAN MODE) AND is_published = true",
        countQuery = "SELECT count(*) FROM exam WHERE MATCH(title) AGAINST (:keyword IN BOOLEAN MODE) AND is_published = true",
        nativeQuery = true
    )
    Page<Exam> searchExamsFullTextPublished(@Param("keyword") String keyword, Pageable pageable);
}
