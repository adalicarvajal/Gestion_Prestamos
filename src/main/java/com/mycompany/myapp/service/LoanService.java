package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.LoanDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Loan}.
 */
public interface LoanService {
    /**
     * Save a loan.
     *
     * @param loanDTO the entity to save.
     * @return the persisted entity.
     */
    LoanDTO save(LoanDTO loanDTO);

    /**
     * Updates a loan.
     *
     * @param loanDTO the entity to update.
     * @return the persisted entity.
     */
    LoanDTO update(LoanDTO loanDTO);

    /**
     * Partially updates a loan.
     *
     * @param loanDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LoanDTO> partialUpdate(LoanDTO loanDTO);

    /**
     * Get all the loans.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LoanDTO> findAll(Pageable pageable);

    /**
     * Get the "id" loan.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LoanDTO> findOne(Long id);

    /**
     * Delete the "id" loan.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
