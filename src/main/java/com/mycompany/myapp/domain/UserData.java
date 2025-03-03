package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserData.
 */
@Entity
@Table(name = "user_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "salary", precision = 21, scale = 2, nullable = false)
    private BigDecimal salary;

    @NotNull
    @Column(name = "family_load", nullable = false)
    private Integer familyLoad;

    @NotNull
    @Column(name = "workplace", nullable = false)
    private String workplace;

    @Column(name = "housing_type")
    private String housingType;

    @Column(name = "rent_cost", precision = 21, scale = 2)
    private BigDecimal rentCost;

    @NotNull
    @Column(name = "years_of_employment", nullable = false)
    private Integer yearsOfEmployment;

    @NotNull
    @Column(name = "employment_status", nullable = false)
    private Integer employmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSalary() {
        return this.salary;
    }

    public UserData salary(BigDecimal salary) {
        this.setSalary(salary);
        return this;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Integer getFamilyLoad() {
        return this.familyLoad;
    }

    public UserData familyLoad(Integer familyLoad) {
        this.setFamilyLoad(familyLoad);
        return this;
    }

    public void setFamilyLoad(Integer familyLoad) {
        this.familyLoad = familyLoad;
    }

    public String getWorkplace() {
        return this.workplace;
    }

    public UserData workplace(String workplace) {
        this.setWorkplace(workplace);
        return this;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getHousingType() {
        return this.housingType;
    }

    public UserData housingType(String housingType) {
        this.setHousingType(housingType);
        return this;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    public BigDecimal getRentCost() {
        return this.rentCost;
    }

    public UserData rentCost(BigDecimal rentCost) {
        this.setRentCost(rentCost);
        return this;
    }

    public void setRentCost(BigDecimal rentCost) {
        this.rentCost = rentCost;
    }

    public Integer getYearsOfEmployment() {
        return this.yearsOfEmployment;
    }

    public UserData yearsOfEmployment(Integer yearsOfEmployment) {
        this.setYearsOfEmployment(yearsOfEmployment);
        return this;
    }

    public void setYearsOfEmployment(Integer yearsOfEmployment) {
        this.yearsOfEmployment = yearsOfEmployment;
    }

    public Integer getEmploymentStatus() {
        return this.employmentStatus;
    }

    public UserData employmentStatus(Integer employmentStatus) {
        this.setEmploymentStatus(employmentStatus);
        return this;
    }

    public void setEmploymentStatus(Integer employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserData user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserData)) {
            return false;
        }
        return getId() != null && getId().equals(((UserData) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserData{" +
            "id=" + getId() +
            ", salary=" + getSalary() +
            ", familyLoad=" + getFamilyLoad() +
            ", workplace='" + getWorkplace() + "'" +
            ", housingType='" + getHousingType() + "'" +
            ", rentCost=" + getRentCost() +
            ", yearsOfEmployment=" + getYearsOfEmployment() +
            ", employmentStatus=" + getEmploymentStatus() +
            "}";
    }
}
