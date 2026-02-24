package org.example.tpj2eannonces.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

@Service
public class JwtClaimsService {

    private final JwtService jwtService;

    public JwtClaimsService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(jwtService.parseToken(token).getSubject());
    }

    public String extractUsername(String token) {
        return jwtService.parseToken(token).get("username", String.class);
    }

    public List<String> extractRoles(String token) {
        Claims claims = jwtService.parseToken(token);
        List<String> roles = extractStringListClaim(claims, "roles");
        if (!roles.isEmpty()) {
            return roles;
        }

        String role = claims.get("role", String.class);
        if (role == null || role.isBlank()) {
            return List.of();
        }
        return List.of(role);
    }

    public List<String> extractAuthorities(String token) {
        Claims claims = jwtService.parseToken(token);
        List<String> authorities = extractStringListClaim(claims, "authorities");
        if (!authorities.isEmpty()) {
            return authorities;
        }
        return extractRoles(token);
    }

    public String extractRole(String token) {
        List<String> roles = extractRoles(token);
        return roles.isEmpty() ? null : roles.getFirst();
    }

    private List<String> extractStringListClaim(Claims claims, String claimName) {
        Object claim = claims.get(claimName);
        if (!(claim instanceof Collection<?> claimValues)) {
            return List.of();
        }

        List<String> values = new ArrayList<>();
        for (Object value : claimValues) {
            if (value == null) {
                continue;
            }
            String strValue = value.toString().trim();
            if (!strValue.isEmpty()) {
                values.add(strValue);
            }
        }
        return values;
    }
}
