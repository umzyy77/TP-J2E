package org.example.tpj2eannonces.api.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.ws.rs.core.Response;

class OpenApiResourceTest {

    @Test
    void getSpec_shouldReturn200WhenSpecificationIsAvailable() {
        OpenApiResource resource = new OpenApiResource();

        try (Response response = resource.getSpec()) {
            assertThat(response.getStatus()).isEqualTo(200);
            String body = (String) response.getEntity();
            assertThat(body).contains("\"openapi\"");
        }
    }

    @Test
    void getSpec_shouldReturn404WhenSpecificationIsMissing() {
        OpenApiResource resource = new OpenApiResource() {
            @Override
            protected InputStream openApiInputStream() {
                return null;
            }
        };

        try (Response response = resource.getSpec()) {
            assertThat(response.getStatus()).isEqualTo(404);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("NOT_FOUND");
        }
    }

    @Test
    void getSpec_shouldReturn500WhenParsingFails() {
        OpenApiResource resource = new OpenApiResource() {
            @Override
            protected InputStream openApiInputStream() {
                return new ByteArrayInputStream("openapi: 3.0.3".getBytes(StandardCharsets.UTF_8));
            }

            @Override
            protected JsonNode readSpecification(InputStream input) throws IOException {
                throw new IOException("boom");
            }
        };

        try (Response response = resource.getSpec()) {
            assertThat(response.getStatus()).isEqualTo(500);
            ApiErrorDTO error = (ApiErrorDTO) response.getEntity();
            assertThat(error.error()).isEqualTo("INTERNAL_ERROR");
        }
    }
}
