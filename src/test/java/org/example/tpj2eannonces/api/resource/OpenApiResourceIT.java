package org.example.tpj2eannonces.api.resource;

import static org.assertj.core.api.Assertions.assertThat;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

class OpenApiResourceIT extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(OpenApiResource.class);
    }

    @Test
    void openApi_shouldReturnYamlContent() {
        try (Response response = target("/openapi").request(MediaType.TEXT_PLAIN).get()) {
            assertThat(response.getStatus()).isEqualTo(200);
            String body = response.readEntity(String.class);
            assertThat(body).contains("openapi: 3.0.3");
        }
    }
}
