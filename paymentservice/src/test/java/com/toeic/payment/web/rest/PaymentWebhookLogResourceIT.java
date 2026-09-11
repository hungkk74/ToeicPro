package com.toeic.payment.web.rest;

import static com.toeic.payment.domain.PaymentWebhookLogAsserts.*;
import static com.toeic.payment.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.payment.IntegrationTest;
import com.toeic.payment.domain.PaymentWebhookLog;
import com.toeic.payment.domain.enumeration.PaymentGateway;
import com.toeic.payment.repository.PaymentWebhookLogRepository;
import com.toeic.payment.service.PaymentWebhookLogService;
import com.toeic.payment.service.dto.PaymentWebhookLogDTO;
import com.toeic.payment.service.mapper.PaymentWebhookLogMapper;
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
 * Integration tests for the {@link PaymentWebhookLogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PaymentWebhookLogResourceIT {

    private static final String DEFAULT_ORDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_CODE = "BBBBBBBBBB";

    private static final PaymentGateway DEFAULT_GATEWAY = PaymentGateway.VNPAY;
    private static final PaymentGateway UPDATED_GATEWAY = PaymentGateway.MOMO;

    private static final String DEFAULT_RAW_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_RAW_PAYLOAD = "BBBBBBBBBB";

    private static final String DEFAULT_SIGNATURE = "AAAAAAAAAA";
    private static final String UPDATED_SIGNATURE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_VERIFIED = false;
    private static final Boolean UPDATED_IS_VERIFIED = true;

    private static final String DEFAULT_RESPONSE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1702035377500L);

    private static final String ENTITY_API_URL = "/api/payment-webhook-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentWebhookLogRepository paymentWebhookLogRepository;

    @Mock
    private PaymentWebhookLogRepository paymentWebhookLogRepositoryMock;

    @Autowired
    private PaymentWebhookLogMapper paymentWebhookLogMapper;

    @Mock
    private PaymentWebhookLogService paymentWebhookLogServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentWebhookLogMockMvc;

    private PaymentWebhookLog paymentWebhookLog;

    private PaymentWebhookLog insertedPaymentWebhookLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentWebhookLog createEntity() {
        return new PaymentWebhookLog()
            .orderCode(DEFAULT_ORDER_CODE)
            .gateway(DEFAULT_GATEWAY)
            .rawPayload(DEFAULT_RAW_PAYLOAD)
            .signature(DEFAULT_SIGNATURE)
            .isVerified(DEFAULT_IS_VERIFIED)
            .responseCode(DEFAULT_RESPONSE_CODE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentWebhookLog createUpdatedEntity() {
        return new PaymentWebhookLog()
            .orderCode(UPDATED_ORDER_CODE)
            .gateway(UPDATED_GATEWAY)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .signature(UPDATED_SIGNATURE)
            .isVerified(UPDATED_IS_VERIFIED)
            .responseCode(UPDATED_RESPONSE_CODE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        paymentWebhookLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentWebhookLog != null) {
            paymentWebhookLogRepository.delete(insertedPaymentWebhookLog);
            insertedPaymentWebhookLog = null;
        }
    }

    @Test
    @Transactional
    void createPaymentWebhookLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaymentWebhookLog
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);
        var returnedPaymentWebhookLogDTO = om.readValue(
            restPaymentWebhookLogMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(paymentWebhookLogDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentWebhookLogDTO.class
        );

        // Validate the PaymentWebhookLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentWebhookLog = paymentWebhookLogMapper.toEntity(returnedPaymentWebhookLogDTO);
        assertPaymentWebhookLogUpdatableFieldsEquals(returnedPaymentWebhookLog, getPersistedPaymentWebhookLog(returnedPaymentWebhookLog));

        insertedPaymentWebhookLog = returnedPaymentWebhookLog;
    }

    @Test
    @Transactional
    void createPaymentWebhookLogWithExistingId() throws Exception {
        // Create the PaymentWebhookLog with an existing ID
        paymentWebhookLog.setId(1L);
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentWebhookLogMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentWebhookLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentWebhookLog.setOrderCode(null);

        // Create the PaymentWebhookLog, which fails.
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        restPaymentWebhookLogMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGatewayIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentWebhookLog.setGateway(null);

        // Create the PaymentWebhookLog, which fails.
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        restPaymentWebhookLogMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsVerifiedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentWebhookLog.setIsVerified(null);

        // Create the PaymentWebhookLog, which fails.
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        restPaymentWebhookLogMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentWebhookLog.setCreatedAt(null);

        // Create the PaymentWebhookLog, which fails.
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        restPaymentWebhookLogMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaymentWebhookLogs() throws Exception {
        // Initialize the database
        insertedPaymentWebhookLog = paymentWebhookLogRepository.saveAndFlush(paymentWebhookLog);

        // Get all the paymentWebhookLogList
        restPaymentWebhookLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentWebhookLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderCode").value(hasItem(DEFAULT_ORDER_CODE)))
            .andExpect(jsonPath("$.[*].gateway").value(hasItem(DEFAULT_GATEWAY.toString())))
            .andExpect(jsonPath("$.[*].rawPayload").value(hasItem(DEFAULT_RAW_PAYLOAD)))
            .andExpect(jsonPath("$.[*].signature").value(hasItem(DEFAULT_SIGNATURE)))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED)))
            .andExpect(jsonPath("$.[*].responseCode").value(hasItem(DEFAULT_RESPONSE_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaymentWebhookLogsWithEagerRelationshipsIsEnabled() throws Exception {
        when(paymentWebhookLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaymentWebhookLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(paymentWebhookLogServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaymentWebhookLogsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(paymentWebhookLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaymentWebhookLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(paymentWebhookLogRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPaymentWebhookLog() throws Exception {
        // Initialize the database
        insertedPaymentWebhookLog = paymentWebhookLogRepository.saveAndFlush(paymentWebhookLog);

        // Get the paymentWebhookLog
        restPaymentWebhookLogMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentWebhookLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentWebhookLog.getId().intValue()))
            .andExpect(jsonPath("$.orderCode").value(DEFAULT_ORDER_CODE))
            .andExpect(jsonPath("$.gateway").value(DEFAULT_GATEWAY.toString()))
            .andExpect(jsonPath("$.rawPayload").value(DEFAULT_RAW_PAYLOAD))
            .andExpect(jsonPath("$.signature").value(DEFAULT_SIGNATURE))
            .andExpect(jsonPath("$.isVerified").value(DEFAULT_IS_VERIFIED))
            .andExpect(jsonPath("$.responseCode").value(DEFAULT_RESPONSE_CODE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPaymentWebhookLog() throws Exception {
        // Get the paymentWebhookLog
        restPaymentWebhookLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentWebhookLog() throws Exception {
        // Initialize the database
        insertedPaymentWebhookLog = paymentWebhookLogRepository.saveAndFlush(paymentWebhookLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentWebhookLog
        PaymentWebhookLog updatedPaymentWebhookLog = paymentWebhookLogRepository.findById(paymentWebhookLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaymentWebhookLog are not directly saved in db
        em.detach(updatedPaymentWebhookLog);
        updatedPaymentWebhookLog
            .orderCode(UPDATED_ORDER_CODE)
            .gateway(UPDATED_GATEWAY)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .signature(UPDATED_SIGNATURE)
            .isVerified(UPDATED_IS_VERIFIED)
            .responseCode(UPDATED_RESPONSE_CODE)
            .createdAt(UPDATED_CREATED_AT);
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(updatedPaymentWebhookLog);

        restPaymentWebhookLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentWebhookLogDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentWebhookLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentWebhookLogToMatchAllProperties(updatedPaymentWebhookLog);
    }

    @Test
    @Transactional
    void putNonExistingPaymentWebhookLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentWebhookLog.setId(longCount.incrementAndGet());

        // Create the PaymentWebhookLog
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentWebhookLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentWebhookLogDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentWebhookLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentWebhookLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentWebhookLog.setId(longCount.incrementAndGet());

        // Create the PaymentWebhookLog
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentWebhookLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentWebhookLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentWebhookLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentWebhookLog.setId(longCount.incrementAndGet());

        // Create the PaymentWebhookLog
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentWebhookLogMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentWebhookLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentWebhookLogWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentWebhookLog = paymentWebhookLogRepository.saveAndFlush(paymentWebhookLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentWebhookLog using partial update
        PaymentWebhookLog partialUpdatedPaymentWebhookLog = new PaymentWebhookLog();
        partialUpdatedPaymentWebhookLog.setId(paymentWebhookLog.getId());

        partialUpdatedPaymentWebhookLog
            .gateway(UPDATED_GATEWAY)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .signature(UPDATED_SIGNATURE)
            .isVerified(UPDATED_IS_VERIFIED)
            .responseCode(UPDATED_RESPONSE_CODE)
            .createdAt(UPDATED_CREATED_AT);

        restPaymentWebhookLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentWebhookLog.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentWebhookLog))
            )
            .andExpect(status().isOk());

        // Validate the PaymentWebhookLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentWebhookLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentWebhookLog, paymentWebhookLog),
            getPersistedPaymentWebhookLog(paymentWebhookLog)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaymentWebhookLogWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentWebhookLog = paymentWebhookLogRepository.saveAndFlush(paymentWebhookLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentWebhookLog using partial update
        PaymentWebhookLog partialUpdatedPaymentWebhookLog = new PaymentWebhookLog();
        partialUpdatedPaymentWebhookLog.setId(paymentWebhookLog.getId());

        partialUpdatedPaymentWebhookLog
            .orderCode(UPDATED_ORDER_CODE)
            .gateway(UPDATED_GATEWAY)
            .rawPayload(UPDATED_RAW_PAYLOAD)
            .signature(UPDATED_SIGNATURE)
            .isVerified(UPDATED_IS_VERIFIED)
            .responseCode(UPDATED_RESPONSE_CODE)
            .createdAt(UPDATED_CREATED_AT);

        restPaymentWebhookLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentWebhookLog.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentWebhookLog))
            )
            .andExpect(status().isOk());

        // Validate the PaymentWebhookLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentWebhookLogUpdatableFieldsEquals(
            partialUpdatedPaymentWebhookLog,
            getPersistedPaymentWebhookLog(partialUpdatedPaymentWebhookLog)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPaymentWebhookLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentWebhookLog.setId(longCount.incrementAndGet());

        // Create the PaymentWebhookLog
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentWebhookLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentWebhookLogDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentWebhookLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentWebhookLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentWebhookLog.setId(longCount.incrementAndGet());

        // Create the PaymentWebhookLog
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentWebhookLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentWebhookLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentWebhookLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentWebhookLog.setId(longCount.incrementAndGet());

        // Create the PaymentWebhookLog
        PaymentWebhookLogDTO paymentWebhookLogDTO = paymentWebhookLogMapper.toDto(paymentWebhookLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentWebhookLogMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentWebhookLogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentWebhookLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentWebhookLog() throws Exception {
        // Initialize the database
        insertedPaymentWebhookLog = paymentWebhookLogRepository.saveAndFlush(paymentWebhookLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentWebhookLog
        restPaymentWebhookLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentWebhookLog.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentWebhookLogRepository.count();
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

    protected PaymentWebhookLog getPersistedPaymentWebhookLog(PaymentWebhookLog paymentWebhookLog) {
        return paymentWebhookLogRepository.findById(paymentWebhookLog.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentWebhookLogToMatchAllProperties(PaymentWebhookLog expectedPaymentWebhookLog) {
        assertPaymentWebhookLogAllPropertiesEquals(expectedPaymentWebhookLog, getPersistedPaymentWebhookLog(expectedPaymentWebhookLog));
    }

    protected void assertPersistedPaymentWebhookLogToMatchUpdatableProperties(PaymentWebhookLog expectedPaymentWebhookLog) {
        assertPaymentWebhookLogAllUpdatablePropertiesEquals(
            expectedPaymentWebhookLog,
            getPersistedPaymentWebhookLog(expectedPaymentWebhookLog)
        );
    }
}
