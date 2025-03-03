package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Amortization;
import com.mycompany.myapp.domain.Loan;
import com.mycompany.myapp.service.dto.AmortizationDTO;
import com.mycompany.myapp.service.dto.LoanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Amortization} and its DTO {@link AmortizationDTO}.
 */
@Mapper(componentModel = "spring")
public interface AmortizationMapper extends EntityMapper<AmortizationDTO, Amortization> {
    @Mapping(target = "loan", source = "loan", qualifiedByName = "loanId")
    AmortizationDTO toDto(Amortization s);

    @Named("loanId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LoanDTO toDtoLoanId(Loan loan);
}
