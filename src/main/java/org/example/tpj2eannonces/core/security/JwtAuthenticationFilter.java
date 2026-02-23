package org.example.tpj2eannonces.core.security;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private final JwtService jwtService;
    private final JwtClaimsService jwtClaimsService;

    public JwtAuthenticationFilter(JwtService jwtService, JwtClaimsService jwtClaimsService) {
        this.jwtService = jwtService;
        this.jwtClaimsService = jwtClaimsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtService.validateToken(token)) {
                String subject = jwtService.parseToken(token).getSubject();
                if (!isUuid(subject)) {
                    SecurityContextHolder.clearContext();
                    log.debug("JWT valide cryptographiquement mais claims invalides: authentification ignoree");
                } else {
                    List<String> tokenAuthorities = jwtClaimsService.extractAuthorities(token);

                    List<SimpleGrantedAuthority> authorities = tokenAuthorities.stream()
                            .filter(authority -> authority != null && !authority.isBlank())
                            .map(SimpleGrantedAuthority::new)
                            .distinct()
                            .toList();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(subject, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isUuid(String value) {
        return value != null && UUID_PATTERN.matcher(value).matches();
    }
}
