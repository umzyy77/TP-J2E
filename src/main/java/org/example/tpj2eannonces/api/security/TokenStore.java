package org.example.tpj2eannonces.api.security;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TokenStore {

    private static final TokenStore INSTANCE = new TokenStore();
    private static final long TOKEN_TTL_SECONDS = 3600;

    private final ConcurrentHashMap<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    private TokenStore() {
    }

    public static TokenStore getInstance() {
        return INSTANCE;
    }

    public String generateToken(UUID userId, String username) {
        cleanup();
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(TOKEN_TTL_SECONDS);
        tokens.put(token, new TokenInfo(userId, username, expiresAt));
        return token;
    }

    public Optional<TokenInfo> validate(String token) {
        TokenInfo info = tokens.get(token);
        if (info == null) {
            return Optional.empty();
        }
        if (info.isExpired()) {
            tokens.remove(token);
            return Optional.empty();
        }
        return Optional.of(info);
    }

    public void revoke(String token) {
        tokens.remove(token);
    }

    public long getTokenTtlSeconds() {
        return TOKEN_TTL_SECONDS;
    }

    private void cleanup() {
        tokens.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
