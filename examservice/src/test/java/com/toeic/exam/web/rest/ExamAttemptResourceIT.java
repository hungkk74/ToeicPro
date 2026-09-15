package com.toeic.exam.web.rest;

import static com.toeic.exam.domain.ExamAttemptAsserts.*;
import static com.toeic.exam.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.exam.IntegrationTest;
import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.ExamAttempt;
import com.toeic.exam.domain.enumeration.AttemptStatus;
import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.service.ExamAttemptService;
import com.toeic.exam.service.dto.ExamAttemptDTO;
import com.toeic.exam.service.mapper.ExamAttemptMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * Integration tests for the {@link ExamAttemptResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExamAttemptResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final AttemptStatus DEFAULT_STATUS = AttemptStatus.IN_PROGRESS;
    private static final AttemptStatus UPDATED_STATUS = AttemptStatus.COMPLETED;

    private static final Integer DEFAULT_LISTENING_SCORE = 5;
    private static final Integer UPDATED_LISTENING_SCORE = 6;

    private static final Integer DEFAULT_READING_SCORE = 5;
    private static final Integer UPDATED_READING_SCORE = 6;

    private static final Integer DEFAULT_TOTAL_SCORE = 10;
    private static final Integer UPDATED_TOTAL_SCORE = 11;

    private static final Integer DEFAULT_CORRECT_ANSWERS = 1;
    private static final Integer UPDATED_CORRECT_ANSWERS = 2;

    private static final Integer DEFAULT_WRONG_ANSWERS = 1;
    private static final Integer UPDATED_WRONG_ANSWERS = 2;

    private static final Integer DEFAULT_SKIPPED_ANSWERS = 1;
    private static final Integer UPDATED_SKIPPED_ANSWERS = 2;

    private static final Integer DEFAULT_TIME_SPENT_SECONDS = 1;
    private static final Integer UPDATED_TIME_SPENT_SECONDS = 2;

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.ofEpochMilli(1702863340394L);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.ofEpochMilli(1702863340394L);

    private static final String ENTITY_API_URL = "/api/exam-attempts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Mock
    private ExamAttemptRepository examAttemptRepositoryMock;

    @Autowired
    private ExamAttemptMapper examAttemptMapper;

    @Mock
    private ExamAttemptService examAttemptServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamAttemptMockMvc;

    private ExamAttempt examAttempt;

    private ExamAttempt insertedExamAttempt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamAttempt createEntity(EntityManager em) {
        ExamAttempt examAttempt = new ExamAttempt()
            .userId(DEFAULT_USER_ID)
            .status(DEFAULT_STATUS)
            .listeningScore(DEFAULT_LISTENING_SCORE)
            .readingScore(DEFAULT_READING_SCORE)
            .totalScore(DEFAULT_TOTAL_SCORE)
            .correctAnswers(DEFAULT_CORRECT_ANSWERS)
            .wrongAnswers(DEFAULT_WRONG_ANSWERS)
            .skippedAnswers(DEFAULT_SKIPPED_ANSWERS)
            .timeSpentSeconds(DEFAULT_TIME_SPENT_SECONDS)
            .startedAt(DEFAULT_STARTED_AT)
            .completedAt(DEFAULT_COMPLETED_AT);
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createEntity();
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).get(0);
        }
        examAttempt.setExam(exam);
        return examAttempt;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamAttempt createUpdatedEntity(EntityManager em) {
        ExamAttempt updatedExamAttempt = new ExamAttempt()
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .listeningScore(UPDATED_LISTENING_SCORE)
            .readingScore(UPDATED_READING_SCORE)
            .totalScore(UPDATED_TOTAL_SCORE)
            .correctAnswers(UPDATED_CORRECT_ANSWERS)
            .wrongAnswers(UPDATED_WRONG_ANSWERS)
            .skippedAnswers(UPDATED_SKIPPED_ANSWERS)
            .timeSpentSeconds(UPDATED_TIME_SPENT_SECONDS)
            .startedAt(UPDATED_STARTED_AT)
            .completedAt(UPDATED_COMPLETED_AT);
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createUpdatedEntity();
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).get(0);
        }
        updatedExamAttempt.setExam(exam);
        return updatedExamAttempt;
    }

    @BeforeEach
    void initTest() {
        examAttempt = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedExamAttempt != null) {
            examAttemptRepository.delete(insertedExamAttempt);
            insertedExamAttempt = null;
        }
    }

    @Test
    @Transactional
    void createExamAttempt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);
        var returnedExamAttemptDTO = om.readValue(
            restExamAttemptMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExamAttemptDTO.class
        );

        // Validate the ExamAttempt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExamAttempt = examAttemptMapper.toEntity(returnedExamAttemptDTO);
        assertExamAttemptUpdatableFieldsEquals(returnedExamAttempt, getPersistedExamAttempt(returnedExamAttempt));

        insertedExamAttempt = returnedExamAttempt;
    }

    @Test
    @Transactional
    void createExamAttemptWithExistingId() throws Exception {
        // Create the ExamAttempt with an existing ID
        examAttempt.setId(1L);
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamAttemptMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        examAttempt.setUserId(null);

        // Create the ExamAttempt, which fails.
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        restExamAttemptMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        examAttempt.setStatus(null);

        // Create the ExamAttempt, which fails.
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        restExamAttemptMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        examAttempt.setStartedAt(null);

        // Create the ExamAttempt, which fails.
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        restExamAttemptMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExamAttempts() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get all the examAttemptList
        restExamAttemptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examAttempt.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].listeningScore").value(hasItem(DEFAULT_LISTENING_SCORE)))
            .andExpect(jsonPath("$.[*].readingScore").value(hasItem(DEFAULT_READING_SCORE)))
            .andExpect(jsonPath("$.[*].totalScore").value(hasItem(DEFAULT_TOTAL_SCORE)))
            .andExpect(jsonPath("$.[*].correctAnswers").value(hasItem(DEFAULT_CORRECT_ANSWERS)))
            .andExpect(jsonPath("$.[*].wrongAnswers").value(hasItem(DEFAULT_WRONG_ANSWERS)))
            .andExpect(jsonPath("$.[*].skippedAnswers").value(hasItem(DEFAULT_SKIPPED_ANSWERS)))
            .andExpect(jsonPath("$.[*].timeSpentSeconds").value(hasItem(DEFAULT_TIME_SPENT_SECONDS)))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(DEFAULT_STARTED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamAttemptsWithEagerRelationshipsIsEnabled() throws Exception {
        when(examAttemptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamAttemptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(examAttemptServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExamAttemptsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(examAttemptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExamAttemptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(examAttemptRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getExamAttempt() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        // Get the examAttempt
        restExamAttemptMockMvc
            .perform(get(ENTITY_API_URL_ID, examAttempt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(examAttempt.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.listeningScore").value(DEFAULT_LISTENING_SCORE))
            .andExpect(jsonPath("$.readingScore").value(DEFAULT_READING_SCORE))
            .andExpect(jsonPath("$.totalScore").value(DEFAULT_TOTAL_SCORE))
            .andExpect(jsonPath("$.correctAnswers").value(DEFAULT_CORRECT_ANSWERS))
            .andExpect(jsonPath("$.wrongAnswers").value(DEFAULT_WRONG_ANSWERS))
            .andExpect(jsonPath("$.skippedAnswers").value(DEFAULT_SKIPPED_ANSWERS))
            .andExpect(jsonPath("$.timeSpentSeconds").value(DEFAULT_TIME_SPENT_SECONDS))
            .andExpect(jsonPath("$.startedAt").value(DEFAULT_STARTED_AT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingExamAttempt() throws Exception {
        // Get the examAttempt
        restExamAttemptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExamAttempt() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAttempt
        ExamAttempt updatedExamAttempt = examAttemptRepository.findById(examAttempt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExamAttempt are not directly saved in db
        em.detach(updatedExamAttempt);
        updatedExamAttempt
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .listeningScore(UPDATED_LISTENING_SCORE)
            .readingScore(UPDATED_READING_SCORE)
            .totalScore(UPDATED_TOTAL_SCORE)
            .correctAnswers(UPDATED_CORRECT_ANSWERS)
            .wrongAnswers(UPDATED_WRONG_ANSWERS)
            .skippedAnswers(UPDATED_SKIPPED_ANSWERS)
            .timeSpentSeconds(UPDATED_TIME_SPENT_SECONDS)
            .startedAt(UPDATED_STARTED_AT)
            .completedAt(UPDATED_COMPLETED_AT);
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(updatedExamAttempt);

        restExamAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examAttemptDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExamAttemptToMatchAllProperties(updatedExamAttempt);
    }

    @Test
    @Transactional
    void putNonExistingExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examAttemptDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(examAttemptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAttempt using partial update
        ExamAttempt partialUpdatedExamAttempt = new ExamAttempt();
        partialUpdatedExamAttempt.setId(examAttempt.getId());

        partialUpdatedExamAttempt
            .listeningScore(UPDATED_LISTENING_SCORE)
            .readingScore(UPDATED_READING_SCORE)
            .correctAnswers(UPDATED_CORRECT_ANSWERS)
            .wrongAnswers(UPDATED_WRONG_ANSWERS)
            .skippedAnswers(UPDATED_SKIPPED_ANSWERS)
            .timeSpentSeconds(UPDATED_TIME_SPENT_SECONDS);

        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamAttempt.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamAttempt))
            )
            .andExpect(status().isOk());

        // Validate the ExamAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamAttemptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExamAttempt, examAttempt),
            getPersistedExamAttempt(examAttempt)
        );
    }

    @Test
    @Transactional
    void fullUpdateExamAttemptWithPatch() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the examAttempt using partial update
        ExamAttempt partialUpdatedExamAttempt = new ExamAttempt();
        partialUpdatedExamAttempt.setId(examAttempt.getId());

        partialUpdatedExamAttempt
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .listeningScore(UPDATED_LISTENING_SCORE)
            .readingScore(UPDATED_READING_SCORE)
            .totalScore(UPDATED_TOTAL_SCORE)
            .correctAnswers(UPDATED_CORRECT_ANSWERS)
            .wrongAnswers(UPDATED_WRONG_ANSWERS)
            .skippedAnswers(UPDATED_SKIPPED_ANSWERS)
            .timeSpentSeconds(UPDATED_TIME_SPENT_SECONDS)
            .startedAt(UPDATED_STARTED_AT)
            .completedAt(UPDATED_COMPLETED_AT);

        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamAttempt.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExamAttempt))
            )
            .andExpect(status().isOk());

        // Validate the ExamAttempt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExamAttemptUpdatableFieldsEquals(partialUpdatedExamAttempt, getPersistedExamAttempt(partialUpdatedExamAttempt));
    }

    @Test
    @Transactional
    void patchNonExistingExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examAttemptDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExamAttempt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        examAttempt.setId(longCount.incrementAndGet());

        // Create the ExamAttempt
        ExamAttemptDTO examAttemptDTO = examAttemptMapper.toDto(examAttempt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamAttemptMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(examAttemptDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamAttempt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExamAttempt() throws Exception {
        // Initialize the database
        insertedExamAttempt = examAttemptRepository.saveAndFlush(examAttempt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the examAttempt
        restExamAttemptMockMvc
            .perform(delete(ENTITY_API_URL_ID, examAttempt.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return examAttemptRepository.count();
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

    protected ExamAttempt getPersistedExamAttempt(ExamAttempt examAttempt) {
        return examAttemptRepository.findById(examAttempt.getId()).orElseThrow();
    }

    protected void assertPersistedExamAttemptToMatchAllProperties(ExamAttempt expectedExamAttempt) {
        assertExamAttemptAllPropertiesEquals(expectedExamAttempt, getPersistedExamAttempt(expectedExamAttempt));
    }

    protected void assertPersistedExamAttemptToMatchUpdatableProperties(ExamAttempt expectedExamAttempt) {
        assertExamAttemptAllUpdatablePropertiesEquals(expectedExamAttempt, getPersistedExamAttempt(expectedExamAttempt));
    }
}
