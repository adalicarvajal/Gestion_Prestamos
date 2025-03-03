package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.AmortizationAsserts.*;
import static com.mycompany.myapp.domain.AmortizationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AmortizationMapperTest {

    private AmortizationMapper amortizationMapper;

    @BeforeEach
    void setUp() {
        amortizationMapper = new AmortizationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAmortizationSample1();
        var actual = amortizationMapper.toEntity(amortizationMapper.toDto(expected));
        assertAmortizationAllPropertiesEquals(expected, actual);
    }
}
