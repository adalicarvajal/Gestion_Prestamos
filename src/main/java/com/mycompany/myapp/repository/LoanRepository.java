package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Loan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query("select loan from Loan loan where loan.user.login = ?#{authentication.name}")
    List<Loan> findByUserIsCurrentUser();
}
