package com.toeic.exam.service;

import com.toeic.exam.service.dto.ExamDTO;
import com.toeic.exam.service.dto.create.FullExamCreateDTO;

/**
 * Service xử lý import toàn bộ cây đề thi (Exam -> Part -> QuestionGroup -> Question).
 */
public interface ExamBulkImportService {
    ExamDTO createFullExam(FullExamCreateDTO request);
}
