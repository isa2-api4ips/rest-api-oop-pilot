package eu.europa.ec.isa2.restapi.profile.model.schemas;

import eu.europa.ec.isa2.restapi.profile.enums.MessagingSchemaType;
import io.swagger.v3.oas.models.media.Schema;


public class JwsCompactDetachedHeader extends Schema<String> {
    public static final JwsCompactDetachedHeader JWS_COMPACT_HEADER = new JwsCompactDetachedHeader(MessagingSchemaType.JWS_COMPACT);
    public static final JwsCompactDetachedHeader JWS_COMPACT_DETACHED_HEADER = new JwsCompactDetachedHeader(MessagingSchemaType.JWS_COMPACT_DETACHED);

    public JwsCompactDetachedHeader(MessagingSchemaType type) {
        super("string", type.getFormat());
        setPattern(type.getPattern());
        setName(type.getName());
        setTitle(type.getTitle());
        setDescription(type.getDescription());
    }
}
