package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Loan.
 */
@Entity
@Table(name = "loan")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Loan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "requested_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal requestedAmount;

    @NotNull
    @Column(name = "interest_rate", precision = 21, scale = 2, nullable = false)
    private BigDecimal interestRate;

    @NotNull
    @Column(name = "payment_term_months", nullable = false)
    private Integer paymentTermMonths;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Amortization> amortizations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Loan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRequestedAmount() {
        return this.requestedAmount;
    }

    public Loan requestedAmount(BigDecimal requestedAmount) {
        this.setRequestedAmount(requestedAmount);
        return this;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    public Loan interestRate(BigDecimal interestRate) {
        this.setInterestRate(interestRate);
        return this;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getPaymentTermMonths() {
        return this.paymentTermMonths;
    }

    public Loan paymentTermMonths(Integer paymentTermMonths) {
        this.setPaymentTermMonths(paymentTermMonths);
        return this;
    }

    public void setPaymentTermMonths(Integer paymentTermMonths) {
        this.paymentTermMonths = paymentTermMonths;
    }

    public LocalDate getApplicationDate() {
        return this.applicationDate;
    }

    public Loan applicationDate(LocalDate applicationDate) {
        this.setApplicationDate(applicationDate);
        return this;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Loan status(Integer status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Loan user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Loan)) {
            return false;
        }
        return getId() != null && getId().equals(((Loan) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Loan{" +
            "id=" + getId() +
            ", requestedAmount=" + getRequestedAmount() +
            ", interestRate=" + getInterestRate() +
            ", paymentTermMonths=" + getPaymentTermMonths() +
            ", applicationDate='" + getApplicationDate() + "'" +
            ", status=" + getStatus() +
            "}";
    }
}
