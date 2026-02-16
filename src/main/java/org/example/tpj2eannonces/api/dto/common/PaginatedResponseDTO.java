package org.example.tpj2eannonces.api.dto.common;

import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> data,
        long totalCount,
        int page,
        int pageSize,
        int totalPages
) {

    public static <T> PaginatedResponseDTO<T> of(List<T> data, long totalCount, int page, int pageSize) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 0;
        return new PaginatedResponseDTO<>(data, totalCount, page, pageSize, totalPages);
    }
}
