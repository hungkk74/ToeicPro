package com.toeic.exam.service.mapper;

import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.UserAnswer;
import com.toeic.exam.service.dto.ExamAttemptDTO;
import com.toeic.exam.service.dto.QuestionDTO;
import com.toeic.exam.service.dto.UserAnswerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserAnswer} and its DTO {@link UserAnswerDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserAnswerMapper extends EntityMapper<UserAnswerDTO, UserAnswer> {
    @Mapping(target = "examAttempt", source = "examAttempt", qualifiedByName = "examAttemptId")
    @Mapping(target = "question", source = "question", qualifiedByName = "questionQuestionNumber")
    UserAnswerDTO toDto(UserAnswer s);

    @Named("examAttemptId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ExamAttemptDTO toDtoExamAttemptId(ExamAttempt examAttempt);

    @Named("questionQuestionNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "questionNumber", source = "questionNumber")
    QuestionDTO toDtoQuestionQuestionNumber(Question question);
}
