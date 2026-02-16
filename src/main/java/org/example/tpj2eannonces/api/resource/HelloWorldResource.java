package org.example.tpj2eannonces.api.resource;

import java.util.Map;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/helloWorld")
@PermitAll
public class HelloWorldResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> hello() {
        return Map.of("message", "Hello World!");
    }
}
