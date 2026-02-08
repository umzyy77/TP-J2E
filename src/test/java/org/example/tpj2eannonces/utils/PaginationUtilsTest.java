package org.example.tpj2eannonces.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PaginationUtilsTest {

    @Test
    void buildBaseUrl_shouldAppendOnlyValidParams() {
        String url = PaginationUtils.buildBaseUrl(
                "/MasterAnnonce", "/AnnonceList",
                new PaginationUtils.QueryParam("q", "velo"),
                new PaginationUtils.QueryParam("author", ""),
                new PaginationUtils.QueryParam("category", "3"),
                new PaginationUtils.QueryParam("status", null));

        assertThat(url).isEqualTo("/MasterAnnonce/AnnonceList?_=1&q=velo&category=3");
    }

    @Test
    void buildBaseUrl_shouldHandleNullContextAndResourcePath() {
        String url = PaginationUtils.buildBaseUrl(null, null);

        assertThat(url).isEqualTo("?_=1");
    }

    @Test
    void buildBaseUrl_shouldIgnoreNullOrInvalidQueryParams() {
        String url = PaginationUtils.buildBaseUrl(
                "/ctx", "/list",
                null,
                new PaginationUtils.QueryParam("", "1"),
                new PaginationUtils.QueryParam("page", "2"));

        assertThat(url).isEqualTo("/ctx/list?_=1&page=2");
    }
}
