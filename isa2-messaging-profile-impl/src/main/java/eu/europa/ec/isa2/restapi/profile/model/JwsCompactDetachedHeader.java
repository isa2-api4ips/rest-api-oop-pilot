package eu.europa.ec.isa2.restapi.profile.model;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "string",
        format = "jws-compact-detached",
        name = "JwsCompactDetached",
        title = "The format for the message-level and payload signature",
        pattern = "^[A-Za-z0-9_-]+(?:(\\.\\.)[A-Za-z0-9_-]+){1}",
        description ="Defines the string pattern as a regular expression that MUST be followed to represent detached JWS compact tokens" )
public class JwsCompactDetachedHeader {
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
