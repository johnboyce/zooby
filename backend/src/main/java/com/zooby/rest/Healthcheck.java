package com.zooby.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
public class Healthcheck {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response check() {
        return Response.ok("{\"status\":\"ok\"}").build();
    }
}
