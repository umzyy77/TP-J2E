package org.example.tpj2eannonces.shared.util;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringCollectionUtilsTest {

    @Test
    void normalizeDistinct_shouldReturnEmpty_whenNull() {
        assertThat(StringCollectionUtils.normalizeDistinct(null)).isEmpty();
    }

    @Test
    void normalizeDistinct_shouldReturnEmpty_whenEmptyCollection() {
        assertThat(StringCollectionUtils.normalizeDistinct(List.of())).isEmpty();
    }

    @Test
    void normalizeDistinct_shouldTrimAndDeduplicate() {
        List<String> input = Arrays.asList("  hello  ", "hello", "world", "  world  ");

        List<String> result = StringCollectionUtils.normalizeDistinct(input);

        assertThat(result).containsExactly("hello", "world");
    }

    @Test
    void normalizeDistinct_shouldSkipNulls() {
        List<String> input = Arrays.asList("hello", null, "world");

        List<String> result = StringCollectionUtils.normalizeDistinct(input);

        assertThat(result).containsExactly("hello", "world");
    }

    @Test
    void normalizeDistinct_shouldSkipBlankStrings() {
        List<String> input = Arrays.asList("hello", "  ", "", "world");

        List<String> result = StringCollectionUtils.normalizeDistinct(input);

        assertThat(result).containsExactly("hello", "world");
    }

    @Test
    void normalizeDistinct_shouldPreserveOrder() {
        List<String> input = List.of("banana", "apple", "cherry");

        List<String> result = StringCollectionUtils.normalizeDistinct(input);

        assertThat(result).containsExactly("banana", "apple", "cherry");
    }
}
