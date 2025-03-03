package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Loan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoanDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal requestedAmount;

    @NotNull
    private BigDecimal interestRate;

    @NotNull
    private Integer paymentTermMonths;

    private LocalDate applicationDate;

    @NotNull
    private Integer status;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getPaymentTermMonths() {
        return paymentTermMonths;
    }

    public void setPaymentTermMonths(Integer paymentTermMonths) {
        this.paymentTermMonths = paymentTermMonths;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoanDTO)) {
            return false;
        }

        LoanDTO loanDTO = (LoanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, loanDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoanDTO{" +
            "id=" + getId() +
            ", requestedAmount=" + getRequestedAmount() +
            ", interestRate=" + getInterestRate() +
            ", paymentTermMonths=" + getPaymentTermMonths() +
            ", applicationDate='" + getApplicationDate() + "'" +
            ", status=" + getStatus() +
            ", user=" + getUser() +
            "}";
    }
}
