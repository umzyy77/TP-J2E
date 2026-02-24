package org.example.tpj2eannonces.features.user.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.example.tpj2eannonces.features.role.model.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void defaultConstructor_shouldWork() {
        User user = new User();
        assertThat(user.getUsername()).isNull();
    }

    @Test
    void parameterizedConstructor_shouldSetFields() {
        User user = new User("alice", "alice@test.com", "secret");

        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getEmail()).isEqualTo("alice@test.com");
        assertThat(user.getPassword()).isEqualTo("secret");
    }

    @Test
    void gettersAndSetters_shouldWork() {
        User user = new User();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Role role = new Role("ROLE_USER");

        user.setId(id);
        user.setUsername("bob");
        user.setEmail("bob@test.com");
        user.setPassword("pass");
        user.setRole(role);
        user.setCreatedAt(now);
        user.setAnnonces(new ArrayList<>());

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getUsername()).isEqualTo("bob");
        assertThat(user.getEmail()).isEqualTo("bob@test.com");
        assertThat(user.getPassword()).isEqualTo("pass");
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getAnnonces()).isEmpty();
    }

    @Test
    void onCreate_shouldSetCreatedAt() {
        User user = new User();
        user.onCreate();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void resolveRoleNames_shouldReturnRoleName() {
        Role role = new Role("ROLE_USER");
        User user = new User();
        user.setRole(role);

        assertThat(user.resolveRoleNames()).containsExactly("ROLE_USER");
    }

    @Test
    void resolveRoleNames_shouldReturnEmpty_whenNoRole() {
        User user = new User();
        assertThat(user.resolveRoleNames()).isEmpty();
    }

    @Test
    void resolveRoleNames_shouldReturnEmpty_whenRoleNameIsBlank() {
        Role role = new Role();
        role.setName("  ");
        User user = new User();
        user.setRole(role);

        assertThat(user.resolveRoleNames()).isEmpty();
    }

    @Test
    void resolveRoleNames_shouldReturnEmpty_whenRoleNameIsNull() {
        Role role = new Role();
        role.setName(null);
        User user = new User();
        user.setRole(role);

        assertThat(user.resolveRoleNames()).isEmpty();
    }

    @Test
    void resolveAuthorities_shouldReturnRoleAndAuthorities() {
        Role role = new Role("ROLE_USER");
        Set<String> authorities = new LinkedHashSet<>();
        authorities.add("ANNONCE_READ");
        authorities.add("ANNONCE_WRITE");
        role.setAuthorities(authorities);

        User user = new User();
        user.setRole(role);

        Set<String> result = user.resolveAuthorities();
        assertThat(result).contains("ROLE_USER", "ANNONCE_READ", "ANNONCE_WRITE");
    }

    @Test
    void resolveAuthorities_shouldReturnEmpty_whenNoRole() {
        User user = new User();
        assertThat(user.resolveAuthorities()).isEmpty();
    }

    @Test
    void resolveAuthorities_shouldSkipBlankAuthorities() {
        Role role = new Role("ROLE_USER");
        Set<String> authorities = new LinkedHashSet<>();
        authorities.add("ANNONCE_READ");
        authorities.add("  ");
        authorities.add(null);
        role.setAuthorities(authorities);

        User user = new User();
        user.setRole(role);

        Set<String> result = user.resolveAuthorities();
        assertThat(result).containsExactly("ROLE_USER", "ANNONCE_READ");
    }

    @Test
    void resolveAuthorities_shouldReturnEmpty_whenRoleNameBlankAndNoAuthorities() {
        Role role = new Role();
        role.setName("  ");
        User user = new User();
        user.setRole(role);

        assertThat(user.resolveAuthorities()).isEmpty();
    }

    @Test
    void resolveAuthorities_shouldReturnEmpty_whenRoleNameNullAndNoAuthorities() {
        Role role = new Role();
        role.setName(null);
        User user = new User();
        user.setRole(role);

        assertThat(user.resolveAuthorities()).isEmpty();
    }

    @Test
    void resolveAuthorities_shouldReturnOnlyAuthorities_whenRoleNameNull() {
        Role role = new Role();
        role.setName(null);
        Set<String> authorities = new LinkedHashSet<>();
        authorities.add("ANNONCE_READ");
        role.setAuthorities(authorities);

        User user = new User();
        user.setRole(role);

        Set<String> result = user.resolveAuthorities();
        assertThat(result).containsExactly("ANNONCE_READ");
    }

    @Test
    void equals_shouldUseBaseEntity() {
        User u1 = new User();
        UUID id = UUID.randomUUID();
        u1.setId(id);
        User u2 = new User();
        u2.setId(id);

        assertThat(u1)
                .isEqualTo(u2)
                .hasSameHashCodeAs(u2);
    }

    @Test
    void equals_shouldBeFalse_whenDifferentId() {
        User u1 = new User();
        u1.setId(UUID.randomUUID());
        User u2 = new User();
        u2.setId(UUID.randomUUID());

        assertThat(u1).isNotEqualTo(u2);
    }

    @Test
    void toString_shouldContainFields() {
        User user = new User("alice", "alice@test.com", "secret");
        UUID id = UUID.randomUUID();
        user.setId(id);

        String result = user.toString();
        assertThat(result).contains("alice", "alice@test.com");
    }

    @Test
    void toString_shouldHandleNullRole() {
        User user = new User("alice", "alice@test.com", "secret");
        String result = user.toString();
        assertThat(result).contains("role=null");
    }

    @Test
    void toString_shouldContainRoleName_whenRoleIsSet() {
        User user = new User("alice", "alice@test.com", "secret");
        user.setId(UUID.randomUUID());
        Role role = new Role("ROLE_ADMIN");
        user.setRole(role);

        String result = user.toString();
        assertThat(result).contains("role=ROLE_ADMIN");
    }
}
