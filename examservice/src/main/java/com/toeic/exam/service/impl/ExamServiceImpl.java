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

    // 1. Dựng DTO gốc Exam
    ExamTakeDTO examTakeDTO = new ExamTakeDTO();
    examTakeDTO.setId(exam.getId());
    examTakeDTO.setCode(exam.getCode());
    examTakeDTO.setTitle(exam.getTitle());
    examTakeDTO.setDurationMinutes(exam.getDurationMinutes());
    examTakeDTO.setTotalQuestions(exam.getTotalQuestions());
    examTakeDTO.setAudioFullUrl(exam.getAudioFullUrl());

    // 2. Lấy danh sách Part và đưa vào map tra cứu O(1)
    List<Part> parts = partRepository.findByExamIdOrderByPartNumberAsc(examId);
    Map<Long, PartTakeDTO> partMap = new LinkedHashMap<>();
    for (Part part : parts) {
        PartTakeDTO pDTO = new PartTakeDTO();
        pDTO.setId(part.getId());
        pDTO.setPartNumber(part.getPartNumber());
        pDTO.setName(part.getName());
        pDTO.setTotalQuestions(part.getTotalQuestions());
        partMap.put(part.getId(), pDTO);
        examTakeDTO.getParts().add(pDTO);
    }

    // 3. Lấy danh sách QuestionGroup và ghép vào Part cha
    List<QuestionGroup> groups = questionGroupRepository.findByExamId(examId);
    Map<Long, QuestionGroupTakeDTO> groupMap = new HashMap<>();
    for (QuestionGroup g : groups) {
        QuestionGroupTakeDTO gDTO = new QuestionGroupTakeDTO();
        gDTO.setId(g.getId());
        gDTO.setPassageText(g.getPassageText());
        gDTO.setAudioUrl(g.getAudioUrl());
        gDTO.setImageUrl(g.getImageUrl());
        groupMap.put(g.getId(), gDTO);

        if (g.getPart() != null && partMap.containsKey(g.getPart().getId())) {
            partMap.get(g.getPart().getId()).getGroups().add(gDTO);
        }
    }

    // 4. Lấy toàn bộ Question và phân bổ vào Group hoặc Standalone
    List<Question> questions = questionRepository.findByExamIdOrderByQuestionNumberAsc(examId);
    for (Question q : questions) {
        QuestionTakeDTO qDTO = new QuestionTakeDTO();
        qDTO.setId(q.getId());
        qDTO.setQuestionNumber(q.getQuestionNumber());
        qDTO.setContent(q.getContent());
        qDTO.setImageUrl(q.getImageUrl());
        qDTO.setAudioUrl(q.getAudioUrl());
        qDTO.setOptionA(q.getOptionA());
        qDTO.setOptionB(q.getOptionB());
        qDTO.setOptionC(q.getOptionC());
        qDTO.setOptionD(q.getOptionD());

        if (q.getQuestionGroup() != null && groupMap.containsKey(q.getQuestionGroup().getId())) {
            groupMap.get(q.getQuestionGroup().getId()).getQuestions().add(qDTO);
        } else if (q.getPart() != null && partMap.containsKey(q.getPart().getId())) {
            partMap.get(q.getPart().getId()).getStandaloneQuestions().add(qDTO);
        }
    }

    return examTakeDTO;
}

}
