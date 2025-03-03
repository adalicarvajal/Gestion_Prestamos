package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.AmortizationRepository;
import com.mycompany.myapp.service.AmortizationService;
import com.mycompany.myapp.service.dto.AmortizationDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Amortization}.
 */
@RestController
@RequestMapping("/api/amortizations")
public class AmortizationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AmortizationResource.class);

    private static final String ENTITY_NAME = "amortization";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AmortizationService amortizationService;

    private final AmortizationRepository amortizationRepository;

    public AmortizationResource(AmortizationService amortizationService, AmortizationRepository amortizationRepository) {
        this.amortizationService = amortizationService;
        this.amortizationRepository = amortizationRepository;
    }

    /**
     * {@code POST  /amortizations} : Create a new amortization.
     *
     * @param amortizationDTO the amortizationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new amortizationDTO, or with status {@code 400 (Bad Request)} if the amortization has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AmortizationDTO> createAmortization(@Valid @RequestBody AmortizationDTO amortizationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Amortization : {}", amortizationDTO);
        if (amortizationDTO.getId() != null) {
            throw new BadRequestAlertException("A new amortization cannot already have an ID", ENTITY_NAME, "idexists");
        }
        amortizationDTO = amortizationService.save(amortizationDTO);
        return ResponseEntity.created(new URI("/api/amortizations/" + amortizationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, amortizationDTO.getId().toString()))
            .body(amortizationDTO);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<AmortizationDTO>> getAmortizationsByUserId(
        @PathVariable Long userId, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Amortizations for user : {}", userId);
        Page<AmortizationDTO> page = amortizationService.findAllByUserId(userId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/by-loan/{loanId}")
    public ResponseEntity<List<AmortizationDTO>> getAmortizationsByLoanId(
        @PathVariable Long loanId, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Amortizations for loan : {}", loanId);
        Page<AmortizationDTO> page = amortizationService.findAllByLoanId(loanId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<AmortizationDTO>> getAmortizationsByCurrentUser(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get amortizations for the current user");
        Page<AmortizationDTO> page = amortizationService.findAllByCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


    /**
     * {@code PUT  /amortizations/:id} : Updates an existing amortization.
     *
     * @param id the id of the amortizationDTO to save.
     * @param amortizationDTO the amortizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated amortizationDTO,
     * or with status {@code 400 (Bad Request)} if the amortizationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the amortizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AmortizationDTO> updateAmortization(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AmortizationDTO amortizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Amortization : {}, {}", id, amortizationDTO);
        if (amortizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, amortizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!amortizationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        amortizationDTO = amortizationService.update(amortizationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, amortizationDTO.getId().toString()))
            .body(amortizationDTO);
    }

    /**
     * {@code PATCH  /amortizations/:id} : Partial updates given fields of an existing amortization, field will ignore if it is null
     *
     * @param id the id of the amortizationDTO to save.
     * @param amortizationDTO the amortizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated amortizationDTO,
     * or with status {@code 400 (Bad Request)} if the amortizationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the amortizationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the amortizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AmortizationDTO> partialUpdateAmortization(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AmortizationDTO amortizationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Amortization partially : {}, {}", id, amortizationDTO);
        if (amortizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, amortizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!amortizationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AmortizationDTO> result = amortizationService.partialUpdate(amortizationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, amortizationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /amortizations} : get all the amortizations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of amortizations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AmortizationDTO>> getAllAmortizations(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Amortizations");
        Page<AmortizationDTO> page = amortizationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /amortizations/:id} : get the "id" amortization.
     *
     * @param id the id of the amortizationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the amortizationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AmortizationDTO> getAmortization(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Amortization : {}", id);
        Optional<AmortizationDTO> amortizationDTO = amortizationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(amortizationDTO);
    }

    /**
     * {@code DELETE  /amortizations/:id} : delete the "id" amortization.
     *
     * @param id the id of the amortizationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmortization(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Amortization : {}", id);
        amortizationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
