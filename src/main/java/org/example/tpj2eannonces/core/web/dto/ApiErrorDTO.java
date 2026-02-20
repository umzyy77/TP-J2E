package org.example.tpj2eannonces.core.web.dto;

import java.util.List;

public record ApiErrorDTO(
        String error,
        List<String> messages
) {

    public static ApiErrorDTO of(String error, String message) {
        return new ApiErrorDTO(error, List.of(message));
    }

    public static ApiErrorDTO of(String error, List<String> messages) {
        return new ApiErrorDTO(error, messages);
    }
}
