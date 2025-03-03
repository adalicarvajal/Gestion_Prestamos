package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.LoanAsserts.*;
import static com.mycompany.myapp.domain.LoanTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoanMapperTest {

    private LoanMapper loanMapper;

    @BeforeEach
    void setUp() {
        loanMapper = new LoanMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLoanSample1();
        var actual = loanMapper.toEntity(loanMapper.toDto(expected));
        assertLoanAllPropertiesEquals(expected, actual);
    }
}
