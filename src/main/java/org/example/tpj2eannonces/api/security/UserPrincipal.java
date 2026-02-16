package org.example.tpj2eannonces.api.security;

import java.security.Principal;
import java.util.UUID;

public class UserPrincipal implements Principal {

    private final UUID userId;
    private final String username;

    public UserPrincipal(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }

    public UUID getUserId() {
        return userId;
    }
}
