package org.example.tpj2eannonces.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import javax.security.auth.Subject;

import org.example.tpj2eannonces.api.security.jaas.RolePrincipal;
import org.junit.jupiter.api.Test;

class UserSecurityContextTest {

    @Test
    void shouldExtractUserPrincipalFromSubject() {
        Subject subject = new Subject();
        UUID userId = UUID.randomUUID();
        subject.getPrincipals().add(new UserPrincipal(userId, "testuser"));
        subject.getPrincipals().add(new RolePrincipal("ROLE_USER"));

        UserSecurityContext ctx = new UserSecurityContext(subject, false);

        assertThat(ctx.getUserPrincipal()).isNotNull();
        assertThat(ctx.getUserPrincipal().getName()).isEqualTo("testuser");
        assertThat(((UserPrincipal) ctx.getUserPrincipal()).getUserId()).isEqualTo(userId);
    }

    @Test
    void isUserInRole_shouldReturnTrueForMatchingRole() {
        Subject subject = new Subject();
        subject.getPrincipals().add(new UserPrincipal(UUID.randomUUID(), "testuser"));
        subject.getPrincipals().add(new RolePrincipal("ROLE_USER"));

        UserSecurityContext ctx = new UserSecurityContext(subject, false);

        assertThat(ctx.isUserInRole("ROLE_USER")).isTrue();
        assertThat(ctx.isUserInRole("ROLE_ADMIN")).isFalse();
    }

    @Test
    void isSecure_shouldReflectConstructorParam() {
        Subject subject = new Subject();
        subject.getPrincipals().add(new UserPrincipal(UUID.randomUUID(), "testuser"));

        assertThat(new UserSecurityContext(subject, true).isSecure()).isTrue();
        assertThat(new UserSecurityContext(subject, false).isSecure()).isFalse();
    }

    @Test
    void getAuthenticationScheme_shouldReturnBearer() {
        Subject subject = new Subject();
        subject.getPrincipals().add(new UserPrincipal(UUID.randomUUID(), "testuser"));

        UserSecurityContext ctx = new UserSecurityContext(subject, false);

        assertThat(ctx.getAuthenticationScheme()).isEqualTo("Bearer");
    }

    @Test
    void getSubject_shouldReturnOriginalSubject() {
        Subject subject = new Subject();
        subject.getPrincipals().add(new UserPrincipal(UUID.randomUUID(), "testuser"));

        UserSecurityContext ctx = new UserSecurityContext(subject, false);

        assertThat(ctx.getSubject()).isSameAs(subject);
    }

    @Test
    void constructor_shouldFailWithoutUserPrincipal() {
        Subject emptySubject = new Subject();

        assertThatThrownBy(() -> new UserSecurityContext(emptySubject, false))
                .isInstanceOf(IllegalStateException.class);
    }
}
