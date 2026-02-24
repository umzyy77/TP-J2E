package org.example.tpj2eannonces.features.annonce.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.springframework.format.annotation.DateTimeFormat;

public record AnnonceSearchDTO(
        String q,
        AnnonceStatus status,
        Long categoryId,
        UUID authorId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime fromDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime toDate
) {
    public boolean hasFilters() {
        return q != null || status != null || categoryId != null
                || authorId != null || fromDate != null || toDate != null;
    }
}
