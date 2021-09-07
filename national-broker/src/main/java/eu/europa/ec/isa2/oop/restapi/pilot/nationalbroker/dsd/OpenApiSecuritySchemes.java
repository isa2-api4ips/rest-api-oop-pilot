package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(name = "NationalBroker_Http_BearerTokenAuthorization"
        , description = "The Access Token issued by invoking the endpoint **/oauth/token** of National Broker should be provided here as Bearer token for authorization."
        , type = SecuritySchemeType.HTTP
        , scheme = "Bearer"
        , bearerFormat = "Opaque")
public interface OpenApiSecuritySchemes {
}
