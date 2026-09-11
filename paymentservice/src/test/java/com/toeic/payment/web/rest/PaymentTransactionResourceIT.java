package com.toeic.payment.web.rest;

import static com.toeic.payment.domain.PaymentTransactionAsserts.*;
import static com.toeic.payment.web.rest.TestUtil.createUpdateProxyForBean;
import static com.toeic.payment.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.payment.IntegrationTest;
import com.toeic.payment.domain.PaymentTransaction;
import com.toeic.payment.domain.enumeration.PaymentGateway;
import com.toeic.payment.domain.enumeration.PaymentStatus;
import com.toeic.payment.repository.PaymentTransactionRepository;
import com.toeic.payment.service.dto.PaymentTransactionDTO;
import com.toeic.payment.service.mapper.PaymentTransactionMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link PaymentTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentTransactionResourceIT {

    private static final String DEFAULT_ORDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final Long DEFAULT_SUBSCRIPTION_ID = 1L;
    private static final Long UPDATED_SUBSCRIPTION_ID = 2L;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final PaymentGateway DEFAULT_GATEWAY = PaymentGateway.VNPAY;
    private static final PaymentGateway UPDATED_GATEWAY = PaymentGateway.MOMO;

    private static final String DEFAULT_GATEWAY_TRANS_ID = "AAAAAAAAAA";
    private static final String UPDATED_GATEWAY_TRANS_ID = "BBBBBBBBBB";

    private static final PaymentStatus DEFAULT_STATUS = PaymentStatus.PENDING;
    private static final PaymentStatus UPDATED_STATUS = PaymentStatus.SUCCESS;

    private static final String DEFAULT_PAYMENT_URL = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_URL = "BBBBBBBBBB";

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final Instant DEFAULT_PAYMENT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PAYMENT_TIME = Instant.ofEpochMilli(1702035377500L);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.ofEpochMilli(1702035377500L);

    private static final String ENTITY_API_URL = "/api/payment-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentTransactionMockMvc;

    private PaymentTransaction paymentTransaction;

    private PaymentTransaction insertedPaymentTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentTransaction createEntity() {
        return new PaymentTransaction()
            .orderCode(DEFAULT_ORDER_CODE)
            .userId(DEFAULT_USER_ID)
            .subscriptionId(DEFAULT_SUBSCRIPTION_ID)
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .gateway(DEFAULT_GATEWAY)
            .gatewayTransId(DEFAULT_GATEWAY_TRANS_ID)
            .status(DEFAULT_STATUS)
            .paymentUrl(DEFAULT_PAYMENT_URL)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .paymentTime(DEFAULT_PAYMENT_TIME)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentTransaction createUpdatedEntity() {
        return new PaymentTransaction()
            .orderCode(UPDATED_ORDER_CODE)
            .userId(UPDATED_USER_ID)
            .subscriptionId(UPDATED_SUBSCRIPTION_ID)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .gateway(UPDATED_GATEWAY)
            .gatewayTransId(UPDATED_GATEWAY_TRANS_ID)
            .status(UPDATED_STATUS)
            .paymentUrl(UPDATED_PAYMENT_URL)
            .ipAddress(UPDATED_IP_ADDRESS)
            .paymentTime(UPDATED_PAYMENT_TIME)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        paymentTransaction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentTransaction != null) {
            paymentTransactionRepository.delete(insertedPaymentTransaction);
            insertedPaymentTransaction = null;
        }
    }

    @Test
    @Transactional
    void createPaymentTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);
        var returnedPaymentTransactionDTO = om.readValue(
            restPaymentTransactionMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(paymentTransactionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentTransactionDTO.class
        );

        // Validate the PaymentTransaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentTransaction = paymentTransactionMapper.toEntity(returnedPaymentTransactionDTO);
        assertPaymentTransactionUpdatableFieldsEquals(
            returnedPaymentTransaction,
            getPersistedPaymentTransaction(returnedPaymentTransaction)
        );

        insertedPaymentTransaction = returnedPaymentTransaction;
    }

    @Test
    @Transactional
    void createPaymentTransactionWithExistingId() throws Exception {
        // Create the PaymentTransaction with an existing ID
        paymentTransaction.setId(1L);
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setOrderCode(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setUserId(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubscriptionIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setSubscriptionId(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setAmount(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setCurrency(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGatewayIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setGateway(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setStatus(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentTransaction.setCreatedAt(null);

        // Create the PaymentTransaction, which fails.
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaymentTransactions() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get all the paymentTransactionList
        restPaymentTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderCode").value(hasItem(DEFAULT_ORDER_CODE)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].subscriptionId").value(hasItem(DEFAULT_SUBSCRIPTION_ID.intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].gateway").value(hasItem(DEFAULT_GATEWAY.toString())))
            .andExpect(jsonPath("$.[*].gatewayTransId").value(hasItem(DEFAULT_GATEWAY_TRANS_ID)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentUrl").value(hasItem(DEFAULT_PAYMENT_URL)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].paymentTime").value(hasItem(DEFAULT_PAYMENT_TIME.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPaymentTransaction() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        // Get the paymentTransaction
        restPaymentTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentTransaction.getId().intValue()))
            .andExpect(jsonPath("$.orderCode").value(DEFAULT_ORDER_CODE))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.subscriptionId").value(DEFAULT_SUBSCRIPTION_ID.intValue()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.gateway").value(DEFAULT_GATEWAY.toString()))
            .andExpect(jsonPath("$.gatewayTransId").value(DEFAULT_GATEWAY_TRANS_ID))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentUrl").value(DEFAULT_PAYMENT_URL))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.paymentTime").value(DEFAULT_PAYMENT_TIME.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPaymentTransaction() throws Exception {
        // Get the paymentTransaction
        restPaymentTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentTransaction() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentTransaction
        PaymentTransaction updatedPaymentTransaction = paymentTransactionRepository.findById(paymentTransaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaymentTransaction are not directly saved in db
        em.detach(updatedPaymentTransaction);
        updatedPaymentTransaction
            .orderCode(UPDATED_ORDER_CODE)
            .userId(UPDATED_USER_ID)
            .subscriptionId(UPDATED_SUBSCRIPTION_ID)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .gateway(UPDATED_GATEWAY)
            .gatewayTransId(UPDATED_GATEWAY_TRANS_ID)
            .status(UPDATED_STATUS)
            .paymentUrl(UPDATED_PAYMENT_URL)
            .ipAddress(UPDATED_IP_ADDRESS)
            .paymentTime(UPDATED_PAYMENT_TIME)
            .createdAt(UPDATED_CREATED_AT);
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(updatedPaymentTransaction);

        restPaymentTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentTransactionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentTransactionToMatchAllProperties(updatedPaymentTransaction);
    }

    @Test
    @Transactional
    void putNonExistingPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentTransactionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentTransaction using partial update
        PaymentTransaction partialUpdatedPaymentTransaction = new PaymentTransaction();
        partialUpdatedPaymentTransaction.setId(paymentTransaction.getId());

        partialUpdatedPaymentTransaction
            .userId(UPDATED_USER_ID)
            .subscriptionId(UPDATED_SUBSCRIPTION_ID)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .status(UPDATED_STATUS)
            .ipAddress(UPDATED_IP_ADDRESS);

        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentTransaction))
            )
            .andExpect(status().isOk());

        // Validate the PaymentTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentTransaction, paymentTransaction),
            getPersistedPaymentTransaction(paymentTransaction)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaymentTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentTransaction using partial update
        PaymentTransaction partialUpdatedPaymentTransaction = new PaymentTransaction();
        partialUpdatedPaymentTransaction.setId(paymentTransaction.getId());

        partialUpdatedPaymentTransaction
            .orderCode(UPDATED_ORDER_CODE)
            .userId(UPDATED_USER_ID)
            .subscriptionId(UPDATED_SUBSCRIPTION_ID)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .gateway(UPDATED_GATEWAY)
            .gatewayTransId(UPDATED_GATEWAY_TRANS_ID)
            .status(UPDATED_STATUS)
            .paymentUrl(UPDATED_PAYMENT_URL)
            .ipAddress(UPDATED_IP_ADDRESS)
            .paymentTime(UPDATED_PAYMENT_TIME)
            .createdAt(UPDATED_CREATED_AT);

        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentTransaction))
            )
            .andExpect(status().isOk());

        // Validate the PaymentTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentTransactionUpdatableFieldsEquals(
            partialUpdatedPaymentTransaction,
            getPersistedPaymentTransaction(partialUpdatedPaymentTransaction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentTransactionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentTransaction.setId(longCount.incrementAndGet());

        // Create the PaymentTransaction
        PaymentTransactionDTO paymentTransactionDTO = paymentTransactionMapper.toDto(paymentTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentTransaction() throws Exception {
        // Initialize the database
        insertedPaymentTransaction = paymentTransactionRepository.saveAndFlush(paymentTransaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentTransaction
        restPaymentTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentTransaction.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentTransactionRepository.count();
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

    protected PaymentTransaction getPersistedPaymentTransaction(PaymentTransaction paymentTransaction) {
        return paymentTransactionRepository.findById(paymentTransaction.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentTransactionToMatchAllProperties(PaymentTransaction expectedPaymentTransaction) {
        assertPaymentTransactionAllPropertiesEquals(expectedPaymentTransaction, getPersistedPaymentTransaction(expectedPaymentTransaction));
    }

    protected void assertPersistedPaymentTransactionToMatchUpdatableProperties(PaymentTransaction expectedPaymentTransaction) {
        assertPaymentTransactionAllUpdatablePropertiesEquals(
            expectedPaymentTransaction,
            getPersistedPaymentTransaction(expectedPaymentTransaction)
        );
    }
}
