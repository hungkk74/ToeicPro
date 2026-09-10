package com.toeic.subscription.web.rest;

import static com.toeic.subscription.domain.PlanAsserts.*;
import static com.toeic.subscription.web.rest.TestUtil.createUpdateProxyForBean;
import static com.toeic.subscription.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.subscription.IntegrationTest;
import com.toeic.subscription.domain.Plan;
import com.toeic.subscription.repository.PlanRepository;
import com.toeic.subscription.service.dto.PlanDTO;
import com.toeic.subscription.service.mapper.PlanMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link PlanResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlanResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final Integer DEFAULT_DURATION_DAYS = 1;
    private static final Integer UPDATED_DURATION_DAYS = 2;

    private static final String DEFAULT_FEATURES = "AAAAAAAAAA";
    private static final String UPDATED_FEATURES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/plans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlanMockMvc;

    private Plan plan;

    private Plan insertedPlan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plan createEntity() {
        return new Plan()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .durationDays(DEFAULT_DURATION_DAYS)
            .features(DEFAULT_FEATURES)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plan createUpdatedEntity() {
        return new Plan()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .durationDays(UPDATED_DURATION_DAYS)
            .features(UPDATED_FEATURES)
            .isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        plan = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPlan != null) {
            planRepository.delete(insertedPlan);
            insertedPlan = null;
        }
    }

    @Test
    @Transactional
    void createPlan() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);
        var returnedPlanDTO = om.readValue(
            restPlanMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PlanDTO.class
        );

        // Validate the Plan in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlan = planMapper.toEntity(returnedPlanDTO);
        assertPlanUpdatableFieldsEquals(returnedPlan, getPersistedPlan(returnedPlan));

        insertedPlan = returnedPlan;
    }

    @Test
    @Transactional
    void createPlanWithExistingId() throws Exception {
        // Create the Plan with an existing ID
        plan.setId(1L);
        PlanDTO planDTO = planMapper.toDto(plan);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlanMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        plan.setCode(null);

        // Create the Plan, which fails.
        PlanDTO planDTO = planMapper.toDto(plan);

        restPlanMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        plan.setName(null);

        // Create the Plan, which fails.
        PlanDTO planDTO = planMapper.toDto(plan);

        restPlanMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        plan.setPrice(null);

        // Create the Plan, which fails.
        PlanDTO planDTO = planMapper.toDto(plan);

        restPlanMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationDaysIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        plan.setDurationDays(null);

        // Create the Plan, which fails.
        PlanDTO planDTO = planMapper.toDto(plan);

        restPlanMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlans() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.saveAndFlush(plan);

        // Get all the planList
        restPlanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plan.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].durationDays").value(hasItem(DEFAULT_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].features").value(hasItem(DEFAULT_FEATURES)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getPlan() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.saveAndFlush(plan);

        // Get the plan
        restPlanMockMvc
            .perform(get(ENTITY_API_URL_ID, plan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(plan.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.durationDays").value(DEFAULT_DURATION_DAYS))
            .andExpect(jsonPath("$.features").value(DEFAULT_FEATURES))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingPlan() throws Exception {
        // Get the plan
        restPlanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlan() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.saveAndFlush(plan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plan
        Plan updatedPlan = planRepository.findById(plan.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPlan are not directly saved in db
        em.detach(updatedPlan);
        updatedPlan
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .durationDays(UPDATED_DURATION_DAYS)
            .features(UPDATED_FEATURES)
            .isActive(UPDATED_IS_ACTIVE);
        PlanDTO planDTO = planMapper.toDto(updatedPlan);

        restPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planDTO))
            )
            .andExpect(status().isOk());

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlanToMatchAllProperties(updatedPlan);
    }

    @Test
    @Transactional
    void putNonExistingPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(planDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(planDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlanWithPatch() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.saveAndFlush(plan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plan using partial update
        Plan partialUpdatedPlan = new Plan();
        partialUpdatedPlan.setId(plan.getId());

        partialUpdatedPlan.code(UPDATED_CODE).name(UPDATED_NAME).price(UPDATED_PRICE).isActive(UPDATED_IS_ACTIVE);

        restPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlan.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlan))
            )
            .andExpect(status().isOk());

        // Validate the Plan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPlan, plan), getPersistedPlan(plan));
    }

    @Test
    @Transactional
    void fullUpdatePlanWithPatch() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.saveAndFlush(plan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the plan using partial update
        Plan partialUpdatedPlan = new Plan();
        partialUpdatedPlan.setId(plan.getId());

        partialUpdatedPlan
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .durationDays(UPDATED_DURATION_DAYS)
            .features(UPDATED_FEATURES)
            .isActive(UPDATED_IS_ACTIVE);

        restPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlan.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPlan))
            )
            .andExpect(status().isOk());

        // Validate the Plan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanUpdatableFieldsEquals(partialUpdatedPlan, getPersistedPlan(partialUpdatedPlan));
    }

    @Test
    @Transactional
    void patchNonExistingPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, planDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(planDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(planDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        plan.setId(longCount.incrementAndGet());

        // Create the Plan
        PlanDTO planDTO = planMapper.toDto(plan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(planDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlan() throws Exception {
        // Initialize the database
        insertedPlan = planRepository.saveAndFlush(plan);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the plan
        restPlanMockMvc
            .perform(delete(ENTITY_API_URL_ID, plan.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return planRepository.count();
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

    protected Plan getPersistedPlan(Plan plan) {
        return planRepository.findById(plan.getId()).orElseThrow();
    }

    protected void assertPersistedPlanToMatchAllProperties(Plan expectedPlan) {
        assertPlanAllPropertiesEquals(expectedPlan, getPersistedPlan(expectedPlan));
    }

    protected void assertPersistedPlanToMatchUpdatableProperties(Plan expectedPlan) {
        assertPlanAllUpdatablePropertiesEquals(expectedPlan, getPersistedPlan(expectedPlan));
    }
}
