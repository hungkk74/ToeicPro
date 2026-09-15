package com.toeic.exam.repository;

import com.toeic.exam.domain.ExamAttempt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExamAttempt entity.
 */
@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    default Optional<ExamAttempt> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ExamAttempt> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ExamAttempt> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select examAttempt from ExamAttempt examAttempt left join fetch examAttempt.exam",
        countQuery = "select count(examAttempt) from ExamAttempt examAttempt"
    )
    Page<ExamAttempt> findAllWithToOneRelationships(Pageable pageable);

    @Query("select examAttempt from ExamAttempt examAttempt left join fetch examAttempt.exam")
    List<ExamAttempt> findAllWithToOneRelationships();

    @Query("select examAttempt from ExamAttempt examAttempt left join fetch examAttempt.exam where examAttempt.id =:id")
    Optional<ExamAttempt> findOneWithToOneRelationships(@Param("id") Long id);
}
