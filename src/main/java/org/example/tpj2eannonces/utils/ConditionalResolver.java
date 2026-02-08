package org.example.tpj2eannonces.utils;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class ConditionalResolver {

    private ConditionalResolver() {
    }

    @SafeVarargs
    public static <T> T resolve(Supplier<T> fallback, Rule<T>... rules) {
        if (rules != null) {
            for (Rule<T> rule : rules) {
                if (rule == null || rule.condition() == null || rule.valueSupplier() == null) {
                    continue;
                }
                if (rule.condition().getAsBoolean()) {
                    return rule.valueSupplier().get();
                }
            }
        }
        return fallback.get();
    }

    public record Rule<T>(BooleanSupplier condition, Supplier<T> valueSupplier) {
    }
}
