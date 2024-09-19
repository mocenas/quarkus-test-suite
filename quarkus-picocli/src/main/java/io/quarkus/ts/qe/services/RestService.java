package io.quarkus.ts.qe.services;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello")
public class RestService {

    private static String configuredResponse = "";

    public static void setConfiguredResponse(String configuredResponse) {
        RestService.configuredResponse = configuredResponse;
    }

    @GET
    public String getHello() {
        return "Hello";
    }

    @GET
    @Path("/configured")
    public String getConfiguredResponse() {
        return configuredResponse;
    }
}
