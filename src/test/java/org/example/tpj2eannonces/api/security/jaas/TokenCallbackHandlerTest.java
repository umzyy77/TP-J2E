package org.example.tpj2eannonces.api.security.jaas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.junit.jupiter.api.Test;

class TokenCallbackHandlerTest {

    @Test
    void handle_shouldSetTokenOnNameCallback() throws Exception {
        TokenCallbackHandler handler = new TokenCallbackHandler("token-123");
        NameCallback callback = new NameCallback("token");

        handler.handle(new javax.security.auth.callback.Callback[]{callback});

        assertThat(callback.getName()).isEqualTo("token-123");
    }

    @Test
    void handle_shouldThrowForUnsupportedCallback() {
        TokenCallbackHandler handler = new TokenCallbackHandler("token-123");
        PasswordCallback unsupported = new PasswordCallback("password", false);

        assertThatThrownBy(() -> handler.handle(new javax.security.auth.callback.Callback[]{unsupported}))
                .isInstanceOf(UnsupportedCallbackException.class);
    }
}
