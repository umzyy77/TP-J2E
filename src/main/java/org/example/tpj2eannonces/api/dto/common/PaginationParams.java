package org.example.tpj2eannonces.api.dto.common;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

/**
 * Parametres de pagination generiques, reutilisables par tous les BeanParam.
 */
public class PaginationParams {

    @QueryParam("page")
    @DefaultValue("0")
    private int page;

    @QueryParam("size")
    @DefaultValue("10")
    private int size;

    public int getPage() { return page; }
    public int getSize() { return size; }
}
