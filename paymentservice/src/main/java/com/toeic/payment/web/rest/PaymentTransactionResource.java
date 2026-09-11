package com.toeic.payment.web.rest;

import com.toeic.payment.repository.PaymentTransactionRepository;
import com.toeic.payment.service.PaymentTransactionService;
import com.toeic.payment.service.dto.PaymentTransactionDTO;
import com.toeic.payment.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.toeic.payment.domain.PaymentTransaction}.
 */
@RestController
@RequestMapping("/api/payment-transactions")
public class PaymentTransactionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentTransactionResource.class);

    private static final String ENTITY_NAME = "paymentservicePaymentTransaction";

    @Value("${jhipster.clientApp.name:paymentservice}")
    private String applicationName;

    private final PaymentTransactionService paymentTransactionService;

    private final PaymentTransactionRepository paymentTransactionRepository;

    public PaymentTransactionResource(
        PaymentTransactionService paymentTransactionService,
        PaymentTransactionRepository paymentTransactionRepository
    ) {
        this.paymentTransactionService = paymentTransactionService;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    /**
     * {@code POST  /payment-transactions} : Create a new paymentTransaction.
     *
     * @param paymentTransactionDTO the paymentTransactionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentTransactionDTO, or with status {@code 400 (Bad Request)} if the paymentTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PaymentTransactionDTO> createPaymentTransaction(@Valid @RequestBody PaymentTransactionDTO paymentTransactionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PaymentTransaction : {}", paymentTransactionDTO);
        if (paymentTransactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new paymentTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paymentTransactionDTO = paymentTransactionService.save(paymentTransactionDTO);
        return ResponseEntity.created(new URI("/api/payment-transactions/" + paymentTransactionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paymentTransactionDTO.getId().toString()))
            .body(paymentTransactionDTO);
    }

    /**
     * {@code PUT  /payment-transactions/:id} : Updates an existing paymentTransaction.
     *
     * @param id the id of the paymentTransactionDTO to save.
     * @param paymentTransactionDTO the paymentTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the paymentTransactionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaymentTransactionDTO> updatePaymentTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaymentTransactionDTO paymentTransactionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PaymentTransaction : {}, {}", id, paymentTransactionDTO);
        if (paymentTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paymentTransactionDTO = paymentTransactionService.update(paymentTransactionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentTransactionDTO.getId().toString()))
            .body(paymentTransactionDTO);
    }

    /**
     * {@code PATCH  /payment-transactions/:id} : Partial updates given fields of an existing paymentTransaction, field will ignore if it is null
     *
     * @param id the id of the paymentTransactionDTO to save.
     * @param paymentTransactionDTO the paymentTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the paymentTransactionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paymentTransactionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PaymentTransactionDTO> partialUpdatePaymentTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaymentTransactionDTO paymentTransactionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PaymentTransaction partially : {}, {}", id, paymentTransactionDTO);
        if (paymentTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaymentTransactionDTO> result = paymentTransactionService.partialUpdate(paymentTransactionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentTransactionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /payment-transactions} : get all the Payment Transactions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Payment Transactions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PaymentTransactionDTO>> getAllPaymentTransactions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PaymentTransactions");
        Page<PaymentTransactionDTO> page = paymentTransactionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /payment-transactions/:id} : get the "id" paymentTransaction.
     *
     * @param id the id of the paymentTransactionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentTransactionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransactionDTO> getPaymentTransaction(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PaymentTransaction : {}", id);
        Optional<PaymentTransactionDTO> paymentTransactionDTO = paymentTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentTransactionDTO);
    }

    /**
     * {@code DELETE  /payment-transactions/:id} : delete the "id" paymentTransaction.
     *
     * @param id the id of the paymentTransactionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentTransaction(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PaymentTransaction : {}", id);
        paymentTransactionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
