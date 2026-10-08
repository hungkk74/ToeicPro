package com.toeic.exam.repository;

import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import com.toeic.exam.service.dto.ExamAttemptHistoryDTO;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
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

    public interface AttemptAuthView {
        String getUserId();
        AttemptStatus getStatus();
    }

    @Query("SELECT a.userId AS userId, a.status AS status FROM ExamAttempt a WHERE a.id = :id")
    Optional<AttemptAuthView> findAuthById(Long id);

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"),
        @QueryHint(name = "jakarta.persistence.query.timeout", value = "5000")
    })
    @Query("select a from ExamAttempt a where a.id = :id")
    Optional<ExamAttempt> findOneForUpdate(@Param("id") Long id);

    @Query(
        value = "select new com.toeic.exam.service.dto.ExamAttemptHistoryDTO(" +
            "a.id, e.id, e.title, a.listeningScore, a.readingScore, a.totalScore, " +
            "a.correctAnswers, a.wrongAnswers, a.skippedAnswers, a.timeSpentSeconds, " +
            "a.startedAt, a.completedAt) " +
            "from ExamAttempt a left join a.exam e " +
            "where a.userId = :userId and a.status = com.toeic.exam.domain.enumeration.AttemptStatus.COMPLETED " +
            "order by a.completedAt desc",
        countQuery = "select count(a) from ExamAttempt a where a.userId = :userId and a.status = com.toeic.exam.domain.enumeration.AttemptStatus.COMPLETED"
    )
    Page<ExamAttemptHistoryDTO> findHistoryByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT a.id FROM ExamAttempt a WHERE a.status = :status AND a.startedAt < :startedAt ORDER BY a.id ASC")
    List<Long> findIdsByStatusAndStartedAtBefore(
        @Param("status") com.toeic.exam.domain.enumeration.AttemptStatus status,
        @Param("startedAt") java.time.Instant startedAt,
        Pageable pageable
    );

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ExamAttempt a WHERE a.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<Long> ids);
}

