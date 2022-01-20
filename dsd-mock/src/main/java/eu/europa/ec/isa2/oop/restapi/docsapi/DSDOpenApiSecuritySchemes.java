package eu.europa.ec.isa2.oop.restapi.docsapi;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.*;

@SecuritySchemes({
        @SecurityScheme(name = "DSD_ClientCredentials_OAuthSecurity"
                , description = "<p>Client Credentials authorization between National Broker and DSD using OKTA authorization server.<br/></p>" +
                " <p><b>Important Note:</b>OKTA Oauth server does not allow client credentials authorization from web browser like when using Swagger UI. " +
                "When testing the services with Swagger UI, use <b>DSD_Http_BearerTokenAuthorization</b> to provide an access token that has been obtained previously outside of Swagger UI.</p>"
                , type = SecuritySchemeType.OAUTH2
                , flows = @OAuthFlows(
                clientCredentials = @OAuthFlow(tokenUrl = "properties:dsd.oauth2.spring.security.token.url"
                        , scopes = @OAuthScope(name = "ROLE_DSD", description = "Authorization DSD token"))))
        ,
        @SecurityScheme(name = "DSD_Http_BearerTokenAuthorization"
                , description = "OKTA OAuth server does not allow client credentials authorization from web browser. Hence providing Bearer Token Authorization for use in **Swagger UI**. <br/><br/>" +
                "**Note:** this is only a workaround for a limitation of using Swagger UI. For system integration please use **DSD_ClientCredentials_OAuthSecurity** . <br/><br/>" +
                "**Usage:** Submit an HTTPS POST request to the token url of **DSD_ClientCredentials_OAuthSecurity** with body contents **grant_type=client_credentials** and **scope=ROLE_DSD** in **x-www-form-encoded** format. " +
                "As HTTP basic authorization send the **Client ID** and **Client Secret** of the National Broker. The OKTA server will return a JSON response containing an access token as a JWT." +
                "Submit the JWT in this **DSD_Http_BearerTokenAuthorization**."
                , type = SecuritySchemeType.HTTP
                , scheme = "Bearer"
                , bearerFormat = "Opaque")
})
public interface DSDOpenApiSecuritySchemes {
}
