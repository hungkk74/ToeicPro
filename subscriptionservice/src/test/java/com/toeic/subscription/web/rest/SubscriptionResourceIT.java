package com.toeic.subscription.web.rest;

import static com.toeic.subscription.domain.SubscriptionAsserts.*;
import static com.toeic.subscription.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.subscription.IntegrationTest;
import com.toeic.subscription.domain.Subscription;
import com.toeic.subscription.domain.enumeration.SubscriptionStatus;
import com.toeic.subscription.repository.SubscriptionRepository;
import com.toeic.subscription.service.SubscriptionService;
import com.toeic.subscription.service.dto.SubscriptionDTO;
import com.toeic.subscription.service.mapper.SubscriptionMapper;
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
 * Integration tests for the {@link SubscriptionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SubscriptionResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final SubscriptionStatus DEFAULT_STATUS = SubscriptionStatus.TRIAL;
    private static final SubscriptionStatus UPDATED_STATUS = SubscriptionStatus.ACTIVE;

    private static final Instant DEFAULT_STARTS_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTS_AT = Instant.ofEpochMilli(1703126422020L);

    private static final Instant DEFAULT_EXPIRES_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES_AT = Instant.ofEpochMilli(1703126422020L);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1703126422020L);

    private static final String ENTITY_API_URL = "/api/subscriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionRepository subscriptionRepositoryMock;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private SubscriptionService subscriptionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubscriptionMockMvc;

    private Subscription subscription;

    private Subscription insertedSubscription;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subscription createEntity() {
        return new Subscription()
            .userId(DEFAULT_USER_ID)
            .status(DEFAULT_STATUS)
            .startsAt(DEFAULT_STARTS_AT)
            .expiresAt(DEFAULT_EXPIRES_AT)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subscription createUpdatedEntity() {
        return new Subscription()
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .startsAt(UPDATED_STARTS_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        subscription = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubscription != null) {
            subscriptionRepository.delete(insertedSubscription);
            insertedSubscription = null;
        }
    }

    @Test
    @Transactional
    void createSubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);
        var returnedSubscriptionDTO = om.readValue(
            restSubscriptionMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SubscriptionDTO.class
        );

        // Validate the Subscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubscription = subscriptionMapper.toEntity(returnedSubscriptionDTO);
        assertSubscriptionUpdatableFieldsEquals(returnedSubscription, getPersistedSubscription(returnedSubscription));

        insertedSubscription = returnedSubscription;
    }

    @Test
    @Transactional
    void createSubscriptionWithExistingId() throws Exception {
        // Create the Subscription with an existing ID
        subscription.setId(1L);
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubscriptionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setUserId(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setStatus(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartsAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setStartsAt(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiresAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscription.setExpiresAt(null);

        // Create the Subscription, which fails.
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        restSubscriptionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSubscriptions() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        // Get all the subscriptionList
        restSubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startsAt").value(hasItem(DEFAULT_STARTS_AT.toString())))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubscriptionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(subscriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSubscriptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(subscriptionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubscriptionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(subscriptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSubscriptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(subscriptionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSubscription() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        // Get the subscription
        restSubscriptionMockMvc
            .perform(get(ENTITY_API_URL_ID, subscription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subscription.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.startsAt").value(DEFAULT_STARTS_AT.toString()))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSubscription() throws Exception {
        // Get the subscription
        restSubscriptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubscription() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscription
        Subscription updatedSubscription = subscriptionRepository.findById(subscription.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSubscription are not directly saved in db
        em.detach(updatedSubscription);
        updatedSubscription
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .startsAt(UPDATED_STARTS_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdAt(UPDATED_CREATED_AT);
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(updatedSubscription);

        restSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscriptionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubscriptionToMatchAllProperties(updatedSubscription);
    }

    @Test
    @Transactional
    void putNonExistingSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscriptionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscription using partial update
        Subscription partialUpdatedSubscription = new Subscription();
        partialUpdatedSubscription.setId(subscription.getId());

        partialUpdatedSubscription.userId(UPDATED_USER_ID).startsAt(UPDATED_STARTS_AT).createdAt(UPDATED_CREATED_AT);

        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscription.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubscription))
            )
            .andExpect(status().isOk());

        // Validate the Subscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSubscription, subscription),
            getPersistedSubscription(subscription)
        );
    }

    @Test
    @Transactional
    void fullUpdateSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscription using partial update
        Subscription partialUpdatedSubscription = new Subscription();
        partialUpdatedSubscription.setId(subscription.getId());

        partialUpdatedSubscription
            .userId(UPDATED_USER_ID)
            .status(UPDATED_STATUS)
            .startsAt(UPDATED_STARTS_AT)
            .expiresAt(UPDATED_EXPIRES_AT)
            .createdAt(UPDATED_CREATED_AT);

        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscription.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubscription))
            )
            .andExpect(status().isOk());

        // Validate the Subscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubscriptionUpdatableFieldsEquals(partialUpdatedSubscription, getPersistedSubscription(partialUpdatedSubscription));
    }

    @Test
    @Transactional
    void patchNonExistingSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subscriptionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscription.setId(longCount.incrementAndGet());

        // Create the Subscription
        SubscriptionDTO subscriptionDTO = subscriptionMapper.toDto(subscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subscriptionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubscription() throws Exception {
        // Initialize the database
        insertedSubscription = subscriptionRepository.saveAndFlush(subscription);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subscription
        restSubscriptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, subscription.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subscriptionRepository.count();
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

    protected Subscription getPersistedSubscription(Subscription subscription) {
        return subscriptionRepository.findById(subscription.getId()).orElseThrow();
    }

    protected void assertPersistedSubscriptionToMatchAllProperties(Subscription expectedSubscription) {
        assertSubscriptionAllPropertiesEquals(expectedSubscription, getPersistedSubscription(expectedSubscription));
    }

    protected void assertPersistedSubscriptionToMatchUpdatableProperties(Subscription expectedSubscription) {
        assertSubscriptionAllUpdatablePropertiesEquals(expectedSubscription, getPersistedSubscription(expectedSubscription));
    }
}
