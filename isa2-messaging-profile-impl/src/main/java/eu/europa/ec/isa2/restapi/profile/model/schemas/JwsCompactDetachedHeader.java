package eu.europa.ec.isa2.restapi.profile.model.schemas;

import io.swagger.v3.oas.models.media.Schema;


public class JwsCompactDetachedHeader extends Schema<String> {

    public static final JwsCompactDetachedHeader JWS_COMPACT_DETACHED_HEADER = new JwsCompactDetachedHeader();

    public JwsCompactDetachedHeader() {
        super("string", "jws-compact-detached");
        //setPattern("^[A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+){2}$");
        setPattern("^[A-Za-z0-9_-]+((?:\\.[A-Za-z0-9_-]+){2}|(?:(\\.\\.)[A-Za-z0-9_-]+){1})");
        setName("jws-compact-detached");
        setTitle("The message-level and payload signature");
        setDescription("The JWS compact representation schema defines the string pattern as a regular expression, denoting the structure a JWS compact token MUST follow to be a valid compact JWS Representation. It is used for defining the value of the message-level signature and of the payload signature");
    }
}
