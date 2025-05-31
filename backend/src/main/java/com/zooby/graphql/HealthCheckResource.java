package com.zooby.graphql;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class HealthCheckResource {

    @Query("healthCheck")
    public String healthCheck() {
        return "OK";
    }
}
