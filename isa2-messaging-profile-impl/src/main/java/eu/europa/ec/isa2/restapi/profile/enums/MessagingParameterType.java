package eu.europa.ec.isa2.restapi.profile.enums;



import eu.europa.ec.isa2.restapi.profile.model.schemas.JwsCompactDetachedHeader;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum MessagingParameterType {
    ORIGINAL_SENDER("Original-Sender", MessagingReferenceType.ORIGINAL_SENDER, "The representation of the Original Sender", null, MessagingParameterLocationType.HEADER,false,false, JwsCompactDetachedHeader.JWS_COMPACT_HEADER, "eyJhbGciOiJIUzINiIsnRI6IkpXVCJ9.eyJzdWIiOiIxM0NTY3ODkwIiwibmIiwiaWF0IoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMePOk6yJV_adQssw5c","The Original Sender represents the authenticated entity acting as the user that sends the message using the client. Following the API Core Profile, the original sender MUST be represented with an OpenID Connect token, in the form of Compact JWT Token."),
    FINAL_RECIPIENT("Final-Recipient", MessagingReferenceType.FINAL_RECIPIENT,"The representation of the Final Recipient", null, MessagingParameterLocationType.HEADER,false,false, null,"9999::333222111","The Final Recipient is the entity for whom the message is sent. Can be a single identifier or a set of identifiers, as described in the Recipient Addressing Schemes section of the specification. The representation of the final recipient is out of scope of the current specification."),
    TIMESTAMP("Timestamp", MessagingReferenceType.TIMESTAMP,"The exact timestamp of the message generation", "date-time", MessagingParameterLocationType.HEADER, false,false, null,"2021-06-17T08:30:00Z", "The Timestamp is the exact date and time at which the message was sent. It is provided by the client and verified by the server."),
    EDEL_MESSAGE_SIG("Edel-Message-Sig", MessagingReferenceType.EDEL_MESSAGE_SIG,"The detached JAdES signature signing the message to be sent", null, MessagingParameterLocationType.HEADER,false,false,JwsCompactDetachedHeader.JWS_COMPACT_DETACHED_HEADER,"eyJhbGciOiJIUzINiIsnRI6IkpXVCJ9.eyJzdWIiOiIxM0NTY3ODkwIiwibmIiwiaWF0IoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMePOk6yJV_adQssw5c", "The Edel-Message-Sig carries the signature of the HTTP Message following the API Core Profile on Message-Level Security. Following the light context constraints, the signature is optional for the client messages, but is RECOMMENDED for server messages."),
    EDEL_PAYLOAD_SIG("Edel-Payload-Sig", MessagingReferenceType.EDEL_PAYLOAD_SIG,"The detached JAdES signature signing the payload to be sent", null, MessagingParameterLocationType.HEADER,true, false, JwsCompactDetachedHeader.JWS_COMPACT_DETACHED_HEADER,"eyJhbGciOiJIUzINiIsnRI6IkpXVCJ9.eyJzdWIiOiIxM0NTY3ODkwIiwibmIiwiaWF0IoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMePOk6yJV_adQssw5c","The Edel-Payload-Sig carries the signature of a subpart of the Multipart message (see User Message section) following the API Core Profile on Payload Security."),
    RESPONSE_WEBHOOK("Response-Webhook", MessagingReferenceType.RESPONSE_WEBHOOK,"The URL to which the server will send the response", "uri", MessagingParameterLocationType.HEADER,false,true, null,null,"The Response-Webhook provides the callback URL that the server MUST use for sending a response. It is required when implementing the \"Send Message with Asynchronous Response – Push and Webhook Push\" exchange pattern."),
    SIGNAL_WEBHOOK("Signal-Webhook", MessagingReferenceType.SIGNAL_WEBHOOK, "The URL to which the server will send the signal", "uri", MessagingParameterLocationType.HEADER,false,true, null,null,"The Signal-Webhook provides the callback URL that the server MUST use for sending a signal back. It is required when implementing the \"Send Message with Asynchronous Response – Push and Webhook Pull\" exchange pattern."),
    MESSAGE_ID("messageId", MessagingReferenceType.MESSAGE_ID,"The unique identifier of the message sent", null, MessagingParameterLocationType.PATH,false, false,null,"dde12f67-c391-4851-8fa2-c07dd8532efd","The MessageId is the unique identifier of the message submitted. It MUST be defined by the client. It is used for reliable messaging for guaranteeing the at-most-once message submission (no duplicate message-ids are allowed by the server implementing the API)."),
    SERVICE("service", MessagingReferenceType.SERVICE, "A representation of the service the message is submitted to", null, MessagingParameterLocationType.PATH,false,false, null,"dsd-service","The Service metadata defines the service under which the message should be sent. It is combined with the Action metadata to provide a complete domain-level message target. The service metadata MUST be a URL Safe string."),
    ACTION("action", MessagingReferenceType.ACTION, "A representation of the action related to the service the message is submitted to", null, MessagingParameterLocationType.PATH,false,false, null,"dsd-action","The Action metadata defines the action of the service under which the message should be sent. It is combined with the Service metadata to provide a complete domain-level target of the message. The action metadata MUST be a URL Safe string."),
    RESPONSE_MESSAGE_ID("rMessageId", MessagingReferenceType.RESPONSE_MESSAGE_ID, "The identifier of the response message being submitted.", null, MessagingParameterLocationType.PATH,false, false,null,null,"The identifier of the response message being submitted. It MUST be generated by the client submitting the response message."),
    RESPONSE_SERVICE("rService", MessagingReferenceType.RESPONSE_SERVICE, "A representation of the service the response message is submitted to", null, MessagingParameterLocationType.PATH,false,false, null,null,"The Service metadata defines the service under which the message should be sent. It is combined with the Action metadata to provide a complete domain-level message target. The service metadata MUST be a URL Safe string."),
    RESPONSE_ACTION("rAction", MessagingReferenceType.RESPONSE_ACTION,"A representation of the action related to the service the response message is submitted to", null, MessagingParameterLocationType.PATH,false,false, null,null,"The Action metadata defines the action of the service under which the message should be sent. It is combined with the Service metadata to provide a complete domain-level target of the message. The action metadata MUST be a URL Safe string.");

    String name;
    String title;
    String description;

    String format;
    String example;
    boolean payloadPart;
    boolean webhookParameters;
    MessagingParameterLocationType location;
    Schema schema;
    MessagingReferenceType messagingReferenceType;

    MessagingParameterType(String name, MessagingReferenceType messagingReferenceType, String title, String format, MessagingParameterLocationType type,
                           boolean payloadPart, boolean pushParameter, Schema schema,
                           String example,
                           String description
                           ) {
        this.name = name;
        this.title = title;
        this.format = format;
        this.location = type;
        this.description = description;
        this.payloadPart = payloadPart;
        this.webhookParameters = pushParameter;
        this.schema= schema;
        this.example= example;
        this.messagingReferenceType = messagingReferenceType;

    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    public boolean isPayloadPart() {
        return payloadPart;
    }

    public boolean isWebhookParameters() {
        return webhookParameters;
    }

    public MessagingParameterLocationType getLocation() {
        return location;
    }

    public MessagingReferenceType getMessagingReferenceType() {
        return messagingReferenceType;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getExample() {
        return example;
    }

    static public Optional<MessagingParameterType> getByName(String name){
        return Arrays.stream(values()).filter(type -> StringUtils.equalsIgnoreCase(name, type.getName())).findFirst();
    }
}

