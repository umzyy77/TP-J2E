package org.example.tpj2eannonces.api.security;

import java.time.Instant;
import java.util.UUID;

public record TokenInfo(UUID userId, String username, Instant expiresAt) {

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
