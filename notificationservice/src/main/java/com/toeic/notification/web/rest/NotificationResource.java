package com.toeic.notification.web.rest;

import com.toeic.notification.repository.NotificationRepository;
import com.toeic.notification.service.NotificationService;
import com.toeic.notification.service.dto.NotificationDTO;
import com.toeic.notification.web.rest.errors.BadRequestAlertException;
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
import com.toeic.notification.security.AuthoritiesConstants;
import com.toeic.notification.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * REST controller for managing {@link com.toeic.notification.domain.Notification}.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "notificationserviceNotification";

    @Value("${jhipster.clientApp.name:notificationservice}")
    private String applicationName;

    private final NotificationService notificationService;

    private final NotificationRepository notificationRepository;

    public NotificationResource(NotificationService notificationService, NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    /**
     * {@code POST  /notifications} : Create a new notification.
     *
     * @param notificationDTO the notificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationDTO, or with status {@code 400 (Bad Request)} if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Notification : {}", notificationDTO);
        if (notificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationDTO = notificationService.save(notificationDTO);
        return ResponseEntity.created(new URI("/api/notifications/" + notificationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(notificationDTO);
    }

    /**
     * {@code PUT  /notifications/:id} : Updates an existing notification.
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<NotificationDTO> updateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Notification : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notificationDTO = notificationService.update(notificationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(notificationDTO);
    }

    /**
     * {@code PATCH  /notifications/:id} : Partial updates given fields of an existing notification, field will ignore if it is null
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<NotificationDTO> partialUpdateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Notification partially : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationDTO> result = notificationService.partialUpdate(notificationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notifications} : get all the Notifications.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Notifications in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Notifications");
        Page<NotificationDTO> page = notificationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notification.
     *
     * @param id the id of the notificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Notification : {}", id);
        Optional<NotificationDTO> notificationDTO = notificationService.findOne(id);
        if (notificationDTO.isPresent()) {
            checkOwnershipOrAdmin(notificationDTO.get().getUserId());
        }
        return ResponseUtil.wrapOrNotFound(notificationDTO);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the notificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Notification : {}", id);
        notificationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /notifications/user/:userId} : get all notifications for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(
        @PathVariable("userId") String userId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Notifications for user : {}", userId);
        checkOwnershipOrAdmin(userId);
        Page<NotificationDTO> page = notificationService.findByUserId(userId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notifications/user/:userId/unread-count} : count unread notifications for a specific user.
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable("userId") String userId) {
        LOG.debug("REST request to count unread Notifications for user : {}", userId);
        checkOwnershipOrAdmin(userId);
        long count = notificationService.countUnreadByUserId(userId);
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code PATCH  /notifications/:id/mark-as-read} : mark notification as read.
     */
    @PatchMapping("/{id}/mark-as-read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable("id") Long id) {
        LOG.debug("REST request to mark Notification as read : {}", id);
        Optional<NotificationDTO> notificationDTO = notificationService.findOne(id);
        if (notificationDTO.isPresent()) {
            checkOwnershipOrAdmin(notificationDTO.get().getUserId());
        }
        return ResponseUtil.wrapOrNotFound(notificationService.markAsRead(id));
    }

    /**
     * {@code PATCH  /notifications/user/:userId/mark-all-as-read} : mark all notifications as read for a user.
     */
    @PatchMapping("/user/{userId}/mark-all-as-read")
    public ResponseEntity<Integer> markAllAsRead(@PathVariable("userId") String userId) {
        LOG.debug("REST request to mark all Notifications as read for user : {}", userId);
        checkOwnershipOrAdmin(userId);
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().body(count);
    }

    private void checkOwnershipOrAdmin(String targetUserId) {
        if (targetUserId == null) {
            return;
        }
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return;
        }
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) {
            throw new AccessDeniedException("Yêu cầu đăng nhập để truy cập thông báo!");
        }
        String login = currentUserLogin.get();
        if (targetUserId.equals(login)) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt) {
            String sub = jwt.getToken().getSubject();
            if (targetUserId.equals(sub)) {
                return;
            }
        }
        throw new AccessDeniedException("Bạn không có quyền truy cập thông báo của người dùng khác!");
    }
}
