package org.example.tpj2eannonces.api.resource;

import java.io.IOException;
import java.io.InputStream;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/openapi")
@PermitAll
public class OpenApiResource {

    private static final String OPENAPI_CLASSPATH_FILE = "/openapi.yaml";
    private static final Logger logger = LoggerFactory.getLogger(OpenApiResource.class);
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpec() {
        try (InputStream input = openApiInputStream()) {
            if (input == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiErrorDTO.of("NOT_FOUND", "Specification OpenAPI introuvable"))
                        .build();
            }
            JsonNode specification = readSpecification(input);
            String json = toJson(specification);
            return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (IOException e) {
            logger.warn("Erreur lors du chargement de la specification OpenAPI", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiErrorDTO.of("INTERNAL_ERROR", "Erreur lors du chargement de la specification OpenAPI"))
                    .build();
        }
    }

    protected InputStream openApiInputStream() {
        return OpenApiResource.class.getResourceAsStream(OPENAPI_CLASSPATH_FILE);
    }

    protected JsonNode readSpecification(InputStream input) throws IOException {
        return YAML_MAPPER.readTree(input);
    }

    protected String toJson(JsonNode specification) throws IOException {
        return JSON_MAPPER.writeValueAsString(specification);
    }
}
