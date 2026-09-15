package com.toeic.exam.web.rest;

import com.toeic.exam.repository.ExamAttemptRepository;
import com.toeic.exam.service.ExamAttemptService;
import com.toeic.exam.service.dto.ExamAttemptDTO;
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

/**
 * REST controller for managing {@link com.toeic.exam.domain.ExamAttempt}.
 */
@RestController
@RequestMapping("/api/exam-attempts")
public class ExamAttemptResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExamAttemptResource.class);

    private static final String ENTITY_NAME = "examserviceExamAttempt";

    @Value("${jhipster.clientApp.name:examservice}")
    private String applicationName;

    private final ExamAttemptService examAttemptService;

    private final ExamAttemptRepository examAttemptRepository;

    public ExamAttemptResource(ExamAttemptService examAttemptService, ExamAttemptRepository examAttemptRepository) {
        this.examAttemptService = examAttemptService;
        this.examAttemptRepository = examAttemptRepository;
    }

    /**
     * {@code POST  /exam-attempts} : Create a new examAttempt.
     *
     * @param examAttemptDTO the examAttemptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new examAttemptDTO, or with status {@code 400 (Bad Request)} if the examAttempt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ExamAttemptDTO> createExamAttempt(@Valid @RequestBody ExamAttemptDTO examAttemptDTO) throws URISyntaxException {
        LOG.debug("REST request to save ExamAttempt : {}", examAttemptDTO);
        if (examAttemptDTO.getId() != null) {
            throw new BadRequestAlertException("A new examAttempt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        examAttemptDTO = examAttemptService.save(examAttemptDTO);
        return ResponseEntity.created(new URI("/api/exam-attempts/" + examAttemptDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, examAttemptDTO.getId().toString()))
            .body(examAttemptDTO);
    }

    /**
     * {@code PUT  /exam-attempts/:id} : Updates an existing examAttempt.
     *
     * @param id the id of the examAttemptDTO to save.
     * @param examAttemptDTO the examAttemptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examAttemptDTO,
     * or with status {@code 400 (Bad Request)} if the examAttemptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the examAttemptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamAttemptDTO> updateExamAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExamAttemptDTO examAttemptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ExamAttempt : {}, {}", id, examAttemptDTO);
        if (examAttemptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examAttemptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examAttemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        examAttemptDTO = examAttemptService.update(examAttemptDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examAttemptDTO.getId().toString()))
            .body(examAttemptDTO);
    }

    /**
     * {@code PATCH  /exam-attempts/:id} : Partial updates given fields of an existing examAttempt, field will ignore if it is null
     *
     * @param id the id of the examAttemptDTO to save.
     * @param examAttemptDTO the examAttemptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examAttemptDTO,
     * or with status {@code 400 (Bad Request)} if the examAttemptDTO is not valid,
     * or with status {@code 404 (Not Found)} if the examAttemptDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the examAttemptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExamAttemptDTO> partialUpdateExamAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExamAttemptDTO examAttemptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ExamAttempt partially : {}, {}", id, examAttemptDTO);
        if (examAttemptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examAttemptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examAttemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExamAttemptDTO> result = examAttemptService.partialUpdate(examAttemptDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examAttemptDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /exam-attempts} : get all the Exam Attempts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Exam Attempts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ExamAttemptDTO>> getAllExamAttempts(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ExamAttempts");
        Page<ExamAttemptDTO> page;
        if (eagerload) {
            page = examAttemptService.findAllWithEagerRelationships(pageable);
        } else {
            page = examAttemptService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /exam-attempts/:id} : get the "id" examAttempt.
     *
     * @param id the id of the examAttemptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the examAttemptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamAttemptDTO> getExamAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ExamAttempt : {}", id);
        Optional<ExamAttemptDTO> examAttemptDTO = examAttemptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(examAttemptDTO);
    }

    /**
     * {@code DELETE  /exam-attempts/:id} : delete the "id" examAttempt.
     *
     * @param id the id of the examAttemptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ExamAttempt : {}", id);
        examAttemptService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
