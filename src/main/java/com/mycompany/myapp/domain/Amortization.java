package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Amortization.
 */
@Entity
@Table(name = "amortization")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Amortization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Column(name = "remaining_balance", precision = 21, scale = 2, nullable = false)
    private BigDecimal remainingBalance;

    @NotNull
    @Column(name = "principal", precision = 21, scale = 2, nullable = false)
    private BigDecimal principal;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @NotNull
    @Column(name = "payment_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal paymentAmount;

    @Column(name = "penalty_interest", precision = 21, scale = 2)
    private BigDecimal penaltyInterest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Loan loan;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Amortization id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInstallmentNumber() {
        return this.installmentNumber;
    }

    public Amortization installmentNumber(Integer installmentNumber) {
        this.setInstallmentNumber(installmentNumber);
        return this;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public Amortization dueDate(LocalDate dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getRemainingBalance() {
        return this.remainingBalance;
    }

    public Amortization remainingBalance(BigDecimal remainingBalance) {
        this.setRemainingBalance(remainingBalance);
        return this;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public BigDecimal getPrincipal() {
        return this.principal;
    }

    public Amortization principal(BigDecimal principal) {
        this.setPrincipal(principal);
        return this;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public LocalDate getPaymentDate() {
        return this.paymentDate;
    }

    public Amortization paymentDate(LocalDate paymentDate) {
        this.setPaymentDate(paymentDate);
        return this;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }

    public Amortization paymentAmount(BigDecimal paymentAmount) {
        this.setPaymentAmount(paymentAmount);
        return this;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPenaltyInterest() {
        return this.penaltyInterest;
    }

    public Amortization penaltyInterest(BigDecimal penaltyInterest) {
        this.setPenaltyInterest(penaltyInterest);
        return this;
    }

    public void setPenaltyInterest(BigDecimal penaltyInterest) {
        this.penaltyInterest = penaltyInterest;
    }

    public Loan getLoan() {
        return this.loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Amortization loan(Loan loan) {
        this.setLoan(loan);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Amortization)) {
            return false;
        }
        return getId() != null && getId().equals(((Amortization) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Amortization{" +
            "id=" + getId() +
            ", installmentNumber=" + getInstallmentNumber() +
            ", dueDate='" + getDueDate() + "'" +
            ", remainingBalance=" + getRemainingBalance() +
            ", principal=" + getPrincipal() +
            ", paymentDate='" + getPaymentDate() + "'" +
            ", paymentAmount=" + getPaymentAmount() +
            ", penaltyInterest=" + getPenaltyInterest() +
            "}";
    }
}
