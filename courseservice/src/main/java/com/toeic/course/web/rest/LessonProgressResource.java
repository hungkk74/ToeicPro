package com.toeic.course.web.rest;

import com.toeic.course.repository.LessonProgressRepository;
import com.toeic.course.service.LessonProgressService;
import com.toeic.course.service.dto.LessonProgressDTO;
import com.toeic.course.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.toeic.course.domain.LessonProgress}.
 */
@RestController
@RequestMapping("/api/lesson-progresses")
public class LessonProgressResource {

    private static final Logger LOG = LoggerFactory.getLogger(LessonProgressResource.class);

    private static final String ENTITY_NAME = "courseserviceLessonProgress";

    @Value("${jhipster.clientApp.name:courseservice}")
    private String applicationName;

    private final LessonProgressService lessonProgressService;

    private final LessonProgressRepository lessonProgressRepository;

    public LessonProgressResource(LessonProgressService lessonProgressService, LessonProgressRepository lessonProgressRepository) {
        this.lessonProgressService = lessonProgressService;
        this.lessonProgressRepository = lessonProgressRepository;
    }

    /**
     * {@code POST  /lesson-progresses} : Create a new lessonProgress.
     *
     * @param lessonProgressDTO the lessonProgressDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lessonProgressDTO, or with status {@code 400 (Bad Request)} if the lessonProgress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LessonProgressDTO> createLessonProgress(@Valid @RequestBody LessonProgressDTO lessonProgressDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LessonProgress : {}", lessonProgressDTO);
        if (lessonProgressDTO.getId() != null) {
            throw new BadRequestAlertException("A new lessonProgress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        lessonProgressDTO = lessonProgressService.save(lessonProgressDTO);
        return ResponseEntity.created(new URI("/api/lesson-progresses/" + lessonProgressDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, lessonProgressDTO.getId().toString()))
            .body(lessonProgressDTO);
    }

    /**
     * {@code PUT  /lesson-progresses/:id} : Updates an existing lessonProgress.
     *
     * @param id the id of the lessonProgressDTO to save.
     * @param lessonProgressDTO the lessonProgressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonProgressDTO,
     * or with status {@code 400 (Bad Request)} if the lessonProgressDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lessonProgressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LessonProgressDTO> updateLessonProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LessonProgressDTO lessonProgressDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LessonProgress : {}, {}", id, lessonProgressDTO);
        if (lessonProgressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonProgressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        lessonProgressDTO = lessonProgressService.update(lessonProgressDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonProgressDTO.getId().toString()))
            .body(lessonProgressDTO);
    }

    /**
     * {@code PATCH  /lesson-progresses/:id} : Partial updates given fields of an existing lessonProgress, field will ignore if it is null
     *
     * @param id the id of the lessonProgressDTO to save.
     * @param lessonProgressDTO the lessonProgressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonProgressDTO,
     * or with status {@code 400 (Bad Request)} if the lessonProgressDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lessonProgressDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lessonProgressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LessonProgressDTO> partialUpdateLessonProgress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LessonProgressDTO lessonProgressDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LessonProgress partially : {}, {}", id, lessonProgressDTO);
        if (lessonProgressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonProgressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonProgressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LessonProgressDTO> result = lessonProgressService.partialUpdate(lessonProgressDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonProgressDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /lesson-progresses} : get all the Lesson Progresses.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Lesson Progresses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LessonProgressDTO>> getAllLessonProgresses(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of LessonProgresses");
        Page<LessonProgressDTO> page;
        if (eagerload) {
            page = lessonProgressService.findAllWithEagerRelationships(pageable);
        } else {
            page = lessonProgressService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /lesson-progresses/:id} : get the "id" lessonProgress.
     *
     * @param id the id of the lessonProgressDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lessonProgressDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LessonProgressDTO> getLessonProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LessonProgress : {}", id);
        Optional<LessonProgressDTO> lessonProgressDTO = lessonProgressService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lessonProgressDTO);
    }

    /**
     * {@code DELETE  /lesson-progresses/:id} : delete the "id" lessonProgress.
     *
     * @param id the id of the lessonProgressDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLessonProgress(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LessonProgress : {}", id);
        lessonProgressService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
