package com.toeic.exam.repository;

import com.toeic.exam.domain.UserAnswer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserAnswer entity.
 */
@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    default Optional<UserAnswer> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserAnswer> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserAnswer> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select userAnswer from UserAnswer userAnswer left join fetch userAnswer.question",
        countQuery = "select count(userAnswer) from UserAnswer userAnswer"
    )
    Page<UserAnswer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select userAnswer from UserAnswer userAnswer left join fetch userAnswer.question")
    List<UserAnswer> findAllWithToOneRelationships();

    @Query("select userAnswer from UserAnswer userAnswer left join fetch userAnswer.question where userAnswer.id =:id")
    Optional<UserAnswer> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("SELECT ua FROM UserAnswer ua LEFT JOIN FETCH ua.question q LEFT JOIN FETCH q.part LEFT JOIN FETCH q.questionGroup WHERE ua.examAttempt.id = :attemptId ORDER BY q.questionNumber ASC")
    List<UserAnswer> findByExamAttemptIdWithQuestion(@Param("attemptId") Long attemptId);
}

