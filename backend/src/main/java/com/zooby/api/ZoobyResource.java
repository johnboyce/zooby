package com.zooby.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/zooby")
public class ZoobyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Zooby backend is live!";
    }
}
