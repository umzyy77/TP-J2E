package org.example.tpj2eannonces.shared.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public final class StringCollectionUtils {

    private StringCollectionUtils() {
    }

    public static List<String> normalizeDistinct(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> uniqueValues = new LinkedHashSet<>();
        for (String value : values) {
            if (value == null) {
                continue;
            }
            String normalizedValue = value.trim();
            if (!normalizedValue.isEmpty()) {
                uniqueValues.add(normalizedValue);
            }
        }
        return List.copyOf(uniqueValues);
    }
}
