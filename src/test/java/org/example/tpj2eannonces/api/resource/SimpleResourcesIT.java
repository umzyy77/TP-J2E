package org.example.tpj2eannonces.api.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

class SimpleResourcesIT extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(HelloWorldResource.class)
                .register(ParamsResource.class);
    }

    @Test
    void hello_shouldReturn200() {
        Response response = target("/helloWorld")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        @SuppressWarnings("unchecked")
        Map<String, String> body = response.readEntity(Map.class);
        assertThat(body).containsEntry("message", "Hello World!");
        response.close();
    }

    @Test
    void queryParams_shouldReturn200() {
        Response response = target("/params")
                .queryParam("name", "test")
                .queryParam("age", 25)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.readEntity(Map.class);
        assertThat(body).containsEntry("name", "test");
        assertThat(String.valueOf(body.get("age"))).isEqualTo("25");
        response.close();
    }

    @Test
    void pathParam_shouldReturn200() {
        Response response = target("/params/42")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.readEntity(Map.class);
        assertThat(String.valueOf(body.get("id"))).isEqualTo("42");
        response.close();
    }
}
