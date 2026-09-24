package com.toeic.exam.service;

import com.toeic.exam.service.dto.ExamDTO;
import com.toeic.exam.service.dto.create.FullExamCreateDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.toeic.exam.service.dto.take.ExamTakeDTO;


/**
 * Service Interface for managing {@link com.toeic.exam.domain.Exam}.
 */

public interface ExamService {
    /**
     * Save a exam.
     *
     * @param examDTO the entity to save.
     * @return the persisted entity.
     */
    ExamDTO save(ExamDTO examDTO);

    /**
     * Create a full exam including parts, question groups, and questions.
     *
     * @param request the full exam create DTO.
     * @return the persisted entity.
     */
    ExamDTO createFullExam(FullExamCreateDTO request);

    /**
     * Updates a exam.
     *
     * @param examDTO the entity to update.
     * @return the persisted entity.
     */
    ExamDTO update(ExamDTO examDTO);

    /**
     * Partially updates a exam.
     *
     * @param examDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ExamDTO> partialUpdate(ExamDTO examDTO);

    /**
     * Get all the exams.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExamDTO> findAll(Pageable pageable);

    /**
     * Get the "id" exam.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ExamDTO> findOne(Long id);

    /**
     * Delete the "id" exam.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Lấy toàn bộ đề thi để làm bài (Đã ẩn đáp án đúng và lời giải).
     *
     * @param examId ID của đề thi.
     * @return Cây dữ liệu ExamTakeDTO đã tinh gọn.
     */
    ExamTakeDTO getExamForTaking(Long examId);

    /**
     * Search for exams by keyword using FULLTEXT index.
     *
     * @param keyword the search keyword.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ExamDTO> search(String keyword, Pageable pageable);
}

