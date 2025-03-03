package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Loan;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.LoanDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Loan} and its DTO {@link LoanDTO}.
 */
@Mapper(componentModel = "spring")
public interface LoanMapper extends EntityMapper<LoanDTO, Loan> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    LoanDTO toDto(Loan s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
