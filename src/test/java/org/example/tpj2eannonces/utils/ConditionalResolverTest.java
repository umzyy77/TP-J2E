package org.example.tpj2eannonces.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class ConditionalResolverTest {

    @Test
    void resolve_shouldReturnFirstMatchingRuleValue() {
        AtomicInteger supplierCalls = new AtomicInteger();

        String result = ConditionalResolver.resolve(
                () -> "fallback",
                new ConditionalResolver.Rule<>(() -> false, () -> {
                    supplierCalls.incrementAndGet();
                    return "first";
                }),
                new ConditionalResolver.Rule<>(() -> true, () -> {
                    supplierCalls.incrementAndGet();
                    return "second";
                }),
                new ConditionalResolver.Rule<>(() -> true, () -> {
                    supplierCalls.incrementAndGet();
                    return "third";
                }));

        assertThat(result).isEqualTo("second");
        assertThat(supplierCalls.get()).isEqualTo(1);
    }

    @Test
    void resolve_shouldUseFallbackWhenNoRuleMatches() {
        String result = ConditionalResolver.resolve(
                () -> "fallback",
                new ConditionalResolver.Rule<>(() -> false, () -> "first"));

        assertThat(result).isEqualTo("fallback");
    }

    @Test
    void resolve_shouldIgnoreNullOrIncompleteRules() {
        String result = ConditionalResolver.resolve(
                () -> "fallback",
                null,
                new ConditionalResolver.Rule<>(null, () -> "invalid"),
                new ConditionalResolver.Rule<>(() -> true, null));

        assertThat(result).isEqualTo("fallback");
    }
}
