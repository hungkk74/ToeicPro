package com.toeic.exam.service.impl;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.domain.enumeration.AnswerOption;
import com.toeic.exam.repository.ExamRepository;
import com.toeic.exam.repository.PartRepository;
import com.toeic.exam.repository.QuestionGroupRepository;
import com.toeic.exam.repository.QuestionRepository;
import com.toeic.exam.service.ExamBulkImportService;
import com.toeic.exam.service.dto.ExamDTO;
import com.toeic.exam.service.dto.create.FullExamCreateDTO;
import com.toeic.exam.service.dto.create.PartCreateDTO;
import com.toeic.exam.service.dto.create.QuestionCreateDTO;
import com.toeic.exam.service.dto.create.QuestionGroupCreateDTO;
import com.toeic.exam.service.mapper.ExamMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation chuyên biệt tạo/import trọn gói cấu trúc đề thi.
 */
@Service
@Transactional
public class ExamBulkImportServiceImpl implements ExamBulkImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamBulkImportServiceImpl.class);

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final PartRepository partRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;

    public ExamBulkImportServiceImpl(
        ExamRepository examRepository,
        ExamMapper examMapper,
        PartRepository partRepository,
        QuestionGroupRepository questionGroupRepository,
        QuestionRepository questionRepository
    ) {
        this.examRepository = examRepository;
        this.examMapper = examMapper;
        this.partRepository = partRepository;
        this.questionGroupRepository = questionGroupRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public ExamDTO createFullExam(FullExamCreateDTO request) {
        LOG.debug("Request to bulk import Exam: {}", request.getTitle());

        Exam exam = new Exam();
        exam.setCode(request.getCode());
        exam.setTitle(request.getTitle());
        exam.setCategory(request.getCategory());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setTotalQuestions(request.getTotalQuestions());
        exam.setAudioFullUrl(request.getAudioFullUrl());
        exam.setIsPublished(request.getIsPublished());
        exam.setCreatedAt(Instant.now());
        exam = examRepository.save(exam);

        if (request.getParts() == null || request.getParts().isEmpty()) {
            return examMapper.toDto(exam);
        }

        for (PartCreateDTO partDto : request.getParts()) {
            Part part = new Part();
            part.setPartNumber(partDto.getPartNumber());
            part.setName(partDto.getName());
            part.setExam(exam);

            int partTotalQuestions = 0;
            if (partDto.getQuestionGroups() != null) {
                for (QuestionGroupCreateDTO groupDto : partDto.getQuestionGroups()) {
                    if (groupDto.getQuestions() != null) {
                        partTotalQuestions += groupDto.getQuestions().size();
                    }
                }
            }
            part.setTotalQuestions(partTotalQuestions);
            part = partRepository.save(part);

            if (partDto.getQuestionGroups() == null || partDto.getQuestionGroups().isEmpty()) {
                continue;
            }

            for (QuestionGroupCreateDTO groupDto : partDto.getQuestionGroups()) {
                QuestionGroup group = new QuestionGroup();
                group.setPassageText(groupDto.getPassageText());
                group.setImageUrl(groupDto.getImageUrl());
                group.setAudioUrl(groupDto.getAudioUrl());
                group.setPart(part);
                group = questionGroupRepository.save(group);

                if (groupDto.getQuestions() == null || groupDto.getQuestions().isEmpty()) {
                    continue;
                }

                List<Question> questions = new ArrayList<>(groupDto.getQuestions().size());
                for (QuestionCreateDTO qDto : groupDto.getQuestions()) {
                    Question question = new Question();
                    question.setQuestionNumber(qDto.getQuestionNumber());
                    question.setContent(qDto.getContent());
                    question.setOptionA(qDto.getOptionA() == null ? "" : qDto.getOptionA());
                    question.setOptionB(qDto.getOptionB() == null ? "" : qDto.getOptionB());
                    question.setOptionC(qDto.getOptionC() == null ? "" : qDto.getOptionC());
                    question.setOptionD(qDto.getOptionD());
                    if (qDto.getCorrectOption() != null) {
                        try {
                            question.setCorrectOption(AnswerOption.valueOf(qDto.getCorrectOption().toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException(
                                "Invalid AnswerOption '" + qDto.getCorrectOption() + "' for question #" + qDto.getQuestionNumber());
                        }
                    } else {
                        throw new IllegalArgumentException(
                            "correctOption is required for question #" + qDto.getQuestionNumber());
                    }
                    question.setExplanation(qDto.getExplanation());
                    question.setQuestionGroup(group);
                    question.setPart(part);
                    questions.add(question);
                }
                questionRepository.saveAll(questions);
            }
        }

        return examMapper.toDto(exam);
    }
}
