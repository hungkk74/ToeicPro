package com.toeic.course.repository;

import com.toeic.course.domain.CourseEnrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CourseEnrollment entity.
 */
@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    default Optional<CourseEnrollment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CourseEnrollment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CourseEnrollment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select courseEnrollment from CourseEnrollment courseEnrollment left join fetch courseEnrollment.course",
        countQuery = "select count(courseEnrollment) from CourseEnrollment courseEnrollment"
    )
    Page<CourseEnrollment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select courseEnrollment from CourseEnrollment courseEnrollment left join fetch courseEnrollment.course")
    List<CourseEnrollment> findAllWithToOneRelationships();

    @Query(
        "select courseEnrollment from CourseEnrollment courseEnrollment left join fetch courseEnrollment.course where courseEnrollment.id =:id"
    )
    Optional<CourseEnrollment> findOneWithToOneRelationships(@Param("id") Long id);
}
