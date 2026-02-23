package org.example.tpj2eannonces.core.security;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.example.tpj2eannonces.core.security.exception.UnauthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextFacade {

    private static final String UNAUTHENTICATED_MESSAGE = "Utilisateur non authentifie";
    private static final String INVALID_PRINCIPAL_MESSAGE = "Principal d'authentification invalide";

    public UUID requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthenticatedException(UNAUTHENTICATED_MESSAGE);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UUID uuidPrincipal) {
            return uuidPrincipal;
        }

        if (principal instanceof String principalValue) {
            try {
                return UUID.fromString(principalValue);
            } catch (IllegalArgumentException _) {
                throw new UnauthenticatedException(INVALID_PRINCIPAL_MESSAGE);
            }
        }

        throw new UnauthenticatedException(INVALID_PRINCIPAL_MESSAGE);
    }

    public boolean hasAnyAuthority(String... expectedAuthorities) {
        if (expectedAuthorities == null || expectedAuthorities.length == 0) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        Set<String> normalizedExpectedAuthorities = Arrays.stream(expectedAuthorities)
                .filter(authority -> authority != null && !authority.isBlank())
                .collect(Collectors.toSet());

        if (normalizedExpectedAuthorities.isEmpty()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && !authority.isBlank())
                .anyMatch(normalizedExpectedAuthorities::contains);
    }
}
