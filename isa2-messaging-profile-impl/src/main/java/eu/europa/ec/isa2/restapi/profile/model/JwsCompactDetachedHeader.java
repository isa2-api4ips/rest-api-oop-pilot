package eu.europa.ec.isa2.restapi.profile.model;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "string",
        format = "jws-compact-detached",
        name = "JwsCompactDetached",
        title = "The message-level and payload signature",
        pattern = "^[A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+){2}$",
        description ="The JWS compact representation schema defines the string pattern as a regular expression, denoting the structure a JWS compact token MUST follow to be a valid compact JWS Representation. It is used for defining the value of the message-level signature and of the payload signature" )
public class JwsCompactDetachedHeader {
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
