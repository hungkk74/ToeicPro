package com.toeic.exam.web.rest;

import com.toeic.exam.repository.UserAnswerRepository;
import com.toeic.exam.service.UserAnswerService;
import com.toeic.exam.service.dto.UserAnswerDTO;
import com.toeic.exam.web.rest.errors.BadRequestAlertException;
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
import com.toeic.exam.security.AuthoritiesConstants;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * REST controller for managing {@link com.toeic.exam.domain.UserAnswer}.
 */
@RestController
@RequestMapping("/api/user-answers")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class UserAnswerResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserAnswerResource.class);

    private static final String ENTITY_NAME = "examserviceUserAnswer";

    @Value("${jhipster.clientApp.name:examservice}")
    private String applicationName;

    private final UserAnswerService userAnswerService;

    private final UserAnswerRepository userAnswerRepository;

    public UserAnswerResource(UserAnswerService userAnswerService, UserAnswerRepository userAnswerRepository) {
        this.userAnswerService = userAnswerService;
        this.userAnswerRepository = userAnswerRepository;
    }

    /**
     * {@code POST  /user-answers} : Create a new userAnswer.
     *
     * @param userAnswerDTO the userAnswerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userAnswerDTO, or with status {@code 400 (Bad Request)} if the userAnswer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserAnswerDTO> createUserAnswer(@Valid @RequestBody UserAnswerDTO userAnswerDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserAnswer : {}", userAnswerDTO);
        if (userAnswerDTO.getId() != null) {
            throw new BadRequestAlertException("A new userAnswer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userAnswerDTO = userAnswerService.save(userAnswerDTO);
        return ResponseEntity.created(new URI("/api/user-answers/" + userAnswerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userAnswerDTO.getId().toString()))
            .body(userAnswerDTO);
    }

    /**
     * {@code PUT  /user-answers/:id} : Updates an existing userAnswer.
     *
     * @param id the id of the userAnswerDTO to save.
     * @param userAnswerDTO the userAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the userAnswerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserAnswerDTO> updateUserAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserAnswerDTO userAnswerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserAnswer : {}, {}", id, userAnswerDTO);
        if (userAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userAnswerDTO = userAnswerService.update(userAnswerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAnswerDTO.getId().toString()))
            .body(userAnswerDTO);
    }

    /**
     * {@code PATCH  /user-answers/:id} : Partial updates given fields of an existing userAnswer, field will ignore if it is null
     *
     * @param id the id of the userAnswerDTO to save.
     * @param userAnswerDTO the userAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the userAnswerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userAnswerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserAnswerDTO> partialUpdateUserAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserAnswerDTO userAnswerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserAnswer partially : {}, {}", id, userAnswerDTO);
        if (userAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserAnswerDTO> result = userAnswerService.partialUpdate(userAnswerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAnswerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-answers} : get all the User Answers.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of User Answers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserAnswerDTO>> getAllUserAnswers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of UserAnswers");
        Page<UserAnswerDTO> page;
        if (eagerload) {
            page = userAnswerService.findAllWithEagerRelationships(pageable);
        } else {
            page = userAnswerService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-answers/:id} : get the "id" userAnswer.
     *
     * @param id the id of the userAnswerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userAnswerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAnswerDTO> getUserAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserAnswer : {}", id);
        Optional<UserAnswerDTO> userAnswerDTO = userAnswerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userAnswerDTO);
    }

    /**
     * {@code DELETE  /user-answers/:id} : delete the "id" userAnswer.
     *
     * @param id the id of the userAnswerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserAnswer : {}", id);
        userAnswerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
