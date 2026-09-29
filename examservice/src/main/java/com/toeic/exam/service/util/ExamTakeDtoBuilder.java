package com.toeic.exam.service.util;

import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.service.dto.take.ExamTakeDTO;
import com.toeic.exam.service.dto.take.PartTakeDTO;
import com.toeic.exam.service.dto.take.QuestionGroupTakeDTO;
import com.toeic.exam.service.dto.take.QuestionTakeDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Builder chuyển đổi cấu trúc phẳng các câu hỏi, nhóm câu hỏi và part thành cây ExamTakeDTO phân cấp.
 */
@Component
public class ExamTakeDtoBuilder {

    public ExamTakeDTO build(
        Exam exam,
        List<Part> parts,
        List<QuestionGroup> groups,
        List<Question> questions
    ) {
        Map<Long, List<QuestionTakeDTO>> groupQuestionsMap = new HashMap<>();
        Map<Long, List<QuestionTakeDTO>> standaloneMap = new HashMap<>();

        for (Question q : questions) {
            QuestionTakeDTO qDTO = new QuestionTakeDTO(
                q.getId(),
                q.getQuestionNumber(),
                q.getContent(),
                q.getImageUrl(),
                q.getAudioUrl(),
                q.getOptionA(),
                q.getOptionB(),
                q.getOptionC(),
                q.getOptionD()
            );
            if (q.getQuestionGroup() != null) {
                groupQuestionsMap.computeIfAbsent(q.getQuestionGroup().getId(), k -> new ArrayList<>()).add(qDTO);
            } else if (q.getPart() != null) {
                standaloneMap.computeIfAbsent(q.getPart().getId(), k -> new ArrayList<>()).add(qDTO);
            }
        }

        Map<Long, List<QuestionGroupTakeDTO>> partGroupsMap = new HashMap<>();
        for (QuestionGroup g : groups) {
            List<QuestionTakeDTO> qList = groupQuestionsMap.getOrDefault(g.getId(), List.of());
            QuestionGroupTakeDTO gDTO = new QuestionGroupTakeDTO(
                g.getId(),
                g.getPassageText(),
                g.getAudioUrl(),
                g.getImageUrl(),
                qList
            );
            if (g.getPart() != null) {
                partGroupsMap.computeIfAbsent(g.getPart().getId(), k -> new ArrayList<>()).add(gDTO);
            }
        }

        List<PartTakeDTO> partDTOs = new ArrayList<>();
        for (Part p : parts) {
            List<QuestionGroupTakeDTO> gList = partGroupsMap.getOrDefault(p.getId(), List.of());
            List<QuestionTakeDTO> sList = standaloneMap.getOrDefault(p.getId(), List.of());
            partDTOs.add(new PartTakeDTO(
                p.getId(),
                p.getPartNumber(),
                p.getName(),
                p.getTotalQuestions(),
                gList,
                sList
            ));
        }

        return new ExamTakeDTO(
            exam.getId(),
            exam.getCode(),
            exam.getTitle(),
            exam.getDurationMinutes(),
            exam.getTotalQuestions(),
            exam.getAudioFullUrl(),
            partDTOs
        );
    }
}
