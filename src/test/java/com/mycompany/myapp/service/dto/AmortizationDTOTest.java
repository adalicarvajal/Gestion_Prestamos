package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AmortizationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AmortizationDTO.class);
        AmortizationDTO amortizationDTO1 = new AmortizationDTO();
        amortizationDTO1.setId(1L);
        AmortizationDTO amortizationDTO2 = new AmortizationDTO();
        assertThat(amortizationDTO1).isNotEqualTo(amortizationDTO2);
        amortizationDTO2.setId(amortizationDTO1.getId());
        assertThat(amortizationDTO1).isEqualTo(amortizationDTO2);
        amortizationDTO2.setId(2L);
        assertThat(amortizationDTO1).isNotEqualTo(amortizationDTO2);
        amortizationDTO1.setId(null);
        assertThat(amortizationDTO1).isNotEqualTo(amortizationDTO2);
    }
}
