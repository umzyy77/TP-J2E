package org.example.tpj2eannonces.api.dto.annonce;

import org.example.tpj2eannonces.api.dto.common.PaginationParams;
import org.example.tpj2eannonces.model.AnnonceStatus;

import jakarta.ws.rs.QueryParam;

public class AnnonceSearchParams extends PaginationParams {

    @QueryParam("q")
    private String keyword;

    @QueryParam("category")
    private Long categoryId;

    @QueryParam("status")
    private AnnonceStatus status;

    public String getKeyword()        { return keyword; }
    public Long getCategoryId()       { return categoryId; }
    public AnnonceStatus getStatus()  { return status; }

    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }

    public boolean hasFilters() {
        return categoryId != null || status != null;
    }
}
