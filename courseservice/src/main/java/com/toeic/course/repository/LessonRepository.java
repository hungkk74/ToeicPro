package com.toeic.course.repository;

import com.toeic.course.domain.Lesson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Lesson entity.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    default Optional<Lesson> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Lesson> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Lesson> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select lesson from Lesson lesson left join fetch lesson.chapter",
        countQuery = "select count(lesson) from Lesson lesson"
    )
    Page<Lesson> findAllWithToOneRelationships(Pageable pageable);

    @Query("select lesson from Lesson lesson left join fetch lesson.chapter")
    List<Lesson> findAllWithToOneRelationships();

    @Query("select lesson from Lesson lesson left join fetch lesson.chapter where lesson.id =:id")
    Optional<Lesson> findOneWithToOneRelationships(@Param("id") Long id);
}
