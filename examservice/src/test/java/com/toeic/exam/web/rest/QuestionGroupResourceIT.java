package com.toeic.exam.web.rest;

import static com.toeic.exam.domain.QuestionGroupAsserts.*;
import static com.toeic.exam.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.exam.IntegrationTest;
import com.toeic.exam.domain.Part;
import com.toeic.exam.domain.QuestionGroup;
import com.toeic.exam.repository.QuestionGroupRepository;
import com.toeic.exam.service.QuestionGroupService;
import com.toeic.exam.service.dto.QuestionGroupDTO;
import com.toeic.exam.service.mapper.QuestionGroupMapper;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link QuestionGroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuestionGroupResourceIT {

    private static final String DEFAULT_PASSAGE_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_PASSAGE_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_AUDIO_URL = "AAAAAAAAAA";
    private static final String UPDATED_AUDIO_URL = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/question-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuestionGroupRepository questionGroupRepository;

    @Mock
    private QuestionGroupRepository questionGroupRepositoryMock;

    @Autowired
    private QuestionGroupMapper questionGroupMapper;

    @Mock
    private QuestionGroupService questionGroupServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuestionGroupMockMvc;

    private QuestionGroup questionGroup;

    private QuestionGroup insertedQuestionGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionGroup createEntity(EntityManager em) {
        QuestionGroup questionGroup = new QuestionGroup()
            .passageText(DEFAULT_PASSAGE_TEXT)
            .audioUrl(DEFAULT_AUDIO_URL)
            .imageUrl(DEFAULT_IMAGE_URL);
        // Add required entity
        Part part;
        if (TestUtil.findAll(em, Part.class).isEmpty()) {
            part = PartResourceIT.createEntity(em);
            em.persist(part);
            em.flush();
        } else {
            part = TestUtil.findAll(em, Part.class).get(0);
        }
        questionGroup.setPart(part);
        return questionGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuestionGroup createUpdatedEntity(EntityManager em) {
        QuestionGroup updatedQuestionGroup = new QuestionGroup()
            .passageText(UPDATED_PASSAGE_TEXT)
            .audioUrl(UPDATED_AUDIO_URL)
            .imageUrl(UPDATED_IMAGE_URL);
        // Add required entity
        Part part;
        if (TestUtil.findAll(em, Part.class).isEmpty()) {
            part = PartResourceIT.createUpdatedEntity(em);
            em.persist(part);
            em.flush();
        } else {
            part = TestUtil.findAll(em, Part.class).get(0);
        }
        updatedQuestionGroup.setPart(part);
        return updatedQuestionGroup;
    }

    @BeforeEach
    void initTest() {
        questionGroup = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedQuestionGroup != null) {
            questionGroupRepository.delete(insertedQuestionGroup);
            insertedQuestionGroup = null;
        }
    }

    @Test
    @Transactional
    void createQuestionGroup() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuestionGroup
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(questionGroup);
        var returnedQuestionGroupDTO = om.readValue(
            restQuestionGroupMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(questionGroupDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuestionGroupDTO.class
        );

        // Validate the QuestionGroup in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedQuestionGroup = questionGroupMapper.toEntity(returnedQuestionGroupDTO);
        assertQuestionGroupUpdatableFieldsEquals(returnedQuestionGroup, getPersistedQuestionGroup(returnedQuestionGroup));

        insertedQuestionGroup = returnedQuestionGroup;
    }

    @Test
    @Transactional
    void createQuestionGroupWithExistingId() throws Exception {
        // Create the QuestionGroup with an existing ID
        questionGroup.setId(1L);
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(questionGroup);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionGroupMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllQuestionGroups() throws Exception {
        // Initialize the database
        insertedQuestionGroup = questionGroupRepository.saveAndFlush(questionGroup);

        // Get all the questionGroupList
        restQuestionGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(questionGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].passageText").value(hasItem(DEFAULT_PASSAGE_TEXT)))
            .andExpect(jsonPath("$.[*].audioUrl").value(hasItem(DEFAULT_AUDIO_URL)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuestionGroupsWithEagerRelationshipsIsEnabled() throws Exception {
        when(questionGroupServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restQuestionGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(questionGroupServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuestionGroupsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(questionGroupServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restQuestionGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(questionGroupRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuestionGroup() throws Exception {
        // Initialize the database
        insertedQuestionGroup = questionGroupRepository.saveAndFlush(questionGroup);

        // Get the questionGroup
        restQuestionGroupMockMvc
            .perform(get(ENTITY_API_URL_ID, questionGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(questionGroup.getId().intValue()))
            .andExpect(jsonPath("$.passageText").value(DEFAULT_PASSAGE_TEXT))
            .andExpect(jsonPath("$.audioUrl").value(DEFAULT_AUDIO_URL))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL));
    }

    @Test
    @Transactional
    void getNonExistingQuestionGroup() throws Exception {
        // Get the questionGroup
        restQuestionGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuestionGroup() throws Exception {
        // Initialize the database
        insertedQuestionGroup = questionGroupRepository.saveAndFlush(questionGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionGroup
        QuestionGroup updatedQuestionGroup = questionGroupRepository.findById(questionGroup.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuestionGroup are not directly saved in db
        em.detach(updatedQuestionGroup);
        updatedQuestionGroup.passageText(UPDATED_PASSAGE_TEXT).audioUrl(UPDATED_AUDIO_URL).imageUrl(UPDATED_IMAGE_URL);
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(updatedQuestionGroup);

        restQuestionGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionGroupDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionGroupDTO))
            )
            .andExpect(status().isOk());

        // Validate the QuestionGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuestionGroupToMatchAllProperties(updatedQuestionGroup);
    }

    @Test
    @Transactional
    void putNonExistingQuestionGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionGroup.setId(longCount.incrementAndGet());

        // Create the QuestionGroup
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(questionGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, questionGroupDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuestionGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionGroup.setId(longCount.incrementAndGet());

        // Create the QuestionGroup
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(questionGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(questionGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuestionGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionGroup.setId(longCount.incrementAndGet());

        // Create the QuestionGroup
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(questionGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionGroupMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(questionGroupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuestionGroupWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionGroup = questionGroupRepository.saveAndFlush(questionGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionGroup using partial update
        QuestionGroup partialUpdatedQuestionGroup = new QuestionGroup();
        partialUpdatedQuestionGroup.setId(questionGroup.getId());

        partialUpdatedQuestionGroup.passageText(UPDATED_PASSAGE_TEXT);

        restQuestionGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionGroup.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionGroup))
            )
            .andExpect(status().isOk());

        // Validate the QuestionGroup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionGroupUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuestionGroup, questionGroup),
            getPersistedQuestionGroup(questionGroup)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuestionGroupWithPatch() throws Exception {
        // Initialize the database
        insertedQuestionGroup = questionGroupRepository.saveAndFlush(questionGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the questionGroup using partial update
        QuestionGroup partialUpdatedQuestionGroup = new QuestionGroup();
        partialUpdatedQuestionGroup.setId(questionGroup.getId());

        partialUpdatedQuestionGroup.passageText(UPDATED_PASSAGE_TEXT).audioUrl(UPDATED_AUDIO_URL).imageUrl(UPDATED_IMAGE_URL);

        restQuestionGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuestionGroup.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuestionGroup))
            )
            .andExpect(status().isOk());

        // Validate the QuestionGroup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuestionGroupUpdatableFieldsEquals(partialUpdatedQuestionGroup, getPersistedQuestionGroup(partialUpdatedQuestionGroup));
    }

    @Test
    @Transactional
    void patchNonExistingQuestionGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionGroup.setId(longCount.incrementAndGet());

        // Create the QuestionGroup
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(questionGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuestionGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, questionGroupDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuestionGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionGroup.setId(longCount.incrementAndGet());

        // Create the QuestionGroup
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(questionGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuestionGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuestionGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        questionGroup.setId(longCount.incrementAndGet());

        // Create the QuestionGroup
        QuestionGroupDTO questionGroupDTO = questionGroupMapper.toDto(questionGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuestionGroupMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(questionGroupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuestionGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuestionGroup() throws Exception {
        // Initialize the database
        insertedQuestionGroup = questionGroupRepository.saveAndFlush(questionGroup);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the questionGroup
        restQuestionGroupMockMvc
            .perform(delete(ENTITY_API_URL_ID, questionGroup.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return questionGroupRepository.count();
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

    protected QuestionGroup getPersistedQuestionGroup(QuestionGroup questionGroup) {
        return questionGroupRepository.findById(questionGroup.getId()).orElseThrow();
    }

    protected void assertPersistedQuestionGroupToMatchAllProperties(QuestionGroup expectedQuestionGroup) {
        assertQuestionGroupAllPropertiesEquals(expectedQuestionGroup, getPersistedQuestionGroup(expectedQuestionGroup));
    }

    protected void assertPersistedQuestionGroupToMatchUpdatableProperties(QuestionGroup expectedQuestionGroup) {
        assertQuestionGroupAllUpdatablePropertiesEquals(expectedQuestionGroup, getPersistedQuestionGroup(expectedQuestionGroup));
    }
}
