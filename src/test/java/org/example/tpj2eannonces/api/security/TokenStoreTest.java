package org.example.tpj2eannonces.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenStoreTest {

    private TokenStore tokenStore;

    @BeforeEach
    void setUp() {
        tokenStore = new TokenStore();
        tokenStore.clear();
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        UUID userId = UUID.randomUUID();
        String token = tokenStore.generateToken(userId, "testuser");

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void validate_shouldReturnTokenInfoForValidToken() {
        UUID userId = UUID.randomUUID();
        String token = tokenStore.generateToken(userId, "testuser");

        Optional<TokenInfo> result = tokenStore.validate(token);

        assertThat(result).isPresent();
        assertThat(result.get().userId()).isEqualTo(userId);
        assertThat(result.get().username()).isEqualTo("testuser");
        assertThat(result.get().isExpired()).isFalse();
    }

    @Test
    void validate_shouldReturnEmptyForInvalidToken() {
        Optional<TokenInfo> result = tokenStore.validate("invalid-token-" + UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void revoke_shouldInvalidateToken() {
        UUID userId = UUID.randomUUID();
        String token = tokenStore.generateToken(userId, "testuser");

        tokenStore.revoke(token);

        assertThat(tokenStore.validate(token)).isEmpty();
    }

    @Test
    void getTokenTtlSeconds_shouldReturn3600() {
        assertThat(tokenStore.getTokenTtlSeconds()).isEqualTo(3600);
    }

    @Test
    void instances_shouldShareBackingStorage() {
        TokenStore store1 = new TokenStore();
        TokenStore store2 = new TokenStore();
        String token = store1.generateToken(UUID.randomUUID(), "testuser");

        assertThat(store2.validate(token)).isPresent();
    }

    @Test
    @SuppressWarnings("unchecked")
    void validate_shouldRejectExpiredToken() throws Exception {
        String token = "expired-token";

        Field tokensField = TokenStore.class.getDeclaredField("TOKENS");
        tokensField.setAccessible(true);
        ConcurrentHashMap<String, TokenInfo> internalMap =
                (ConcurrentHashMap<String, TokenInfo>) tokensField.get(null);

        internalMap.put(token, new TokenInfo(UUID.randomUUID(), "expired-user", Instant.now().minusSeconds(10)));

        assertThat(tokenStore.validate(token)).isEmpty();
        assertThat(internalMap).doesNotContainKey(token);
    }
}
