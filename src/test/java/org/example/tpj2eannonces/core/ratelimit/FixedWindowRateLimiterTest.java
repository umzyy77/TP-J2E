package org.example.tpj2eannonces.core.ratelimit;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.example.tpj2eannonces.core.ratelimit.FixedWindowRateLimiter.RateLimitDecision;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FixedWindowRateLimiterTest {

    @Test
    void shouldAllowRequestsWithinLimit() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-24T00:00:00Z"));
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 60, clock);

        RateLimitDecision first = limiter.tryAcquire("127.0.0.1");
        RateLimitDecision second = limiter.tryAcquire("127.0.0.1");

        assertThat(first.allowed()).isTrue();
        assertThat(second.allowed()).isTrue();
    }

    @Test
    void shouldRejectRequestWhenLimitExceeded() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-24T00:00:00Z"));
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 60, clock);

        limiter.tryAcquire("127.0.0.1");
        limiter.tryAcquire("127.0.0.1");
        RateLimitDecision rejected = limiter.tryAcquire("127.0.0.1");

        assertThat(rejected.allowed()).isFalse();
        assertThat(rejected.retryAfterSeconds()).isEqualTo(60);
    }

    @Test
    void shouldResetWindowAfterExpiration() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-24T00:00:00Z"));
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(1, 60, clock);

        limiter.tryAcquire("127.0.0.1");
        RateLimitDecision rejected = limiter.tryAcquire("127.0.0.1");
        assertThat(rejected.allowed()).isFalse();

        clock.plusSeconds(61);
        RateLimitDecision afterReset = limiter.tryAcquire("127.0.0.1");

        assertThat(afterReset.allowed()).isTrue();
    }

    @Test
    void shouldTrackClientsIndependently() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-24T00:00:00Z"));
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(1, 60, clock);

        limiter.tryAcquire("10.0.0.1");
        RateLimitDecision otherClient = limiter.tryAcquire("10.0.0.2");

        assertThat(otherClient.allowed()).isTrue();
    }

    @Test
    void shouldUseUnknownClient_whenKeyIsNull() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-24T00:00:00Z"));
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(1, 60, clock);

        RateLimitDecision first = limiter.tryAcquire(null);
        assertThat(first.allowed()).isTrue();

        RateLimitDecision second = limiter.tryAcquire("");
        assertThat(second.allowed()).isFalse();
    }

    @Test
    void shouldPruneExpiredEntries_whenMaxClientsExceeded() {
        MutableClock clock = new MutableClock(Instant.parse("2026-02-24T00:00:00Z"));
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(1, 1, clock);

        // Fill beyond MAX_TRACKED_CLIENTS (10_000)
        for (int i = 0; i <= 10_000; i++) {
            limiter.tryAcquire("client-" + i);
        }

        // Advance time so all entries are expired
        clock.plusSeconds(2);

        // This should trigger pruning
        RateLimitDecision decision = limiter.tryAcquire("new-client");
        assertThat(decision.allowed()).isTrue();
    }

    private static final class MutableClock extends Clock {

        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        private void plusSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
