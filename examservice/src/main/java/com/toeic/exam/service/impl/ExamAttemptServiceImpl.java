package com.toeic.exam.service.impl;

import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.UserAnswer;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.repository.QuestionRepository;
import com.toeic.exam.repository.UserAnswerRepository;
import com.toeic.exam.service.ExamAttemptService;
import com.toeic.exam.service.dto.ExamAttemptDTO;
import com.toeic.exam.service.dto.ExamResultDTO;
import com.toeic.exam.service.dto.ExamSubmissionDTO;
import com.toeic.exam.service.dto.QuestionAnswerSubmissionDTO;
import com.toeic.exam.service.mapper.ExamAttemptMapper;
import com.toeic.exam.service.util.ToeicScoreConverter;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.toeic.exam.domain.ExamAttempt}.
 */
@Service
@Transactional
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptServiceImpl.class);

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamAttemptMapper examAttemptMapper;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;

    public ExamAttemptServiceImpl(
        ExamAttemptRepository examAttemptRepository,
        ExamAttemptMapper examAttemptMapper,
        QuestionRepository questionRepository,
        UserAnswerRepository userAnswerRepository
    ) {
        this.examAttemptRepository = examAttemptRepository;
        this.examAttemptMapper = examAttemptMapper;
        this.questionRepository = questionRepository;
        this.userAnswerRepository = userAnswerRepository;
    }

    @Override
    public ExamAttemptDTO save(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to save ExamAttempt : {}", examAttemptDTO);
        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    @Override
    public ExamAttemptDTO update(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to update ExamAttempt : {}", examAttemptDTO);
        ExamAttempt examAttempt = examAttemptMapper.toEntity(examAttemptDTO);
        examAttempt = examAttemptRepository.save(examAttempt);
        return examAttemptMapper.toDto(examAttempt);
    }

    @Override
    public Optional<ExamAttemptDTO> partialUpdate(ExamAttemptDTO examAttemptDTO) {
        LOG.debug("Request to partially update ExamAttempt : {}", examAttemptDTO);

        return examAttemptRepository
            .findById(examAttemptDTO.getId())
            .map(existingExamAttempt -> {
                examAttemptMapper.partialUpdate(existingExamAttempt, examAttemptDTO);

                return existingExamAttempt;
            })
            .map(examAttemptRepository::save)
            .map(examAttemptMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamAttemptDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ExamAttempts");
        return examAttemptRepository.findAll(pageable).map(examAttemptMapper::toDto);
    }

    public Page<ExamAttemptDTO> findAllWithEagerRelationships(Pageable pageable) {
        return examAttemptRepository.findAllWithEagerRelationships(pageable).map(examAttemptMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamAttemptDTO> findOne(Long id) {
        LOG.debug("Request to get ExamAttempt : {}", id);
        return examAttemptRepository.findOneWithEagerRelationships(id).map(examAttemptMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ExamAttempt : {}", id);
        examAttemptRepository.deleteById(id);
    }

    @Override
@Transactional
public ExamResultDTO submitExam(Long attemptId, ExamSubmissionDTO dto) {
    // 1. Lấy ExamAttempt và kiểm tra status == IN_PROGRESS
    ExamAttempt attempt = examAttemptRepository.findById(attemptId)
        .orElseThrow(() -> new EntityNotFoundException("Attempt not found"));
    if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
        throw new IllegalStateException("Bài thi đã được nộp trước đó");
    }

    // 2. Lấy danh sách câu hỏi bằng 1 query duy nhất (Batch query)
    Set<Long> qIds = dto.getAnswers().stream()
        .map(QuestionAnswerSubmissionDTO::getQuestionId).collect(Collectors.toSet());
    Map<Long, Question> questionMap = questionRepository.findAllByIdInWithPart(qIds).stream()
        .collect(Collectors.toMap(Question::getId, q -> q));

    // 3. Duyệt chấm từng câu
    int listeningCorrect = 0, readingCorrect = 0, totalCorrect = 0, totalWrong = 0, totalSkipped = 0;
    List<UserAnswer> userAnswers = new ArrayList<>();

    for (QuestionAnswerSubmissionDTO ans : dto.getAnswers()) {
        Question q = questionMap.get(ans.getQuestionId());
        if (q == null) continue;

        boolean isCorrect = ans.getSelectedOption() != null && ans.getSelectedOption() == q.getCorrectOption();
        if (ans.getSelectedOption() == null) {
            totalSkipped++;
        } else if (isCorrect) {
            totalCorrect++;
            if (q.getPart().getPartNumber() <= 4) listeningCorrect++;
            else readingCorrect++;
        } else {
            totalWrong++;
        }

        // Tạo UserAnswer
        UserAnswer ua = new UserAnswer()
            .examAttempt(attempt)
            .question(q)
            .selectedOption(ans.getSelectedOption())
            .isCorrect(isCorrect)
            .timeSpentSeconds(ans.getTimeSpentSeconds());
        userAnswers.add(ua);
    }

    // 4. Batch save câu trả lời
    userAnswerRepository.saveAll(userAnswers);

    // 5. Tính điểm theo ToeicScoreConverter & Cập nhật Attempt
    int lScore = ToeicScoreConverter.toListeningScore(listeningCorrect);
    int rScore = ToeicScoreConverter.toReadingScore(readingCorrect);

    attempt.setStatus(AttemptStatus.COMPLETED);
    attempt.setListeningScore(lScore);
    attempt.setReadingScore(rScore);
    attempt.setTotalScore(lScore + rScore);
    attempt.setCorrectAnswers(totalCorrect);
    attempt.setWrongAnswers(totalWrong);
    attempt.setSkippedAnswers(totalSkipped);
    attempt.setTimeSpentSeconds(dto.getTimeSpentSeconds());
    attempt.setCompletedAt(Instant.now());
    examAttemptRepository.save(attempt);

    // 6. Trả về DTO kết quả
    return buildResultDTO(attempt);
}

    private ExamResultDTO buildResultDTO(ExamAttempt attempt) {
        ExamResultDTO dto = new ExamResultDTO();
        dto.setAttemptId(attempt.getId());
        if (attempt.getExam() != null) {
            dto.setExamId(attempt.getExam().getId());
            dto.setExamTitle(attempt.getExam().getTitle());
        }
        dto.setStatus(attempt.getStatus());
        dto.setListeningScore(attempt.getListeningScore());
        dto.setReadingScore(attempt.getReadingScore());
        dto.setTotalScore(attempt.getTotalScore());
        dto.setCorrectAnswers(attempt.getCorrectAnswers());
        dto.setWrongAnswers(attempt.getWrongAnswers());
        dto.setSkippedAnswers(attempt.getSkippedAnswers());
        dto.setTimeSpentSeconds(attempt.getTimeSpentSeconds());
        dto.setCompletedAt(attempt.getCompletedAt());
        return dto;
    }
}
