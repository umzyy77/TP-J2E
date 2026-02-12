package org.example.tpj2eannonces.utils;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static String buildBaseUrl(String contextPath, String resourcePath, QueryParam... queryParams) {
        String safeContextPath = contextPath == null ? "" : contextPath;
        String safeResourcePath = resourcePath == null ? "" : resourcePath;

        StringBuilder baseUrl = new StringBuilder(safeContextPath)
                .append(safeResourcePath)
                .append("?_=1");

        if (queryParams == null) {
            return baseUrl.toString();
        }

        for (QueryParam queryParam : queryParams) {
            if (queryParam == null) {
                continue;
            }
            appendParam(baseUrl, queryParam.name(), queryParam.value());
        }
        return baseUrl.toString();
    }

    private static void appendParam(StringBuilder baseUrl, String paramName, String paramValue) {
        if (!RequestParamUtils.hasValue(paramName) || !RequestParamUtils.hasValue(paramValue)) {
            return;
        }
        baseUrl.append("&").append(paramName).append("=").append(paramValue);
    }

    public record QueryParam(String name, String value) {
    }
}
