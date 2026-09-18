package com.toeic.exam.service.impl;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.repository.ExamRepository;
import com.toeic.exam.service.ExamService;
import com.toeic.exam.service.dto.ExamDTO;
import com.toeic.exam.service.mapper.ExamMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.repository.PartRepository;
import com.toeic.exam.repository.QuestionGroupRepository;
import com.toeic.exam.repository.QuestionRepository;
import com.toeic.exam.service.dto.take.ExamTakeDTO;
import com.toeic.exam.service.dto.take.PartTakeDTO;
import com.toeic.exam.service.dto.take.QuestionGroupTakeDTO;
import com.toeic.exam.service.dto.take.QuestionTakeDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Service Implementation for managing {@link com.toeic.exam.domain.Exam}.
 */
@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private static final Logger LOG = LoggerFactory.getLogger(ExamServiceImpl.class);

    private final ExamRepository examRepository;
    private final ExamMapper examMapper;
    private final PartRepository partRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;

    public ExamServiceImpl(
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
    public ExamDTO save(ExamDTO examDTO) {
        LOG.debug("Request to save Exam : {}", examDTO);
        Exam exam = examMapper.toEntity(examDTO);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    @Override
    public ExamDTO update(ExamDTO examDTO) {
        LOG.debug("Request to update Exam : {}", examDTO);
        Exam exam = examMapper.toEntity(examDTO);
        exam = examRepository.save(exam);
        return examMapper.toDto(exam);
    }

    @Override
    public Optional<ExamDTO> partialUpdate(ExamDTO examDTO) {
        LOG.debug("Request to partially update Exam : {}", examDTO);

        return examRepository
            .findById(examDTO.getId())
            .map(existingExam -> {
                examMapper.partialUpdate(existingExam, examDTO);

                return existingExam;
            })
            .map(examRepository::save)
            .map(examMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Exams");
        return examRepository.findAll(pageable).map(examMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExamDTO> findOne(Long id) {
        LOG.debug("Request to get Exam : {}", id);
        return examRepository.findById(id).map(examMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Exam : {}", id);
        examRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamTakeDTO getExamForTaking(Long examId) {
        LOG.debug("Request to get exam for taking: {}", examId);

        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));

        if (Boolean.FALSE.equals(exam.getIsPublished())) {
            throw new IllegalStateException("Exam is not published yet");
        }

        // 1. Lấy câu hỏi và gom nhóm trước vào Map theo GroupId và PartId
        List<Question> questions = questionRepository.findByExamIdOrderByQuestionNumberAsc(examId);
        Map<Long, List<QuestionTakeDTO>> groupQuestionsMap = new HashMap<>();
        Map<Long, List<QuestionTakeDTO>> standaloneMap = new HashMap<>();

        for (Question q : questions) {
            QuestionTakeDTO qDTO = new QuestionTakeDTO(
                q.getId(), q.getQuestionNumber(), q.getContent(), q.getImageUrl(),
                q.getAudioUrl(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()
            );
            if (q.getQuestionGroup() != null) {
                groupQuestionsMap.computeIfAbsent(q.getQuestionGroup().getId(), k -> new ArrayList<>()).add(qDTO);
            } else if (q.getPart() != null) {
                standaloneMap.computeIfAbsent(q.getPart().getId(), k -> new ArrayList<>()).add(qDTO);
            }
        }

        // 2. Lấy QuestionGroups và gom vào Map theo PartId
        List<QuestionGroup> groups = questionGroupRepository.findByExamId(examId);
        Map<Long, List<QuestionGroupTakeDTO>> partGroupsMap = new HashMap<>();

        for (QuestionGroup g : groups) {
            List<QuestionTakeDTO> qList = groupQuestionsMap.getOrDefault(g.getId(), List.of());
            QuestionGroupTakeDTO gDTO = new QuestionGroupTakeDTO(
                g.getId(), g.getPassageText(), g.getAudioUrl(), g.getImageUrl(), qList
            );
            if (g.getPart() != null) {
                partGroupsMap.computeIfAbsent(g.getPart().getId(), k -> new ArrayList<>()).add(gDTO);
            }
        }

        // 3. Lấy Parts và ghép các QuestionGroups + StandaloneQuestions
        List<Part> parts = partRepository.findByExamIdOrderByPartNumberAsc(examId);
        List<PartTakeDTO> partDTOs = new ArrayList<>();

        for (Part p : parts) {
            List<QuestionGroupTakeDTO> gList = partGroupsMap.getOrDefault(p.getId(), List.of());
            List<QuestionTakeDTO> sList = standaloneMap.getOrDefault(p.getId(), List.of());
            partDTOs.add(new PartTakeDTO(
                p.getId(), p.getPartNumber(), p.getName(), p.getTotalQuestions(), gList, sList
            ));
        }

        // 4. Khởi tạo ExamTakeDTO bất biến
        return new ExamTakeDTO(
            exam.getId(), exam.getCode(), exam.getTitle(),
            exam.getDurationMinutes(), exam.getTotalQuestions(),
            exam.getAudioFullUrl(), partDTOs
        );
    }
}

