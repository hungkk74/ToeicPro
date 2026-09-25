package com.toeic.course.web.rest;

import com.toeic.course.repository.LessonRepository;
import com.toeic.course.service.LessonService;
import com.toeic.course.service.dto.LessonDTO;
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
import com.toeic.course.security.AuthoritiesConstants;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * REST controller for managing {@link com.toeic.course.domain.Lesson}.
 */
@RestController
@RequestMapping("/api/lessons")
public class LessonResource {

    private static final Logger LOG = LoggerFactory.getLogger(LessonResource.class);

    private static final String ENTITY_NAME = "courseserviceLesson";

    @Value("${jhipster.clientApp.name:courseservice}")
    private String applicationName;

    private final LessonService lessonService;

    private final LessonRepository lessonRepository;

    public LessonResource(LessonService lessonService, LessonRepository lessonRepository) {
        this.lessonService = lessonService;
        this.lessonRepository = lessonRepository;
    }

    /**
     * {@code POST  /lessons} : Create a new lesson.
     *
     * @param lessonDTO the lessonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lessonDTO, or with status {@code 400 (Bad Request)} if the lesson has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<LessonDTO> createLesson(@Valid @RequestBody LessonDTO lessonDTO) throws URISyntaxException {
        LOG.debug("REST request to save Lesson : {}", lessonDTO);
        if (lessonDTO.getId() != null) {
            throw new BadRequestAlertException("A new lesson cannot already have an ID", ENTITY_NAME, "idexists");
        }
        lessonDTO = lessonService.save(lessonDTO);
        return ResponseEntity.created(new URI("/api/lessons/" + lessonDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, lessonDTO.getId().toString()))
            .body(lessonDTO);
    }

    /**
     * {@code PUT  /lessons/:id} : Updates an existing lesson.
     *
     * @param id the id of the lessonDTO to save.
     * @param lessonDTO the lessonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonDTO,
     * or with status {@code 400 (Bad Request)} if the lessonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lessonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<LessonDTO> updateLesson(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LessonDTO lessonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Lesson : {}, {}", id, lessonDTO);
        if (lessonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        lessonDTO = lessonService.update(lessonDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonDTO.getId().toString()))
            .body(lessonDTO);
    }

    /**
     * {@code PATCH  /lessons/:id} : Partial updates given fields of an existing lesson, field will ignore if it is null
     *
     * @param id the id of the lessonDTO to save.
     * @param lessonDTO the lessonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonDTO,
     * or with status {@code 400 (Bad Request)} if the lessonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lessonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lessonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<LessonDTO> partialUpdateLesson(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LessonDTO lessonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Lesson partially : {}, {}", id, lessonDTO);
        if (lessonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LessonDTO> result = lessonService.partialUpdate(lessonDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /lessons} : get all the Lessons.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Lessons in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LessonDTO>> getAllLessons(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Lessons");
        Page<LessonDTO> page;
        if (eagerload) {
            page = lessonService.findAllWithEagerRelationships(pageable);
        } else {
            page = lessonService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /lessons/:id} : get the "id" lesson.
     *
     * @param id the id of the lessonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lessonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LessonDTO> getLesson(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Lesson : {}", id);
        Optional<LessonDTO> lessonDTO = lessonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lessonDTO);
    }

    /**
     * {@code DELETE  /lessons/:id} : delete the "id" lesson.
     *
     * @param id the id of the lessonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteLesson(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Lesson : {}", id);
        lessonService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
