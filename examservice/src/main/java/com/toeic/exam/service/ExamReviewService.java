package com.toeic.exam.service;

import com.toeic.exam.service.dto.review.ExamReviewDTO;

/**
 * Service Interface cho việc xem lại và phân tích kết quả bài thi TOEIC.
 */
public interface ExamReviewService {
    ExamReviewDTO getExamReview(Long attemptId);
}
