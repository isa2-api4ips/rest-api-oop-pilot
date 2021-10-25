package eu.europa.ec.isa2.restapi.profile.model.schemas;

import eu.europa.ec.isa2.restapi.profile.constants.JSONConstants;

/**
 * Because only constants can be used in annotation values we can not use enum for this
 */
public class SchemaDescriptionConstants {

    public static String ID_URL="https://raw.githubusercontent.com/isa2-api4ips/rest-api-profile/main";

    /**
     * Problem schema definition
     */
    final static public String PROBLEM_ID = "https://raw.githubusercontent.com/isa2-api4ips/rest-api-profile/main/api-core-profile/components/schemas/problem.json ";
    final static public String PROBLEM_NAME = "Problem";
    final static public String PROBLEM_TITLE = "A Problem Details object (RFC 7807) defined by the ISA² IPS REST API Core Profile";
    final static public String PROBLEM_SCHEMA = JSONConstants.SCHEMA_V202012;
    final static public String PROBLEM_REQ_PROP_TITLE = "title";
    final static public String PROBLEM_REQ_PROP_TYPE = "type";
    final static public String PROBLEM_REQ_PROP_STATUS = "status";
    final static public String PROBLEM_DESCRIPTION = "A Problem Details object (RFC 7807) with ISA² IPS REST API extensions, " +
            "used for signals (responses) to messages";

    /**
     * Signal message definition
     */
    final static public String SIGNAL_ID = "https://raw.githubusercontent.com/isa2-api4ips/rest-api-profile/main/messaging-api-specification/components/schemas/signal-message.json";
    final static public String SIGNAL_NAME = "SignalMessage";
    final static public String SIGNAL_TITLE = "A Signal message which extends Problem object (RFC 7807) defined by the ISA² IPS REST API Core Profile";
    final static public String SIGNAL_SCHEMA = JSONConstants.SCHEMA_V202012;
    final static public String SIGNAL_REQ_PROP_TITLE = "title";
    final static public String SIGNAL_REQ_PROP_TYPE = "type";
    final static public String SIGNAL_REQ_PROP_STATUS = "status";
    final static public String SIGNAL_REQ_PROP_INSTANCE = "instance";
    final static public String SIGNAL_DESCRIPTION = "A Signal message which extends Problem object (RFC 7807) defined by the ISA² IPS REST API Core Profile";

    /**
     * MESSAGE_REFERENCE definition
     */
    final static public String MESSAGE_REFERENCE_ID = "https://raw.githubusercontent.com/isa2-api4ips/rest-api-profile/main/messaging-api-specification/components/schemas/message-reference.json";
    final static public String MESSAGE_REFERENCE_NAME = "MessageReference";
    final static public String MESSAGE_REFERENCE_TITLE = "A Message Reference object defined by the ISA² IPS REST API Messaging API Specification";
    final static public String MESSAGE_REFERENCE_SCHEMA = JSONConstants.SCHEMA_V202012;
    final static public String MESSAGE_REFERENCE_REQ_PROP_MESSAGE_ID = "messageId";
    final static public String MESSAGE_REFERENCE_REQ_PROP_SERVICE = "service";
    final static public String MESSAGE_REFERENCE_REQ_PROP_ACTION = "action";
    final static public String MESSAGE_REFERENCE_REQ_PROP_HREF = "href";
    final static public String MESSAGE_REFERENCE_DESCRIPTION = "A Message Reference object to be used when multiple messages could be retrieved from an API operation";

    /**
     * MESSAGE_REFERENCE_LIST definition
     */
    final static public String MESSAGE_REFERENCE_LIST_ID = "https://raw.githubusercontent.com/isa2-api4ips/rest-api-profile/main/messaging-api-specification/components/schemas/message-reference-list.json";
    final static public String MESSAGE_REFERENCE_LIST_NAME = "MessageReferenceList";
    final static public String MESSAGE_REFERENCE_LIST_TITLE = "A Message Reference object defined by the ISA² IPS REST API Messaging API Specification";
    final static public String MESSAGE_REFERENCE_LIST_SCHEMA = JSONConstants.SCHEMA_V202012;
    final static public String MESSAGE_REFERENCE_LIST_REQ_PROP_MESSAGE_REF_LIST = "messageReferenceList";
    final static public String MESSAGE_REFERENCE_LIST_DESCRIPTION = "A Message Reference object to be used when multiple messages could be retrieved from an API operation";

    /**
     * JWS COMPACT definition
     */
    final static public String JWS_COMPACT_ID = "https://raw.githubusercontent.com/isa2-api4ips/rest-api-profile/main/messaging-api-specification/components/schemas/jws-compact.json";
    final static public String JWS_COMPACT_NAME = "jws-compact";
    final static public String JWS_COMPACT_TITLE = "JWT token.";
    final static public String JWS_COMPACT_FORMAT = "jws-compact";
    final static public String JWS_COMPACT_PATTERN = "^[A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+){2}$";
    final static public String JWS_COMPACT_DESCRIPTION = "The JWS compact representation schema defines the string pattern as a regular expression, " +
            "denoting the structure a JWS compact token MUST follow to be a valid compact JWS Representation. " +
            "It is used for defining the value of the Original-Sender token.";
    /**
     * JWS COMPACT DETACHED definition
     */
    final static public String JWS_COMPACT_DETACHED_ID = "https://raw.githubusercontent.com/isa2-api4ips/rest-api-profile/main/api-core-profile/components/schemas/jws-compact-detached.json";
    final static public String JWS_COMPACT_DETACHED_NAME = "jws-compact-detached";
    final static public String JWS_COMPACT_DETACHED_TITLE = "The message-level and payload signature";
    final static public String JWS_COMPACT_DETACHED_FORMAT = "jws-compact-detached";
    final static public String JWS_COMPACT_DETACHED_PATTERN = "^[A-Za-z0-9_-]+(?:(\\.\\.)[A-Za-z0-9_-]+){1}";
    final static public String JWS_COMPACT_DETACHED_DESCRIPTION = "The JWS compact representation schema defines the string pattern as a regular expression, " +
            "denoting the structure a JWS compact token MUST follow to be a valid compact JWS Representation. " +
            "It is used for defining the value of the message-level signature and of the payload signature";
}
