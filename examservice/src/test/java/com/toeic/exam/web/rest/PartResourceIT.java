package com.toeic.exam.web.rest;

import static com.toeic.exam.domain.PartAsserts.*;
import static com.toeic.exam.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.exam.IntegrationTest;
import com.toeic.exam.domain.Exam;
import com.toeic.exam.domain.Part;
import com.toeic.exam.repository.PartRepository;
import com.toeic.exam.service.PartService;
import com.toeic.exam.service.dto.PartDTO;
import com.toeic.exam.service.mapper.PartMapper;
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
 * Integration tests for the {@link PartResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PartResourceIT {

    private static final Integer DEFAULT_PART_NUMBER = 1;
    private static final Integer UPDATED_PART_NUMBER = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_TOTAL_QUESTIONS = 1;
    private static final Integer UPDATED_TOTAL_QUESTIONS = 2;

    private static final String ENTITY_API_URL = "/api/parts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartRepository partRepository;

    @Mock
    private PartRepository partRepositoryMock;

    @Autowired
    private PartMapper partMapper;

    @Mock
    private PartService partServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartMockMvc;

    private Part part;

    private Part insertedPart;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Part createEntity(EntityManager em) {
        Part part = new Part().partNumber(DEFAULT_PART_NUMBER).name(DEFAULT_NAME).totalQuestions(DEFAULT_TOTAL_QUESTIONS);
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createEntity();
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).get(0);
        }
        part.setExam(exam);
        return part;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Part createUpdatedEntity(EntityManager em) {
        Part updatedPart = new Part().partNumber(UPDATED_PART_NUMBER).name(UPDATED_NAME).totalQuestions(UPDATED_TOTAL_QUESTIONS);
        // Add required entity
        Exam exam;
        if (TestUtil.findAll(em, Exam.class).isEmpty()) {
            exam = ExamResourceIT.createUpdatedEntity();
            em.persist(exam);
            em.flush();
        } else {
            exam = TestUtil.findAll(em, Exam.class).get(0);
        }
        updatedPart.setExam(exam);
        return updatedPart;
    }

    @BeforeEach
    void initTest() {
        part = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPart != null) {
            partRepository.delete(insertedPart);
            insertedPart = null;
        }
    }

    @Test
    @Transactional
    void createPart() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);
        var returnedPartDTO = om.readValue(
            restPartMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartDTO.class
        );

        // Validate the Part in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPart = partMapper.toEntity(returnedPartDTO);
        assertPartUpdatableFieldsEquals(returnedPart, getPersistedPart(returnedPart));

        insertedPart = returnedPart;
    }

    @Test
    @Transactional
    void createPartWithExistingId() throws Exception {
        // Create the Part with an existing ID
        part.setId(1L);
        PartDTO partDTO = partMapper.toDto(part);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPartNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        part.setPartNumber(null);

        // Create the Part, which fails.
        PartDTO partDTO = partMapper.toDto(part);

        restPartMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        part.setName(null);

        // Create the Part, which fails.
        PartDTO partDTO = partMapper.toDto(part);

        restPartMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalQuestionsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        part.setTotalQuestions(null);

        // Create the Part, which fails.
        PartDTO partDTO = partMapper.toDto(part);

        restPartMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParts() throws Exception {
        // Initialize the database
        insertedPart = partRepository.saveAndFlush(part);

        // Get all the partList
        restPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(part.getId().intValue())))
            .andExpect(jsonPath("$.[*].partNumber").value(hasItem(DEFAULT_PART_NUMBER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].totalQuestions").value(hasItem(DEFAULT_TOTAL_QUESTIONS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartsWithEagerRelationshipsIsEnabled() throws Exception {
        when(partServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restPartMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(partServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPartsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(partServiceMock.findAllWithEagerRelationships()).thenReturn(new ArrayList<>());

        restPartMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(partRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPart() throws Exception {
        // Initialize the database
        insertedPart = partRepository.saveAndFlush(part);

        // Get the part
        restPartMockMvc
            .perform(get(ENTITY_API_URL_ID, part.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(part.getId().intValue()))
            .andExpect(jsonPath("$.partNumber").value(DEFAULT_PART_NUMBER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.totalQuestions").value(DEFAULT_TOTAL_QUESTIONS));
    }

    @Test
    @Transactional
    void getNonExistingPart() throws Exception {
        // Get the part
        restPartMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPart() throws Exception {
        // Initialize the database
        insertedPart = partRepository.saveAndFlush(part);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the part
        Part updatedPart = partRepository.findById(part.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPart are not directly saved in db
        em.detach(updatedPart);
        updatedPart.partNumber(UPDATED_PART_NUMBER).name(UPDATED_NAME).totalQuestions(UPDATED_TOTAL_QUESTIONS);
        PartDTO partDTO = partMapper.toDto(updatedPart);

        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partDTO))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartToMatchAllProperties(updatedPart);
    }

    @Test
    @Transactional
    void putNonExistingPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        part.setId(longCount.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        part.setId(longCount.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        part.setId(longCount.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Part in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartWithPatch() throws Exception {
        // Initialize the database
        insertedPart = partRepository.saveAndFlush(part);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the part using partial update
        Part partialUpdatedPart = new Part();
        partialUpdatedPart.setId(part.getId());

        partialUpdatedPart.partNumber(UPDATED_PART_NUMBER).name(UPDATED_NAME).totalQuestions(UPDATED_TOTAL_QUESTIONS);

        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPart.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPart, part), getPersistedPart(part));
    }

    @Test
    @Transactional
    void fullUpdatePartWithPatch() throws Exception {
        // Initialize the database
        insertedPart = partRepository.saveAndFlush(part);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the part using partial update
        Part partialUpdatedPart = new Part();
        partialUpdatedPart.setId(part.getId());

        partialUpdatedPart.partNumber(UPDATED_PART_NUMBER).name(UPDATED_NAME).totalQuestions(UPDATED_TOTAL_QUESTIONS);

        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPart.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartUpdatableFieldsEquals(partialUpdatedPart, getPersistedPart(partialUpdatedPart));
    }

    @Test
    @Transactional
    void patchNonExistingPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        part.setId(longCount.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        part.setId(longCount.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPart() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        part.setId(longCount.incrementAndGet());

        // Create the Part
        PartDTO partDTO = partMapper.toDto(part);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Part in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePart() throws Exception {
        // Initialize the database
        insertedPart = partRepository.saveAndFlush(part);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the part
        restPartMockMvc
            .perform(delete(ENTITY_API_URL_ID, part.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partRepository.count();
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

    protected Part getPersistedPart(Part part) {
        return partRepository.findById(part.getId()).orElseThrow();
    }

    protected void assertPersistedPartToMatchAllProperties(Part expectedPart) {
        assertPartAllPropertiesEquals(expectedPart, getPersistedPart(expectedPart));
    }

    protected void assertPersistedPartToMatchUpdatableProperties(Part expectedPart) {
        assertPartAllUpdatablePropertiesEquals(expectedPart, getPersistedPart(expectedPart));
    }
}
