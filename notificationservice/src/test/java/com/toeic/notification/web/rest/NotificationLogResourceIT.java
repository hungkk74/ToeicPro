package com.toeic.notification.web.rest;

import static com.toeic.notification.domain.NotificationLogAsserts.*;
import static com.toeic.notification.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.notification.IntegrationTest;
import com.toeic.notification.domain.NotificationLog;
import com.toeic.notification.domain.enumeration.NotificationChannel;
import com.toeic.notification.domain.enumeration.NotificationStatus;
import com.toeic.notification.repository.NotificationLogRepository;
import com.toeic.notification.service.dto.NotificationLogDTO;
import com.toeic.notification.service.mapper.NotificationLogMapper;
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
 * Integration tests for the {@link NotificationLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationLogResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_RECIPIENT = "AAAAAAAAAA";
    private static final String UPDATED_RECIPIENT = "BBBBBBBBBB";

    private static final NotificationChannel DEFAULT_CHANNEL = NotificationChannel.EMAIL;
    private static final NotificationChannel UPDATED_CHANNEL = NotificationChannel.PUSH_NOTIFICATION;

    private static final NotificationStatus DEFAULT_STATUS = NotificationStatus.PENDING;
    private static final NotificationStatus UPDATED_STATUS = NotificationStatus.SENT;

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final Integer DEFAULT_RETRY_COUNT = 1;
    private static final Integer UPDATED_RETRY_COUNT = 2;

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.ofEpochMilli(1702314721800L);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1702314721800L);

    private static final String ENTITY_API_URL = "/api/notification-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private NotificationLogMapper notificationLogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationLogMockMvc;

    private NotificationLog notificationLog;

    private NotificationLog insertedNotificationLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationLog createEntity() {
        return new NotificationLog()
            .userId(DEFAULT_USER_ID)
            .recipient(DEFAULT_RECIPIENT)
            .channel(DEFAULT_CHANNEL)
            .status(DEFAULT_STATUS)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .retryCount(DEFAULT_RETRY_COUNT)
            .sentAt(DEFAULT_SENT_AT)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationLog createUpdatedEntity() {
        return new NotificationLog()
            .userId(UPDATED_USER_ID)
            .recipient(UPDATED_RECIPIENT)
            .channel(UPDATED_CHANNEL)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .sentAt(UPDATED_SENT_AT)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        notificationLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificationLog != null) {
            notificationLogRepository.delete(insertedNotificationLog);
            insertedNotificationLog = null;
        }
    }

    @Test
    @Transactional
    void createNotificationLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);
        var returnedNotificationLogDTO = om.readValue(
            restNotificationLogMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(notificationLogDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationLogDTO.class
        );

        // Validate the NotificationLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotificationLog = notificationLogMapper.toEntity(returnedNotificationLogDTO);
        assertNotificationLogUpdatableFieldsEquals(returnedNotificationLog, getPersistedNotificationLog(returnedNotificationLog));

        insertedNotificationLog = returnedNotificationLog;
    }

    @Test
    @Transactional
    void createNotificationLogWithExistingId() throws Exception {
        // Create the NotificationLog with an existing ID
        notificationLog.setId(1L);
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setUserId(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRecipientIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setRecipient(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChannelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setChannel(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setStatus(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setCreatedAt(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotificationLogs() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].recipient").value(hasItem(DEFAULT_RECIPIENT)))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get the notificationLog
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationLog.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.recipient").value(DEFAULT_RECIPIENT))
            .andExpect(jsonPath("$.channel").value(DEFAULT_CHANNEL.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.retryCount").value(DEFAULT_RETRY_COUNT))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNotificationLog() throws Exception {
        // Get the notificationLog
        restNotificationLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationLog
        NotificationLog updatedNotificationLog = notificationLogRepository.findById(notificationLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotificationLog are not directly saved in db
        em.detach(updatedNotificationLog);
        updatedNotificationLog
            .userId(UPDATED_USER_ID)
            .recipient(UPDATED_RECIPIENT)
            .channel(UPDATED_CHANNEL)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .sentAt(UPDATED_SENT_AT)
            .createdAt(UPDATED_CREATED_AT);
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(updatedNotificationLog);

        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationLogDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationLogToMatchAllProperties(updatedNotificationLog);
    }

    @Test
    @Transactional
    void putNonExistingNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationLogDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationLogWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationLog using partial update
        NotificationLog partialUpdatedNotificationLog = new NotificationLog();
        partialUpdatedNotificationLog.setId(notificationLog.getId());

        partialUpdatedNotificationLog.userId(UPDATED_USER_ID).recipient(UPDATED_RECIPIENT);

        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationLog.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationLog))
            )
            .andExpect(status().isOk());

        // Validate the NotificationLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificationLog, notificationLog),
            getPersistedNotificationLog(notificationLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificationLogWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationLog using partial update
        NotificationLog partialUpdatedNotificationLog = new NotificationLog();
        partialUpdatedNotificationLog.setId(notificationLog.getId());

        partialUpdatedNotificationLog
            .userId(UPDATED_USER_ID)
            .recipient(UPDATED_RECIPIENT)
            .channel(UPDATED_CHANNEL)
            .status(UPDATED_STATUS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .sentAt(UPDATED_SENT_AT)
            .createdAt(UPDATED_CREATED_AT);

        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationLog.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationLog))
            )
            .andExpect(status().isOk());

        // Validate the NotificationLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationLogUpdatableFieldsEquals(
            partialUpdatedNotificationLog,
            getPersistedNotificationLog(partialUpdatedNotificationLog)
        );
    }

    @Test
    @Transactional
    void patchNonExistingNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationLogDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notificationLog
        restNotificationLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationLog.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificationLogRepository.count();
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

    protected NotificationLog getPersistedNotificationLog(NotificationLog notificationLog) {
        return notificationLogRepository.findById(notificationLog.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationLogToMatchAllProperties(NotificationLog expectedNotificationLog) {
        assertNotificationLogAllPropertiesEquals(expectedNotificationLog, getPersistedNotificationLog(expectedNotificationLog));
    }

    protected void assertPersistedNotificationLogToMatchUpdatableProperties(NotificationLog expectedNotificationLog) {
        assertNotificationLogAllUpdatablePropertiesEquals(expectedNotificationLog, getPersistedNotificationLog(expectedNotificationLog));
    }
}
