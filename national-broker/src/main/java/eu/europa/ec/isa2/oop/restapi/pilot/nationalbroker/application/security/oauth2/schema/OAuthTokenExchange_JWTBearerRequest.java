package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2.schema;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Class to handle request body of Token Exchange request following IETF RFC-7523.<br/>
 * Schema object necessitated because of swagger bug: 3547
 *
 * @see <a target="_new" href="https://datatracker.ietf.org/doc/html/rfc7523#section-2.1">IETF RFC-7523</a>
 * @see <a target="_new" href="https://github.com/swagger-api/swagger-core/issues/3547">Swagger Core bug 3547</a>
 */
@Schema(type = "object", description = "Object holds HTTP request body elements for OAuth Token Exchange request.")
public class OAuthTokenExchange_JWTBearerRequest {
    @Schema(name = "grant_type",
            defaultValue = "urn:ietf:params:oauth:grant-type:jwt-bearer",
            example = "urn:ietf:params:oauth:grant-type:jwt-bearer",
            required = true,
            description = "The OAuth2 Grant Type of the assertion. Currently only JWT-Bearer grant is supported.")
    private String grant_type;
    @Schema(name = "assertion",
            required = true,
            description = "An Access Token issued by an Identity provider in Signed JWT form.")
    private String assertion;

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getAssertion() {
        return assertion;
    }

    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }
}
