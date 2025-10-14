package com.redhat.workshop;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Locale;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

    private final GreetingService service;
    private final String countryCode;

    public GreetingResource(GreetingService service,
            @ConfigProperty(name = "country.code", defaultValue = "EN") String configuredCountry) {
        this.service = service;
        this.countryCode = normalize(configuredCountry);
    }

    @GET
    public Response greeting() {
        return service.findGreeting(countryCode)
                .map(greeting -> Response.ok(new GreetingResponse(greeting.code(), greeting.message())).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Unknown country code '" + countryCode + "'"))
                        .build());
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "EN";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    public record GreetingResponse(String code, String message) { }

    public record ErrorResponse(String error) { }
}
