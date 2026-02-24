package org.example.tpj2eannonces.core.ratelimit;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter {

    private static final String UNKNOWN_CLIENT = "unknown";
    private static final int MAX_TRACKED_CLIENTS = 10_000;

    private final int maxRequests;
    private final long windowSeconds;
    private final Clock clock;
    private final ConcurrentHashMap<String, CounterWindow> counters = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowSeconds) {
        this(maxRequests, windowSeconds, Clock.systemUTC());
    }

    public FixedWindowRateLimiter(int maxRequests, long windowSeconds, Clock clock) {
        this.maxRequests = Math.max(1, maxRequests);
        this.windowSeconds = Math.max(1, windowSeconds);
        this.clock = clock;
    }

    public RateLimitDecision tryAcquire(String clientKey) {
        String key = normalizeClientKey(clientKey);
        long now = nowEpochSecond();

        CounterWindow updatedWindow = counters.compute(key, (_, current) -> {
            if (current == null || isExpired(now, current.windowStartEpochSecond())) {
                return new CounterWindow(now, 1);
            }
            return new CounterWindow(current.windowStartEpochSecond(), current.count() + 1);
        });

        if (counters.size() > MAX_TRACKED_CLIENTS) {
            pruneExpiredEntries(now);
        }

        boolean allowed = updatedWindow.count() <= maxRequests;
        long retryAfterSeconds = allowed
                ? 0
                : Math.max(1, windowSeconds - (now - updatedWindow.windowStartEpochSecond()));

        return new RateLimitDecision(allowed, retryAfterSeconds);
    }

    private String normalizeClientKey(String clientKey) {
        if (clientKey == null || clientKey.isBlank()) {
            return UNKNOWN_CLIENT;
        }
        return clientKey.trim();
    }

    private boolean isExpired(long now, long windowStartEpochSecond) {
        return now - windowStartEpochSecond >= windowSeconds;
    }

    private void pruneExpiredEntries(long now) {
        counters.entrySet().removeIf(entry -> isExpired(now, entry.getValue().windowStartEpochSecond()));
    }

    private long nowEpochSecond() {
        return Instant.now(clock).getEpochSecond();
    }

    public record RateLimitDecision(boolean allowed, long retryAfterSeconds) {
    }

    private record CounterWindow(long windowStartEpochSecond, int count) {
    }
}
