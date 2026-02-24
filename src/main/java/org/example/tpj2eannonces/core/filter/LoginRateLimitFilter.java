package org.example.tpj2eannonces.core.filter;

import java.io.IOException;

import org.example.tpj2eannonces.core.ratelimit.FixedWindowRateLimiter;
import org.example.tpj2eannonces.core.ratelimit.FixedWindowRateLimiter.RateLimitDecision;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginRateLimitFilter.class);

    private final boolean enabled;
    private final String loginPath;
    private final FixedWindowRateLimiter rateLimiter;

    public LoginRateLimitFilter(@Value("${rate-limit.login.enabled:true}") boolean enabled,
                                @Value("${rate-limit.login.path}") String loginPath,
                                @Value("${rate-limit.login.max-requests:20}") int maxRequests,
                                @Value("${rate-limit.login.window-seconds:60}") long windowSeconds) {
        this.enabled = enabled;
        this.loginPath = loginPath;
        this.rateLimiter = new FixedWindowRateLimiter(maxRequests, windowSeconds);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!enabled) {
            return true;
        }
        return !HttpMethod.POST.matches(request.getMethod()) || !loginPath.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String clientKey = resolveClientKey(request);
        RateLimitDecision decision = rateLimiter.tryAcquire(clientKey);

        if (!decision.allowed()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", Long.toString(decision.retryAfterSeconds()));
            response.getWriter().write(
                    "{\"error\":\"TOO_MANY_REQUESTS\",\"messages\":[\"Trop de tentatives de connexion, reessayez plus tard\"]}");
            log.warn("Rate limit depasse pour /api/auth/login (client={}, retryAfter={}s)",
                    clientKey, decision.retryAfterSeconds());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int comma = forwardedFor.indexOf(',');
            return (comma >= 0 ? forwardedFor.substring(0, comma) : forwardedFor).trim();
        }
        return request.getRemoteAddr();
    }
}
