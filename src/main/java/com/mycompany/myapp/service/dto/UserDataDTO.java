package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.UserData} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserDataDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal salary;

    @NotNull
    private Integer familyLoad;

    @NotNull
    private String workplace;

    private String housingType;

    private BigDecimal rentCost;

    @NotNull
    private Integer yearsOfEmployment;

    @NotNull
    private Integer employmentStatus;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Integer getFamilyLoad() {
        return familyLoad;
    }

    public void setFamilyLoad(Integer familyLoad) {
        this.familyLoad = familyLoad;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getHousingType() {
        return housingType;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    public BigDecimal getRentCost() {
        return rentCost;
    }

    public void setRentCost(BigDecimal rentCost) {
        this.rentCost = rentCost;
    }

    public Integer getYearsOfEmployment() {
        return yearsOfEmployment;
    }

    public void setYearsOfEmployment(Integer yearsOfEmployment) {
        this.yearsOfEmployment = yearsOfEmployment;
    }

    public Integer getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(Integer employmentStatus) {
        this.employmentStatus = employmentStatus;
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
        if (!(o instanceof UserDataDTO)) {
            return false;
        }

        UserDataDTO userDataDTO = (UserDataDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userDataDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDataDTO{" +
            "id=" + getId() +
            ", salary=" + getSalary() +
            ", familyLoad=" + getFamilyLoad() +
            ", workplace='" + getWorkplace() + "'" +
            ", housingType='" + getHousingType() + "'" +
            ", rentCost=" + getRentCost() +
            ", yearsOfEmployment=" + getYearsOfEmployment() +
            ", employmentStatus=" + getEmploymentStatus() +
            ", user=" + getUser() +
            "}";
    }
}
