package com.toeic.exam.service.impl;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.repository.ExamRepository;
import com.toeic.exam.service.ExamService;
import com.toeic.exam.service.dto.ExamDTO;
import com.toeic.exam.service.mapper.ExamMapper;
import com.toeic.exam.service.dto.create.FullExamCreateDTO;
import com.toeic.exam.service.dto.create.PartCreateDTO;
import com.toeic.exam.service.dto.create.QuestionGroupCreateDTO;
import com.toeic.exam.service.dto.create.QuestionCreateDTO;
import com.toeic.exam.domain.enumeration.AnswerOption;
import java.time.Instant;
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
    public ExamDTO createFullExam(FullExamCreateDTO request) {
        LOG.debug("Request to bulk import Exam: {}", request.getTitle());
        
        // 1. Create Exam
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

        // 2. Iterate Parts
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

            // 3. Iterate Question Groups
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

                // 4. Iterate Questions (Save in Batch for performance)
                List<Question> questions = new ArrayList<>();
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
                            LOG.warn("Invalid AnswerOption: {}", qDto.getCorrectOption());
                        }
                    }
                    question.setExplanation(qDto.getExplanation());
                    question.setQuestionGroup(group);
                    question.setPart(part); // Added part relation
                    questions.add(question);
                }
                questionRepository.saveAll(questions);
            }
        }
        
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

    @org.springframework.beans.factory.annotation.Autowired
    private com.toeic.exam.repository.ExamAttemptRepository examAttemptRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.toeic.exam.repository.UserAnswerRepository userAnswerRepository;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Exam : {}", id);
        
        // 1. Find all attempts for this exam
        // (Using findAll since ExamAttemptRepository might not have findByExamId)
        // Better: use EntityManager to avoid loading all objects
        entityManager.createQuery("DELETE FROM UserAnswer u WHERE u.examAttempt.id IN (SELECT a.id FROM ExamAttempt a WHERE a.exam.id = :examId)")
            .setParameter("examId", id).executeUpdate();
            
        entityManager.createQuery("DELETE FROM ExamAttempt a WHERE a.exam.id = :examId")
            .setParameter("examId", id).executeUpdate();

        entityManager.createQuery("DELETE FROM Question q WHERE q.part.id IN (SELECT p.id FROM Part p WHERE p.exam.id = :examId)")
            .setParameter("examId", id).executeUpdate();

        entityManager.createQuery("DELETE FROM QuestionGroup g WHERE g.part.id IN (SELECT p.id FROM Part p WHERE p.exam.id = :examId)")
            .setParameter("examId", id).executeUpdate();

        entityManager.createQuery("DELETE FROM Part p WHERE p.exam.id = :examId")
            .setParameter("examId", id).executeUpdate();

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

