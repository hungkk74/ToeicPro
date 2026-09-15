package com.toeic.course.web.rest;

import static com.toeic.course.domain.LessonProgressAsserts.*;
import static com.toeic.course.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.course.IntegrationTest;
import com.toeic.course.domain.Lesson;
import com.toeic.course.domain.LessonProgress;
import com.toeic.course.repository.LessonProgressRepository;
import com.toeic.course.service.LessonProgressService;
import com.toeic.course.service.dto.LessonProgressDTO;
import com.toeic.course.service.mapper.LessonProgressMapper;
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
 * Integration tests for the {@link LessonProgressResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LessonProgressResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_COMPLETED = false;
    private static final Boolean UPDATED_IS_COMPLETED = true;

    private static final Integer DEFAULT_LAST_WATCHED_SECOND = 0;
    private static final Integer UPDATED_LAST_WATCHED_SECOND = 1;

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.ofEpochMilli(1702143501280L);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1702143501280L);

    private static final String ENTITY_API_URL = "/api/lesson-progresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private LessonProgressRepository lessonProgressRepositoryMock;

    @Autowired
    private LessonProgressMapper lessonProgressMapper;

    @Mock
    private LessonProgressService lessonProgressServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLessonProgressMockMvc;

    private LessonProgress lessonProgress;

    private LessonProgress insertedLessonProgress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonProgress createEntity(EntityManager em) {
        LessonProgress lessonProgress = new LessonProgress()
            .userId(DEFAULT_USER_ID)
            .isCompleted(DEFAULT_IS_COMPLETED)
            .lastWatchedSecond(DEFAULT_LAST_WATCHED_SECOND)
            .completedAt(DEFAULT_COMPLETED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        lessonProgress.setLesson(lesson);
        return lessonProgress;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonProgress createUpdatedEntity(EntityManager em) {
        LessonProgress updatedLessonProgress = new LessonProgress()
            .userId(UPDATED_USER_ID)
            .isCompleted(UPDATED_IS_COMPLETED)
            .lastWatchedSecond(UPDATED_LAST_WATCHED_SECOND)
            .completedAt(UPDATED_COMPLETED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createUpdatedEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        updatedLessonProgress.setLesson(lesson);
        return updatedLessonProgress;
    }

    @BeforeEach
    void initTest() {
        lessonProgress = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLessonProgress != null) {
            lessonProgressRepository.delete(insertedLessonProgress);
            insertedLessonProgress = null;
        }
    }

    @Test
    @Transactional
    void createLessonProgress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);
        var returnedLessonProgressDTO = om.readValue(
            restLessonProgressMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(lessonProgressDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LessonProgressDTO.class
        );

        // Validate the LessonProgress in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLessonProgress = lessonProgressMapper.toEntity(returnedLessonProgressDTO);
        assertLessonProgressUpdatableFieldsEquals(returnedLessonProgress, getPersistedLessonProgress(returnedLessonProgress));

        insertedLessonProgress = returnedLessonProgress;
    }

    @Test
    @Transactional
    void createLessonProgressWithExistingId() throws Exception {
        // Create the LessonProgress with an existing ID
        lessonProgress.setId(1L);
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonProgressMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lessonProgress.setUserId(null);

        // Create the LessonProgress, which fails.
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        restLessonProgressMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsCompletedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lessonProgress.setIsCompleted(null);

        // Create the LessonProgress, which fails.
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        restLessonProgressMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lessonProgress.setUpdatedAt(null);

        // Create the LessonProgress, which fails.
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        restLessonProgressMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLessonProgresses() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get all the lessonProgressList
        restLessonProgressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lessonProgress.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].isCompleted").value(hasItem(DEFAULT_IS_COMPLETED)))
            .andExpect(jsonPath("$.[*].lastWatchedSecond").value(hasItem(DEFAULT_LAST_WATCHED_SECOND)))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLessonProgressesWithEagerRelationshipsIsEnabled() throws Exception {
        when(lessonProgressServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLessonProgressMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(lessonProgressServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLessonProgressesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(lessonProgressServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLessonProgressMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(lessonProgressRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLessonProgress() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        // Get the lessonProgress
        restLessonProgressMockMvc
            .perform(get(ENTITY_API_URL_ID, lessonProgress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lessonProgress.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.isCompleted").value(DEFAULT_IS_COMPLETED))
            .andExpect(jsonPath("$.lastWatchedSecond").value(DEFAULT_LAST_WATCHED_SECOND))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLessonProgress() throws Exception {
        // Get the lessonProgress
        restLessonProgressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLessonProgress() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lessonProgress
        LessonProgress updatedLessonProgress = lessonProgressRepository.findById(lessonProgress.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLessonProgress are not directly saved in db
        em.detach(updatedLessonProgress);
        updatedLessonProgress
            .userId(UPDATED_USER_ID)
            .isCompleted(UPDATED_IS_COMPLETED)
            .lastWatchedSecond(UPDATED_LAST_WATCHED_SECOND)
            .completedAt(UPDATED_COMPLETED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(updatedLessonProgress);

        restLessonProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonProgressDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isOk());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLessonProgressToMatchAllProperties(updatedLessonProgress);
    }

    @Test
    @Transactional
    void putNonExistingLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonProgressDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLessonProgressWithPatch() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lessonProgress using partial update
        LessonProgress partialUpdatedLessonProgress = new LessonProgress();
        partialUpdatedLessonProgress.setId(lessonProgress.getId());

        partialUpdatedLessonProgress
            .isCompleted(UPDATED_IS_COMPLETED)
            .lastWatchedSecond(UPDATED_LAST_WATCHED_SECOND)
            .completedAt(UPDATED_COMPLETED_AT);

        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonProgress.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLessonProgress))
            )
            .andExpect(status().isOk());

        // Validate the LessonProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLessonProgressUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLessonProgress, lessonProgress),
            getPersistedLessonProgress(lessonProgress)
        );
    }

    @Test
    @Transactional
    void fullUpdateLessonProgressWithPatch() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lessonProgress using partial update
        LessonProgress partialUpdatedLessonProgress = new LessonProgress();
        partialUpdatedLessonProgress.setId(lessonProgress.getId());

        partialUpdatedLessonProgress
            .userId(UPDATED_USER_ID)
            .isCompleted(UPDATED_IS_COMPLETED)
            .lastWatchedSecond(UPDATED_LAST_WATCHED_SECOND)
            .completedAt(UPDATED_COMPLETED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonProgress.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLessonProgress))
            )
            .andExpect(status().isOk());

        // Validate the LessonProgress in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLessonProgressUpdatableFieldsEquals(partialUpdatedLessonProgress, getPersistedLessonProgress(partialUpdatedLessonProgress));
    }

    @Test
    @Transactional
    void patchNonExistingLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lessonProgressDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLessonProgress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lessonProgress.setId(longCount.incrementAndGet());

        // Create the LessonProgress
        LessonProgressDTO lessonProgressDTO = lessonProgressMapper.toDto(lessonProgress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonProgressMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lessonProgressDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonProgress in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLessonProgress() throws Exception {
        // Initialize the database
        insertedLessonProgress = lessonProgressRepository.saveAndFlush(lessonProgress);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the lessonProgress
        restLessonProgressMockMvc
            .perform(delete(ENTITY_API_URL_ID, lessonProgress.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return lessonProgressRepository.count();
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

    protected LessonProgress getPersistedLessonProgress(LessonProgress lessonProgress) {
        return lessonProgressRepository.findById(lessonProgress.getId()).orElseThrow();
    }

    protected void assertPersistedLessonProgressToMatchAllProperties(LessonProgress expectedLessonProgress) {
        assertLessonProgressAllPropertiesEquals(expectedLessonProgress, getPersistedLessonProgress(expectedLessonProgress));
    }

    protected void assertPersistedLessonProgressToMatchUpdatableProperties(LessonProgress expectedLessonProgress) {
        assertLessonProgressAllUpdatablePropertiesEquals(expectedLessonProgress, getPersistedLessonProgress(expectedLessonProgress));
    }
}
