package com.toeic.notification.web.rest;

import static com.toeic.notification.domain.NotificationTemplateAsserts.*;
import static com.toeic.notification.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.notification.IntegrationTest;
import com.toeic.notification.domain.NotificationTemplate;
import com.toeic.notification.domain.enumeration.NotificationChannel;
import com.toeic.notification.repository.NotificationTemplateRepository;
import com.toeic.notification.service.dto.NotificationTemplateDTO;
import com.toeic.notification.service.mapper.NotificationTemplateMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * Integration tests for the {@link NotificationTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationTemplateResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final NotificationChannel DEFAULT_CHANNEL = NotificationChannel.EMAIL;
    private static final NotificationChannel UPDATED_CHANNEL = NotificationChannel.PUSH_NOTIFICATION;

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_BODY_TEMPLATE = "AAAAAAAAAA";
    private static final String UPDATED_BODY_TEMPLATE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1702314721800L);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.ofEpochMilli(1702314721800L);

    private static final String ENTITY_API_URL = "/api/notification-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    @Autowired
    private NotificationTemplateMapper notificationTemplateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationTemplateMockMvc;

    private NotificationTemplate notificationTemplate;

    private NotificationTemplate insertedNotificationTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationTemplate createEntity() {
        return new NotificationTemplate()
            .code(DEFAULT_CODE)
            .channel(DEFAULT_CHANNEL)
            .subject(DEFAULT_SUBJECT)
            .bodyTemplate(DEFAULT_BODY_TEMPLATE)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationTemplate createUpdatedEntity() {
        return new NotificationTemplate()
            .code(UPDATED_CODE)
            .channel(UPDATED_CHANNEL)
            .subject(UPDATED_SUBJECT)
            .bodyTemplate(UPDATED_BODY_TEMPLATE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        notificationTemplate = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificationTemplate != null) {
            notificationTemplateRepository.delete(insertedNotificationTemplate);
            insertedNotificationTemplate = null;
        }
    }

    @Test
    @Transactional
    void createNotificationTemplate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);
        var returnedNotificationTemplateDTO = om.readValue(
            restNotificationTemplateMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(notificationTemplateDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationTemplateDTO.class
        );

        // Validate the NotificationTemplate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotificationTemplate = notificationTemplateMapper.toEntity(returnedNotificationTemplateDTO);
        assertNotificationTemplateUpdatableFieldsEquals(
            returnedNotificationTemplate,
            getPersistedNotificationTemplate(returnedNotificationTemplate)
        );

        insertedNotificationTemplate = returnedNotificationTemplate;
    }

    @Test
    @Transactional
    void createNotificationTemplateWithExistingId() throws Exception {
        // Create the NotificationTemplate with an existing ID
        notificationTemplate.setId(1L);
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationTemplate.setCode(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChannelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationTemplate.setChannel(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationTemplate.setSubject(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationTemplate.setIsActive(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationTemplate.setCreatedAt(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotificationTemplates() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.saveAndFlush(notificationTemplate);

        // Get all the notificationTemplateList
        restNotificationTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].bodyTemplate").value(hasItem(DEFAULT_BODY_TEMPLATE)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getNotificationTemplate() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.saveAndFlush(notificationTemplate);

        // Get the notificationTemplate
        restNotificationTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationTemplate.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.channel").value(DEFAULT_CHANNEL.toString()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.bodyTemplate").value(DEFAULT_BODY_TEMPLATE))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNotificationTemplate() throws Exception {
        // Get the notificationTemplate
        restNotificationTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificationTemplate() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.saveAndFlush(notificationTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationTemplate
        NotificationTemplate updatedNotificationTemplate = notificationTemplateRepository
            .findById(notificationTemplate.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedNotificationTemplate are not directly saved in db
        em.detach(updatedNotificationTemplate);
        updatedNotificationTemplate
            .code(UPDATED_CODE)
            .channel(UPDATED_CHANNEL)
            .subject(UPDATED_SUBJECT)
            .bodyTemplate(UPDATED_BODY_TEMPLATE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(updatedNotificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationTemplateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationTemplateToMatchAllProperties(updatedNotificationTemplate);
    }

    @Test
    @Transactional
    void putNonExistingNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationTemplate.setId(longCount.incrementAndGet());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationTemplateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationTemplate.setId(longCount.incrementAndGet());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationTemplate.setId(longCount.incrementAndGet());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.saveAndFlush(notificationTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationTemplate using partial update
        NotificationTemplate partialUpdatedNotificationTemplate = new NotificationTemplate();
        partialUpdatedNotificationTemplate.setId(notificationTemplate.getId());

        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationTemplate.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationTemplate))
            )
            .andExpect(status().isOk());

        // Validate the NotificationTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationTemplateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificationTemplate, notificationTemplate),
            getPersistedNotificationTemplate(notificationTemplate)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificationTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.saveAndFlush(notificationTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationTemplate using partial update
        NotificationTemplate partialUpdatedNotificationTemplate = new NotificationTemplate();
        partialUpdatedNotificationTemplate.setId(notificationTemplate.getId());

        partialUpdatedNotificationTemplate
            .code(UPDATED_CODE)
            .channel(UPDATED_CHANNEL)
            .subject(UPDATED_SUBJECT)
            .bodyTemplate(UPDATED_BODY_TEMPLATE)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationTemplate.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationTemplate))
            )
            .andExpect(status().isOk());

        // Validate the NotificationTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationTemplateUpdatableFieldsEquals(
            partialUpdatedNotificationTemplate,
            getPersistedNotificationTemplate(partialUpdatedNotificationTemplate)
        );
    }

    @Test
    @Transactional
    void patchNonExistingNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationTemplate.setId(longCount.incrementAndGet());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationTemplateDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationTemplate.setId(longCount.incrementAndGet());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationTemplate.setId(longCount.incrementAndGet());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotificationTemplate() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.saveAndFlush(notificationTemplate);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notificationTemplate
        restNotificationTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationTemplate.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificationTemplateRepository.count();
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

    protected NotificationTemplate getPersistedNotificationTemplate(NotificationTemplate notificationTemplate) {
        return notificationTemplateRepository.findById(notificationTemplate.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationTemplateToMatchAllProperties(NotificationTemplate expectedNotificationTemplate) {
        assertNotificationTemplateAllPropertiesEquals(
            expectedNotificationTemplate,
            getPersistedNotificationTemplate(expectedNotificationTemplate)
        );
    }

    protected void assertPersistedNotificationTemplateToMatchUpdatableProperties(NotificationTemplate expectedNotificationTemplate) {
        assertNotificationTemplateAllUpdatablePropertiesEquals(
            expectedNotificationTemplate,
            getPersistedNotificationTemplate(expectedNotificationTemplate)
        );
    }
}
