package org.example.tpj2eannonces.core.web.dto;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorDTOTest {

    @Test
    void of_withSingleMessage_shouldCreateDTO() {
        ApiErrorDTO dto = ApiErrorDTO.of("ERROR", "Something went wrong");

        assertThat(dto.error()).isEqualTo("ERROR");
        assertThat(dto.messages()).containsExactly("Something went wrong");
    }

    @Test
    void of_withMessageList_shouldCreateDTO() {
        List<String> messages = List.of("error 1", "error 2");
        ApiErrorDTO dto = ApiErrorDTO.of("VALIDATION_ERROR", messages);

        assertThat(dto.error()).isEqualTo("VALIDATION_ERROR");
        assertThat(dto.messages()).containsExactly("error 1", "error 2");
    }
}
