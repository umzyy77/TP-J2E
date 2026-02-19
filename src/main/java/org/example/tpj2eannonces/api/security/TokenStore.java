package org.example.tpj2eannonces.api.security;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TokenStore {

    private static final long TOKEN_TTL_SECONDS = 3600;
    private static final ConcurrentHashMap<String, TokenInfo> TOKENS = new ConcurrentHashMap<>();

    public String generateToken(UUID userId, String username) {
        cleanup();
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(TOKEN_TTL_SECONDS);
        TOKENS.put(token, new TokenInfo(userId, username, expiresAt));
        return token;
    }

    public Optional<TokenInfo> validate(String token) {
        TokenInfo info = TOKENS.get(token);
        if (info == null) {
            return Optional.empty();
        }
        if (info.isExpired()) {
            TOKENS.remove(token);
            return Optional.empty();
        }
        return Optional.of(info);
    }

    public void revoke(String token) {
        TOKENS.remove(token);
    }

    public long getTokenTtlSeconds() {
        return TOKEN_TTL_SECONDS;
    }

    void clear() {
        TOKENS.clear();
    }

    private void cleanup() {
        TOKENS.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
