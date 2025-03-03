package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.AmortizationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Amortization}.
 */
public interface AmortizationService {
    /**
     * Save a amortization.
     *
     * @param amortizationDTO the entity to save.
     * @return the persisted entity.
     */
    AmortizationDTO save(AmortizationDTO amortizationDTO);

    /**
     * Get all the amortizations by userId.
     *
     * @param userId the ID of the user.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AmortizationDTO> findAllByUserId(Long userId, Pageable pageable);

/**
     * Get all the amortizations by loanId.
     *
     * @param loanId the ID of the loan.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AmortizationDTO> findAllByLoanId(Long loanId, Pageable pageable); // Añadir esta línea


    /**
     * Updates a amortization.
     *
     * @param amortizationDTO the entity to update.
     * @return the persisted entity.
     */
    AmortizationDTO update(AmortizationDTO amortizationDTO);

    /**
     * Partially updates a amortization.
     *
     * @param amortizationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AmortizationDTO> partialUpdate(AmortizationDTO amortizationDTO);

    /**
     * Get all the amortizations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AmortizationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" amortization.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AmortizationDTO> findOne(Long id);

    /**
     * Delete the "id" amortization.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Page<AmortizationDTO> findAllByCurrentUser(Pageable pageable);
}
