package org.example.tpj2eannonces.api.security.jaas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.junit.jupiter.api.Test;

class CredentialsCallbackHandlerTest {

    @Test
    void handle_shouldSetNameAndPasswordCallbacks() throws Exception {
        CredentialsCallbackHandler handler = new CredentialsCallbackHandler("john", "secret");
        NameCallback nameCallback = new NameCallback("username");
        PasswordCallback passwordCallback = new PasswordCallback("password", false);

        handler.handle(new javax.security.auth.callback.Callback[]{nameCallback, passwordCallback});

        assertThat(nameCallback.getName()).isEqualTo("john");
        assertThat(passwordCallback.getPassword()).containsExactly('s', 'e', 'c', 'r', 'e', 't');
    }

    @Test
    void handle_shouldThrowForUnsupportedCallback() {
        CredentialsCallbackHandler handler = new CredentialsCallbackHandler("john", "secret");
        TextOutputCallback unsupported = new TextOutputCallback(TextOutputCallback.INFORMATION, "info");

        assertThatThrownBy(() -> handler.handle(new javax.security.auth.callback.Callback[]{unsupported}))
                .isInstanceOf(UnsupportedCallbackException.class);
    }

    @Test
    void constructor_shouldSupportNullPassword() throws Exception {
        CredentialsCallbackHandler handler = new CredentialsCallbackHandler("john", null);
        PasswordCallback passwordCallback = new PasswordCallback("password", false);
        handler.handle(new javax.security.auth.callback.Callback[]{passwordCallback});
        assertThat(passwordCallback.getPassword()).isEmpty();
    }
}
