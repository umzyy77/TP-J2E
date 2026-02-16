package org.example.tpj2eannonces.api.security.jaas;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;

public class RolePrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String role;

    public RolePrincipal(String role) {
        Objects.requireNonNull(role, "Le role ne peut pas etre null");
        this.role = role;
    }

    @Override
    public String getName() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolePrincipal that)) return false;
        return role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return role.hashCode();
    }

    @Override
    public String toString() {
        return "RolePrincipal[" + role + "]";
    }
}
