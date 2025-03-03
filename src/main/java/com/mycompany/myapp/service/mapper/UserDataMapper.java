package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.UserData;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.dto.UserDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserData} and its DTO {@link UserDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserDataMapper extends EntityMapper<UserDataDTO, UserData> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    UserDataDTO toDto(UserData s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
