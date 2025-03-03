package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Amortization;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Amortization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AmortizationRepository extends JpaRepository<Amortization, Long> {
    Page<Amortization> findByLoanUserId(Long userId, Pageable pageable);
    Page<Amortization> findByLoanId(Long loanId, Pageable pageable); // Añadir esta línea
    Page<Amortization> findAllByLoan_User_Login(String login, Pageable pageable);
}
