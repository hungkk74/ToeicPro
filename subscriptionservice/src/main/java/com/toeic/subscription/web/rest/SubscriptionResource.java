package com.toeic.subscription.web.rest;

import com.toeic.subscription.repository.SubscriptionRepository;
import com.toeic.subscription.service.SubscriptionService;
import com.toeic.subscription.service.dto.SubscriptionDTO;
import com.toeic.subscription.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.toeic.subscription.domain.Subscription}.
 */
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionResource.class);

    private static final String ENTITY_NAME = "subscriptionserviceSubscription";

    @Value("${jhipster.clientApp.name:subscriptionservice}")
    private String applicationName;

    private final SubscriptionService subscriptionService;

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionResource(SubscriptionService subscriptionService, SubscriptionRepository subscriptionRepository) {
        this.subscriptionService = subscriptionService;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * {@code POST  /subscriptions} : Create a new subscription.
     *
     * @param subscriptionDTO the subscriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subscriptionDTO, or with status {@code 400 (Bad Request)} if the subscription has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SubscriptionDTO> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Subscription : {}", subscriptionDTO);
        if (subscriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new subscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        subscriptionDTO = subscriptionService.save(subscriptionDTO);
        return ResponseEntity.created(new URI("/api/subscriptions/" + subscriptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, subscriptionDTO.getId().toString()))
            .body(subscriptionDTO);
    }

    /**
     * {@code PUT  /subscriptions/:id} : Updates an existing subscription.
     *
     * @param id the id of the subscriptionDTO to save.
     * @param subscriptionDTO the subscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the subscriptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> updateSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubscriptionDTO subscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Subscription : {}, {}", id, subscriptionDTO);
        if (subscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        subscriptionDTO = subscriptionService.update(subscriptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscriptionDTO.getId().toString()))
            .body(subscriptionDTO);
    }

    /**
     * {@code PATCH  /subscriptions/:id} : Partial updates given fields of an existing subscription, field will ignore if it is null
     *
     * @param id the id of the subscriptionDTO to save.
     * @param subscriptionDTO the subscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the subscriptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subscriptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubscriptionDTO> partialUpdateSubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubscriptionDTO subscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Subscription partially : {}, {}", id, subscriptionDTO);
        if (subscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubscriptionDTO> result = subscriptionService.partialUpdate(subscriptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscriptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /subscriptions} : get all the Subscriptions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Subscriptions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Subscriptions");
        Page<SubscriptionDTO> page;
        if (eagerload) {
            page = subscriptionService.findAllWithEagerRelationships(pageable);
        } else {
            page = subscriptionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /subscriptions/:id} : get the "id" subscription.
     *
     * @param id the id of the subscriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subscriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Subscription : {}", id);
        Optional<SubscriptionDTO> subscriptionDTO = subscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subscriptionDTO);
    }

    /**
     * {@code DELETE  /subscriptions/:id} : delete the "id" subscription.
     *
     * @param id the id of the subscriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Subscription : {}", id);
        subscriptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<Boolean> isUserHasActiveSubscription(@PathVariable("userId") String userId) {
        LOG.debug("REST request to check if user has active subscription: {}", userId);
        return ResponseEntity.ok().body(subscriptionService.hasActiveSubscription(userId));
    }


}
