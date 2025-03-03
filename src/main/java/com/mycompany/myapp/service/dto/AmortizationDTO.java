package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Amortization} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AmortizationDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer installmentNumber;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private BigDecimal remainingBalance;

    @NotNull
    private BigDecimal principal;

    private LocalDate paymentDate;

    @NotNull
    private BigDecimal paymentAmount;

    private BigDecimal penaltyInterest;

    private LoanDTO loan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPenaltyInterest() {
        return penaltyInterest;
    }

    public void setPenaltyInterest(BigDecimal penaltyInterest) {
        this.penaltyInterest = penaltyInterest;
    }

    public LoanDTO getLoan() {
        return loan;
    }

    public void setLoan(LoanDTO loan) {
        this.loan = loan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AmortizationDTO)) {
            return false;
        }

        AmortizationDTO amortizationDTO = (AmortizationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, amortizationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AmortizationDTO{" +
            "id=" + getId() +
            ", installmentNumber=" + getInstallmentNumber() +
            ", dueDate='" + getDueDate() + "'" +
            ", remainingBalance=" + getRemainingBalance() +
            ", principal=" + getPrincipal() +
            ", paymentDate='" + getPaymentDate() + "'" +
            ", paymentAmount=" + getPaymentAmount() +
            ", penaltyInterest=" + getPenaltyInterest() +
            ", loan=" + getLoan() +
            "}";
    }
}
