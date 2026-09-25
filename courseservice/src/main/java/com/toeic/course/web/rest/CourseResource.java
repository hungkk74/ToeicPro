package com.toeic.course.web.rest;

import com.toeic.course.repository.CourseRepository;
import com.toeic.course.service.CourseService;
import com.toeic.course.service.dto.CourseDTO;
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
 * REST controller for managing {@link com.toeic.course.domain.Course}.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

    private static final String ENTITY_NAME = "courseserviceCourse";

    @Value("${jhipster.clientApp.name:courseservice}")
    private String applicationName;

    private final CourseService courseService;

    private final CourseRepository courseRepository;

    public CourseResource(CourseService courseService, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
    }

    /**
     * {@code POST  /courses} : Create a new course.
     *
     * @param courseDTO the courseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseDTO, or with status {@code 400 (Bad Request)} if the course has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) throws URISyntaxException {
        LOG.debug("REST request to save Course : {}", courseDTO);
        if (courseDTO.getId() != null) {
            throw new BadRequestAlertException("A new course cannot already have an ID", ENTITY_NAME, "idexists");
        }
        courseDTO = courseService.save(courseDTO);
        return ResponseEntity.created(new URI("/api/courses/" + courseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, courseDTO.getId().toString()))
            .body(courseDTO);
    }

    /**
     * {@code PUT  /courses/:id} : Updates an existing course.
     *
     * @param id the id of the courseDTO to save.
     * @param courseDTO the courseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseDTO,
     * or with status {@code 400 (Bad Request)} if the courseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<CourseDTO> updateCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseDTO courseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Course : {}, {}", id, courseDTO);
        if (courseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        courseDTO = courseService.update(courseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseDTO.getId().toString()))
            .body(courseDTO);
    }

    /**
     * {@code PATCH  /courses/:id} : Partial updates given fields of an existing course, field will ignore if it is null
     *
     * @param id the id of the courseDTO to save.
     * @param courseDTO the courseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseDTO,
     * or with status {@code 400 (Bad Request)} if the courseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the courseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<CourseDTO> partialUpdateCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseDTO courseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Course partially : {}, {}", id, courseDTO);
        if (courseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseDTO> result = courseService.partialUpdate(courseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /courses} : get all the Courses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Courses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CourseDTO>> getAllCourses(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Courses");
        Page<CourseDTO> page = courseService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /courses/:id} : get the "id" course.
     *
     * @param id the id of the courseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Course : {}", id);
        Optional<CourseDTO> courseDTO = courseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseDTO);
    }

    /**
     * {@code DELETE  /courses/:id} : delete the "id" course.
     *
     * @param id the id of the courseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Course : {}", id);
        courseService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
