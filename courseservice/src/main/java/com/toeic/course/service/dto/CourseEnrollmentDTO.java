package com.toeic.course.service.dto;

import com.toeic.course.domain.enumeration.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.toeic.course.domain.CourseEnrollment} entity.
 */
@Schema(description = "Ghi nhận quyền sở hữu khóa học của học viên sau khi thanh toán")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseEnrollmentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 36)
    private String userId;

    @NotNull
    private EnrollmentStatus status;

    @NotNull
    private Instant enrolledAt;

    private Instant expiresAt;

    @NotNull
    private CourseDTO course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseEnrollmentDTO)) {
            return false;
        }

        CourseEnrollmentDTO courseEnrollmentDTO = (CourseEnrollmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseEnrollmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseEnrollmentDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", status='" + getStatus() + "'" +
            ", enrolledAt='" + getEnrolledAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", course=" + getCourse() +
            "}";
    }
}
