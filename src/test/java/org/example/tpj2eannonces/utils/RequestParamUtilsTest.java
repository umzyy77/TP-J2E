package org.example.tpj2eannonces.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RequestParamUtilsTest {

    @ParameterizedTest
    @CsvSource(value = {
            "NULL,false",
            "'',false",
            "' ',true",
            "abc,true"
    }, nullValues = "NULL")
    void hasValue_shouldReturnExpectedResult(String value, boolean expected) {
        assertThat(RequestParamUtils.hasValue(value)).isEqualTo(expected);
    }

    @Test
    void normalizeBlankToNull_shouldTrimAndHandleBlankValues() {
        assertThat(RequestParamUtils.normalizeBlankToNull(null)).isNull();
        assertThat(RequestParamUtils.normalizeBlankToNull("")).isNull();
        assertThat(RequestParamUtils.normalizeBlankToNull("   ")).isNull();
        assertThat(RequestParamUtils.normalizeBlankToNull("  hello  ")).isEqualTo("hello");
    }

    @Test
    void parseNonNegativeInt_shouldReturnExpectedValues() {
        assertThat(RequestParamUtils.parseNonNegativeInt(null, 7)).isEqualTo(7);
        assertThat(RequestParamUtils.parseNonNegativeInt("", 7)).isEqualTo(7);
        assertThat(RequestParamUtils.parseNonNegativeInt("abc", 7)).isEqualTo(7);
        assertThat(RequestParamUtils.parseNonNegativeInt("-3", 7)).isZero();
        assertThat(RequestParamUtils.parseNonNegativeInt("12", 7)).isEqualTo(12);
    }
}
