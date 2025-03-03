package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AmortizationTestSamples.*;
import static com.mycompany.myapp.domain.LoanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AmortizationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Amortization.class);
        Amortization amortization1 = getAmortizationSample1();
        Amortization amortization2 = new Amortization();
        assertThat(amortization1).isNotEqualTo(amortization2);

        amortization2.setId(amortization1.getId());
        assertThat(amortization1).isEqualTo(amortization2);

        amortization2 = getAmortizationSample2();
        assertThat(amortization1).isNotEqualTo(amortization2);
    }

    @Test
    void loanTest() {
        Amortization amortization = getAmortizationRandomSampleGenerator();
        Loan loanBack = getLoanRandomSampleGenerator();

        amortization.setLoan(loanBack);
        assertThat(amortization.getLoan()).isEqualTo(loanBack);

        amortization.loan(null);
        assertThat(amortization.getLoan()).isNull();
    }
}
