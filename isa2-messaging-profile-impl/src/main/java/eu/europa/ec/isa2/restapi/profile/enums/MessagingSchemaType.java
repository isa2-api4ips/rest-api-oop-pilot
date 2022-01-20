package eu.europa.ec.isa2.restapi.profile.enums;

import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.isa2.restapi.profile.model.schemas.SchemaDescriptionConstants.*;

/**
 * For the future consider to set own annotation for schema definition! And define annotations here and remove
 * SchemaDescriptionConstants
 */
public enum MessagingSchemaType {
    JWS_COMPACT(JWS_COMPACT_ID,
            JWS_COMPACT_NAME,
            MessagingReferenceType.JWS_COMPACT,
            JWS_COMPACT_TITLE,
            null,
            JWS_COMPACT_FORMAT,
            JWS_COMPACT_PATTERN,
            null,
            JWS_COMPACT_DESCRIPTION),
    JWS_COMPACT_DETACHED(JWS_COMPACT_DETACHED_ID,
            JWS_COMPACT_DETACHED_NAME,
            MessagingReferenceType.JWS_COMPACT_DETACHED,
            JWS_COMPACT_DETACHED_TITLE,
            null,
            JWS_COMPACT_DETACHED_FORMAT,
            JWS_COMPACT_DETACHED_PATTERN,
            null,
            JWS_COMPACT_DETACHED_DESCRIPTION),
    PROBLEM(PROBLEM_ID,
            PROBLEM_NAME,
            MessagingReferenceType.PROBLEM,
            PROBLEM_TITLE,
            PROBLEM_SCHEMA,
            null,
            null,
            Arrays.asList(PROBLEM_REQ_PROP_TITLE, PROBLEM_REQ_PROP_TYPE, PROBLEM_REQ_PROP_STATUS),
            PROBLEM_DESCRIPTION),
    SIGNAL_MESSAGE(SIGNAL_ID,
            SIGNAL_NAME,
            MessagingReferenceType.SIGNAL_MESSAGE,
            SIGNAL_TITLE,
            SIGNAL_SCHEMA,
            null,
            null,
            Arrays.asList(SIGNAL_REQ_PROP_TITLE, SIGNAL_REQ_PROP_TYPE, SIGNAL_REQ_PROP_STATUS, SIGNAL_REQ_PROP_INSTANCE),
            SIGNAL_DESCRIPTION),
    MESSAGE_REFERENCE(MESSAGE_REFERENCE_ID,
            MESSAGE_REFERENCE_NAME,
            MessagingReferenceType.MESSAGE_REFERENCE,
            MESSAGE_REFERENCE_TITLE,
            MESSAGE_REFERENCE_SCHEMA,
            null,
            null,
            Arrays.asList(MESSAGE_REFERENCE_REQ_PROP_MESSAGE_ID, MESSAGE_REFERENCE_REQ_PROP_SERVICE, MESSAGE_REFERENCE_REQ_PROP_ACTION, MESSAGE_REFERENCE_REQ_PROP_HREF),
            MESSAGE_REFERENCE_DESCRIPTION),
    MESSAGE_REFERENCE_LIST(MESSAGE_REFERENCE_LIST_ID,
            MESSAGE_REFERENCE_LIST_NAME,
            MessagingReferenceType.MESSAGE_REFERENCE_LIST,
            MESSAGE_REFERENCE_LIST_TITLE,
            MESSAGE_REFERENCE_LIST_SCHEMA,
            null,
            null,
            Arrays.asList(MESSAGE_REFERENCE_LIST_REQ_PROP_MESSAGE_REF_LIST),
            MESSAGE_REFERENCE_LIST_DESCRIPTION);


    final public String id;
    String name;
    MessagingReferenceType messagingReferenceType;
    String title;
    String format;
    String pattern;
    String schema;
    List<String> requiredProperties;
    String description;

    MessagingSchemaType(String id, String name, MessagingReferenceType messagingReferenceType, String title, String schema, String format, String pattern, List<String> requiredProperties, String description) {
        this.id = id;
        this.name = name;
        this.messagingReferenceType = messagingReferenceType;
        this.title = title;
        this.schema = schema;
        this.format = format;
        this.pattern = pattern;
        this.requiredProperties = requiredProperties;
        this.description = description;
    }

    final public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getFormat() {
        return format;
    }

    public String getPattern() {
        return pattern;
    }

    public String getSchema() {
        return schema;
    }

    public List<String> getRequiredProperties() {
        return requiredProperties;
    }

    public String getDescription() {
        return description;
    }

    public MessagingReferenceType getMessagingReferenceType() {
        return messagingReferenceType;
    }

    static public Optional<MessagingSchemaType> getByName(String name){
        return Arrays.stream(values()).filter(type -> StringUtils.equalsIgnoreCase(name, type.getName())).findFirst();
    }
}