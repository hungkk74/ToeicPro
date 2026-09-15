package com.toeic.course.repository;

import com.toeic.course.domain.LessonProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LessonProgress entity.
 */
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    default Optional<LessonProgress> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LessonProgress> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LessonProgress> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select lessonProgress from LessonProgress lessonProgress left join fetch lessonProgress.lesson",
        countQuery = "select count(lessonProgress) from LessonProgress lessonProgress"
    )
    Page<LessonProgress> findAllWithToOneRelationships(Pageable pageable);

    @Query("select lessonProgress from LessonProgress lessonProgress left join fetch lessonProgress.lesson")
    List<LessonProgress> findAllWithToOneRelationships();

    @Query("select lessonProgress from LessonProgress lessonProgress left join fetch lessonProgress.lesson where lessonProgress.id =:id")
    Optional<LessonProgress> findOneWithToOneRelationships(@Param("id") Long id);
}
