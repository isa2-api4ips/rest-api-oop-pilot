package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "object", name = "OAuth error response", description = "Details describing the OAuth error")
public class OAuthErrorRO {
    @Schema(name = "error", description = "OAuth Error code")
    private String error;
    private String error_description;
    private String error_uri;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getError_uri() {
        return error_uri;
    }

    public void setError_uri(String error_uri) {
        this.error_uri = error_uri;
    }
}
