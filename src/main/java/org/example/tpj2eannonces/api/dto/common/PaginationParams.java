package org.example.tpj2eannonces.api.dto.common;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Parametres de pagination generiques, reutilisables par tous les BeanParam.
 */
public class PaginationParams {

    @QueryParam("page")
    @DefaultValue("0")
    @Min(value = 0, message = "page doit etre >= 0")
    private int page;

    @QueryParam("size")
    @DefaultValue("10")
    @Min(value = 1, message = "size doit etre >= 1")
    @Max(value = 100, message = "size doit etre <= 100")
    private int size;

    public int getPage() { return page; }
    public int getSize() { return size; }
}
