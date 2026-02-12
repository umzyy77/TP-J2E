package org.example.tpj2eannonces.utils;

public final class RequestParamUtils {

    private RequestParamUtils() {
    }

    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }

    public static String normalizeBlankToNull(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    public static int parseNonNegativeInt(String value, int defaultValue) {
        if (!hasValue(value)) {
            return defaultValue;
        }
        try {
            return Math.max(0, Integer.parseInt(value));
        } catch (NumberFormatException _) {
            return defaultValue;
        }
    }
}
