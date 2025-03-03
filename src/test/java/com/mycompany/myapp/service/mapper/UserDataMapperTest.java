package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.UserDataAsserts.*;
import static com.mycompany.myapp.domain.UserDataTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDataMapperTest {

    private UserDataMapper userDataMapper;

    @BeforeEach
    void setUp() {
        userDataMapper = new UserDataMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserDataSample1();
        var actual = userDataMapper.toEntity(userDataMapper.toDto(expected));
        assertUserDataAllPropertiesEquals(expected, actual);
    }
}
