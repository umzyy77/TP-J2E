package org.example.tpj2eannonces.api.resource;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/params")
@PermitAll
public class ParamsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> queryParams(
            @QueryParam("name") @DefaultValue("anonymous") String name,
            @QueryParam("age") @DefaultValue("0") int age) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", name);
        result.put("age", age);
        return result;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> pathParam(@PathParam("id") long id) {
        return Map.of("id", id);
    }
}
