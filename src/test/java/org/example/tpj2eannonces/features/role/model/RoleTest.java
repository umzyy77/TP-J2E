package org.example.tpj2eannonces.features.role.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void defaultConstructor_shouldWork() {
        Role role = new Role();
        assertThat(role.getName()).isNull();
        assertThat(role.getAuthorities()).isEmpty();
        assertThat(role.getUsers()).isEmpty();
    }

    @Test
    void parameterizedConstructor_shouldSetName() {
        Role role = new Role("ROLE_ADMIN");
        assertThat(role.getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void gettersAndSetters_shouldWork() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void setAuthorities_shouldCopySet() {
        Role role = new Role();
        Set<String> auths = new LinkedHashSet<>();
        auths.add("READ");
        auths.add("WRITE");
        role.setAuthorities(auths);

        assertThat(role.getAuthorities()).containsExactlyInAnyOrder("READ", "WRITE");
    }

    @Test
    void setAuthorities_null_shouldSetEmptySet() {
        Role role = new Role();
        role.setAuthorities(null);
        assertThat(role.getAuthorities()).isEmpty();
    }

    @Test
    void setUsers_null_shouldSetEmptySet() {
        Role role = new Role();
        role.setUsers(null);
        assertThat(role.getUsers()).isEmpty();
    }

    @Test
    void setUsers_shouldCopySet() {
        Role role = new Role();
        role.setUsers(new LinkedHashSet<>());
        assertThat(role.getUsers()).isEmpty();
    }

    @Test
    void equals_shouldUseBaseEntity() {
        Role r1 = new Role();
        r1.setId(1L);
        Role r2 = new Role();
        r2.setId(1L);

        assertThat(r1)
                .isEqualTo(r2)
                .hasSameHashCodeAs(r2);
    }

    @Test
    void equals_shouldBeFalse_whenDifferentId() {
        Role r1 = new Role();
        r1.setId(1L);
        Role r2 = new Role();
        r2.setId(2L);

        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void toString_shouldContainFields() {
        Role role = new Role("ROLE_USER");
        role.setId(1L);
        Set<String> auths = new LinkedHashSet<>();
        auths.add("READ");
        role.setAuthorities(auths);

        String result = role.toString();
        assertThat(result).contains("ROLE_USER", "READ");
    }
}
