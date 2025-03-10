package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.UserDataTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserData.class);
        UserData userData1 = getUserDataSample1();
        UserData userData2 = new UserData();
        assertThat(userData1).isNotEqualTo(userData2);

        userData2.setId(userData1.getId());
        assertThat(userData1).isEqualTo(userData2);

        userData2 = getUserDataSample2();
        assertThat(userData1).isNotEqualTo(userData2);
    }
}
