package com.toeic.exam.web.rest;

import static com.toeic.exam.domain.UserAnswerAsserts.*;
import static com.toeic.exam.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.exam.IntegrationTest;
import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.Question;
import com.toeic.exam.domain.UserAnswer;
import com.toeic.exam.domain.enumeration.AnswerOption;
import com.toeic.exam.repository.UserAnswerRepository;
import com.toeic.exam.service.UserAnswerService;
import com.toeic.exam.service.dto.UserAnswerDTO;
import com.toeic.exam.service.mapper.UserAnswerMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link UserAnswerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserAnswerResourceIT {

    private static final AnswerOption DEFAULT_SELECTED_OPTION = AnswerOption.A;
    private static final AnswerOption UPDATED_SELECTED_OPTION = AnswerOption.B;

    private static final Boolean DEFAULT_IS_CORRECT = false;
    private static final Boolean UPDATED_IS_CORRECT = true;

    private static final Integer DEFAULT_TIME_SPENT_SECONDS = 1;
    private static final Integer UPDATED_TIME_SPENT_SECONDS = 2;

    private static final String ENTITY_API_URL = "/api/user-answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Mock
    private UserAnswerRepository userAnswerRepositoryMock;

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Mock
    private UserAnswerService userAnswerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserAnswerMockMvc;

    private UserAnswer userAnswer;

    private UserAnswer insertedUserAnswer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAnswer createEntity(EntityManager em) {
        UserAnswer userAnswer = new UserAnswer()
            .selectedOption(DEFAULT_SELECTED_OPTION)
            .isCorrect(DEFAULT_IS_CORRECT)
            .timeSpentSeconds(DEFAULT_TIME_SPENT_SECONDS);
        // Add required entity
        ExamAttempt examAttempt;
        if (TestUtil.findAll(em, ExamAttempt.class).isEmpty()) {
            examAttempt = ExamAttemptResourceIT.createEntity(em);
            em.persist(examAttempt);
            em.flush();
        } else {
            examAttempt = TestUtil.findAll(em, ExamAttempt.class).get(0);
        }
        userAnswer.setExamAttempt(examAttempt);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createEntity(em);
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).get(0);
        }
        userAnswer.setQuestion(question);
        return userAnswer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAnswer createUpdatedEntity(EntityManager em) {
        UserAnswer updatedUserAnswer = new UserAnswer()
            .selectedOption(UPDATED_SELECTED_OPTION)
            .isCorrect(UPDATED_IS_CORRECT)
            .timeSpentSeconds(UPDATED_TIME_SPENT_SECONDS);
        // Add required entity
        ExamAttempt examAttempt;
        if (TestUtil.findAll(em, ExamAttempt.class).isEmpty()) {
            examAttempt = ExamAttemptResourceIT.createUpdatedEntity(em);
            em.persist(examAttempt);
            em.flush();
        } else {
            examAttempt = TestUtil.findAll(em, ExamAttempt.class).get(0);
        }
        updatedUserAnswer.setExamAttempt(examAttempt);
        // Add required entity
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            question = QuestionResourceIT.createUpdatedEntity(em);
            em.persist(question);
            em.flush();
        } else {
            question = TestUtil.findAll(em, Question.class).get(0);
        }
        updatedUserAnswer.setQuestion(question);
        return updatedUserAnswer;
    }

    @BeforeEach
    void initTest() {
        userAnswer = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedUserAnswer != null) {
            userAnswerRepository.delete(insertedUserAnswer);
            insertedUserAnswer = null;
        }
    }

    @Test
    @Transactional
    void createUserAnswer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);
        var returnedUserAnswerDTO = om.readValue(
            restUserAnswerMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAnswerDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserAnswerDTO.class
        );

        // Validate the UserAnswer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserAnswer = userAnswerMapper.toEntity(returnedUserAnswerDTO);
        assertUserAnswerUpdatableFieldsEquals(returnedUserAnswer, getPersistedUserAnswer(returnedUserAnswer));

        insertedUserAnswer = returnedUserAnswer;
    }

    @Test
    @Transactional
    void createUserAnswerWithExistingId() throws Exception {
        // Create the UserAnswer with an existing ID
        userAnswer.setId(1L);
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserAnswerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAnswerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserAnswers() throws Exception {
        // Initialize the database
        insertedUserAnswer = userAnswerRepository.saveAndFlush(userAnswer);

        // Get all the userAnswerList
        restUserAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAnswer.getId().intValue())))
            .andExpect(jsonPath("$.[*].selectedOption").value(hasItem(DEFAULT_SELECTED_OPTION.toString())))
            .andExpect(jsonPath("$.[*].isCorrect").value(hasItem(DEFAULT_IS_CORRECT)))
            .andExpect(jsonPath("$.[*].timeSpentSeconds").value(hasItem(DEFAULT_TIME_SPENT_SECONDS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserAnswersWithEagerRelationshipsIsEnabled() throws Exception {
        when(userAnswerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserAnswerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userAnswerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserAnswersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userAnswerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserAnswerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userAnswerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserAnswer() throws Exception {
        // Initialize the database
        insertedUserAnswer = userAnswerRepository.saveAndFlush(userAnswer);

        // Get the userAnswer
        restUserAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, userAnswer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userAnswer.getId().intValue()))
            .andExpect(jsonPath("$.selectedOption").value(DEFAULT_SELECTED_OPTION.toString()))
            .andExpect(jsonPath("$.isCorrect").value(DEFAULT_IS_CORRECT))
            .andExpect(jsonPath("$.timeSpentSeconds").value(DEFAULT_TIME_SPENT_SECONDS));
    }

    @Test
    @Transactional
    void getNonExistingUserAnswer() throws Exception {
        // Get the userAnswer
        restUserAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserAnswer() throws Exception {
        // Initialize the database
        insertedUserAnswer = userAnswerRepository.saveAndFlush(userAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAnswer
        UserAnswer updatedUserAnswer = userAnswerRepository.findById(userAnswer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserAnswer are not directly saved in db
        em.detach(updatedUserAnswer);
        updatedUserAnswer
            .selectedOption(UPDATED_SELECTED_OPTION)
            .isCorrect(UPDATED_IS_CORRECT)
            .timeSpentSeconds(UPDATED_TIME_SPENT_SECONDS);
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(updatedUserAnswer);

        restUserAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAnswerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAnswerDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserAnswerToMatchAllProperties(updatedUserAnswer);
    }

    @Test
    @Transactional
    void putNonExistingUserAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAnswer.setId(longCount.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAnswerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAnswer.setId(longCount.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAnswer.setId(longCount.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAnswerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedUserAnswer = userAnswerRepository.saveAndFlush(userAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAnswer using partial update
        UserAnswer partialUpdatedUserAnswer = new UserAnswer();
        partialUpdatedUserAnswer.setId(userAnswer.getId());

        partialUpdatedUserAnswer.timeSpentSeconds(UPDATED_TIME_SPENT_SECONDS);

        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAnswer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserAnswer))
            )
            .andExpect(status().isOk());

        // Validate the UserAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserAnswerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserAnswer, userAnswer),
            getPersistedUserAnswer(userAnswer)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedUserAnswer = userAnswerRepository.saveAndFlush(userAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAnswer using partial update
        UserAnswer partialUpdatedUserAnswer = new UserAnswer();
        partialUpdatedUserAnswer.setId(userAnswer.getId());

        partialUpdatedUserAnswer
            .selectedOption(UPDATED_SELECTED_OPTION)
            .isCorrect(UPDATED_IS_CORRECT)
            .timeSpentSeconds(UPDATED_TIME_SPENT_SECONDS);

        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAnswer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserAnswer))
            )
            .andExpect(status().isOk());

        // Validate the UserAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserAnswerUpdatableFieldsEquals(partialUpdatedUserAnswer, getPersistedUserAnswer(partialUpdatedUserAnswer));
    }

    @Test
    @Transactional
    void patchNonExistingUserAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAnswer.setId(longCount.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userAnswerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAnswer.setId(longCount.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAnswer.setId(longCount.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userAnswerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserAnswer() throws Exception {
        // Initialize the database
        insertedUserAnswer = userAnswerRepository.saveAndFlush(userAnswer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userAnswer
        restUserAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, userAnswer.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userAnswerRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected UserAnswer getPersistedUserAnswer(UserAnswer userAnswer) {
        return userAnswerRepository.findById(userAnswer.getId()).orElseThrow();
    }

    protected void assertPersistedUserAnswerToMatchAllProperties(UserAnswer expectedUserAnswer) {
        assertUserAnswerAllPropertiesEquals(expectedUserAnswer, getPersistedUserAnswer(expectedUserAnswer));
    }

    protected void assertPersistedUserAnswerToMatchUpdatableProperties(UserAnswer expectedUserAnswer) {
        assertUserAnswerAllUpdatablePropertiesEquals(expectedUserAnswer, getPersistedUserAnswer(expectedUserAnswer));
    }
}
