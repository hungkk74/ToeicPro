package com.toeic.exam.repository;

import com.toeic.exam.domain.QuestionGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuestionGroup entity.
 */
@Repository
public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long> {
    default Optional<QuestionGroup> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<QuestionGroup> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<QuestionGroup> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select questionGroup from QuestionGroup questionGroup left join fetch questionGroup.part",
        countQuery = "select count(questionGroup) from QuestionGroup questionGroup"
    )
    Page<QuestionGroup> findAllWithToOneRelationships(Pageable pageable);

    @Query("select questionGroup from QuestionGroup questionGroup left join fetch questionGroup.part")
    List<QuestionGroup> findAllWithToOneRelationships();

    @Query("select questionGroup from QuestionGroup questionGroup left join fetch questionGroup.part where questionGroup.id =:id")
    Optional<QuestionGroup> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("SELECT qg FROM QuestionGroup qg WHERE qg.part.exam.id = :examId")
List<QuestionGroup> findByExamId(@Param("examId") Long examId);
}
