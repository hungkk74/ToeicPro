package com.toeic.exam.repository;

import com.toeic.exam.domain.Question;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Question entity.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    default Optional<Question> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Question> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Question> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select question from Question question left join fetch question.part",
        countQuery = "select count(question) from Question question"
    )
    Page<Question> findAllWithToOneRelationships(Pageable pageable);

    @Query("select question from Question question left join fetch question.part")
    List<Question> findAllWithToOneRelationships();

    @Query("select question from Question question left join fetch question.part where question.id =:id")
    Optional<Question> findOneWithToOneRelationships(@Param("id") Long id);
}
