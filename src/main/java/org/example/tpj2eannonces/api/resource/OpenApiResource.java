package org.example.tpj2eannonces.api.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.example.tpj2eannonces.api.dto.common.ApiErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;

@Path("/openapi")
@PermitAll
public class OpenApiResource {

    private static final String OPENAPI_CLASSPATH_FILE = "/openapi.yaml";
    private static final MediaType YAML_MEDIA_TYPE = MediaType.valueOf("application/yaml");
    private static final Logger logger = LoggerFactory.getLogger(OpenApiResource.class);

    @GET
    @Produces({MediaType.TEXT_PLAIN, "application/yaml"})
    public Response getSpec(@Context HttpHeaders headers) {
        try (InputStream input = OpenApiResource.class.getResourceAsStream(OPENAPI_CLASSPATH_FILE)) {
            if (input == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiErrorDTO.of("NOT_FOUND", "Specification OpenAPI introuvable"))
                        .build();
            }
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            boolean acceptsYaml = headers.getAcceptableMediaTypes().stream()
                    .anyMatch(mediaType -> mediaType.isCompatible(YAML_MEDIA_TYPE));
            MediaType responseType = acceptsYaml ? YAML_MEDIA_TYPE : MediaType.TEXT_PLAIN_TYPE;
            return Response.ok(yaml, responseType).build();
        } catch (IOException e) {
            logger.warn("Erreur lors du chargement de la specification OpenAPI", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiErrorDTO.of("INTERNAL_ERROR", "Erreur lors du chargement de la specification OpenAPI"))
                    .build();
        }
    }
}
