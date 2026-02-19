package org.example.tpj2eannonces.api.security.jaas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RolePrincipalTest {

    @Test
    void getName_shouldReturnRole() {
        RolePrincipal rp = new RolePrincipal("ROLE_USER");
        assertThat(rp.getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void constructor_shouldRejectNull() {
        assertThatThrownBy(() -> new RolePrincipal(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void equals_shouldWorkCorrectly() {
        RolePrincipal rp1 = new RolePrincipal("ROLE_USER");
        RolePrincipal rp2 = new RolePrincipal("ROLE_USER");
        RolePrincipal rp3 = new RolePrincipal("ROLE_ADMIN");

        assertThat(rp1).isEqualTo(rp2)
                .isNotEqualTo(rp3)
                .isNotEqualTo(null)
                .isNotEqualTo("ROLE_USER");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        RolePrincipal rp1 = new RolePrincipal("ROLE_USER");
        RolePrincipal rp2 = new RolePrincipal("ROLE_USER");

        assertThat(rp1).hasSameHashCodeAs(rp2);
    }

    @Test
    void toString_shouldContainRole() {
        RolePrincipal rp = new RolePrincipal("ROLE_USER");
        assertThat(rp.toString()).contains("ROLE_USER");
    }
}
