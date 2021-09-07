package eu.europa.ec.isa2.oop.restapi.docsapi;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.*;

@SecuritySchemes({
        @SecurityScheme(name = "DSD_ClientCredentials_OAuthSecurity"
                , type = SecuritySchemeType.OAUTH2
                , flows = @OAuthFlows(
                        clientCredentials = @OAuthFlow(tokenUrl = "properties:dsd.oauth2.spring.security.token.url"
                                , scopes = @OAuthScope(name = "ROLE_DSD", description = "Authorization DSD token"))))
        /*, @SecurityScheme(name = "DSD_Http_BearerTokenAuthorization"
        , description = "OKTA OAuth server does not allow client credentials authorization from web browser. Hence providing Bearer Token Authorization for use in **Swagger UI**."
        , type = SecuritySchemeType.HTTP
        , scheme = "Bearer"
        , bearerFormat = "Opaque")*/
})
public interface DSDOpenApiSecuritySchemes {
}
