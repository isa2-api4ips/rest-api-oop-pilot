package eu.europa.ec.isa2.restapi.profile.enums;


import eu.europa.ec.isa2.restapi.profile.model.schemas.JwsCompactDetachedHeader;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum MessagingParameterType {
    ORIGINAL_SENDER("Original-Sender", MessagingReferenceType.ORIGINAL_SENDER, MessagingParameterUsageType.ALL, "The representation of the Original Sender", null, MessagingParameterLocationType.HEADER,true, true,false, false, null, "9999::333222111", "The Original Sender is the entity that initiates the submission of the message. It is a single identifier and its representation is out of scope of the current specification."),
    ORIGINAL_SENDER_TOKEN("Original-Sender-Token", MessagingReferenceType.ORIGINAL_SENDER_TOKEN, MessagingParameterUsageType.ALL, "The ID Token, proving the identity of the Original Sender", null, MessagingParameterLocationType.HEADER, true,true,false, false, JwsCompactDetachedHeader.JWS_COMPACT_HEADER, "eyJhbGciOiJIUzINiIsnRI6IkpXVCJ9.eyJzdWIiOiIxM0NTY3ODkwIiwibmIiwiaWF0IoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMePOk6yJV_adQssw5c", "The Original Sender represents the authenticated entity acting as the user that sends the message using the client. Following the API Core Profile, the original sender MUST be represented with an OpenID Connect token or a signed JWT in both the form of Compact JWT Token, which is carried under the Original-Sender-Token HTTP Field"),
    FINAL_RECIPIENT("Final-Recipient", MessagingReferenceType.FINAL_RECIPIENT,   MessagingParameterUsageType.ALL, "The representation of the Final Recipient", null, MessagingParameterLocationType.HEADER, true,true,false, false, null, "9999::333222112", "The Final Recipient is the entity for whom the message is sent. Can be a single identifier or a set of identifiers, as described in the Recipient Addressing Schemes section of the specification. The representation of the final recipient is out of scope of the current specification."),
    MESSAGE_ID("messageId", MessagingReferenceType.MESSAGE_ID, MessagingParameterUsageType.ALL, "The unique identifier of the message sent", null, MessagingParameterLocationType.PATH,true, false,false, false, null, "dde12f67-c391-4851-8fa2-c07dd8532efd", "The MessageId is the unique identifier of the message submitted. It MUST be defined by the client. It is used for reliable messaging for guaranteeing the at-most-once message submission (no duplicate message-ids are allowed by the server implementing the API)."),
    MESSAGE_ID_HEADER("Message-Id", MessagingReferenceType.MESSAGE_ID_HEADER, MessagingParameterUsageType.MESSAGE_RESPONSE_ONLY, "The unique identifier of the response message received or the unique identifier of the signal message received", null, MessagingParameterLocationType.HEADER,true, false,false, false, null, "dde12f67-c391-4851-8fa2-c07dd8532efd", "When the rMessageId URL parameter can't be used for providing the identifier of the response message, e.g. in synchronous responses, the Message-Id header field can be used instead. It identifies either a response or a signal message received synchronously."),
    SERVICE_HEADER("Service", MessagingReferenceType.SERVICE_HEADER, MessagingParameterUsageType.MESSAGE_RESPONSE_ONLY,"A representation of the service the response message", null, MessagingParameterLocationType.PATH, true,false,false, false, null, "dsd-service", "When, in synchronous responses, the rService URL parameter cannot be used for providing the service of the response message, the Service header field can be used instead for the purpose. It is combined with the action from the Action header field to provide a complete domain-level message target."),
    ACTION_HEADER("Action", MessagingReferenceType.ACTION_HEADER, MessagingParameterUsageType.MESSAGE_RESPONSE_ONLY,"A representation of the action related to the service of the response message", null, MessagingParameterLocationType.PATH,true,false, false, false, null, "dsd-action", "When, in synchronous responses, the rAction URL parameter cannot be used for providing the action of the service of the response message, the Action header field can be used instead for the purpose. It is combined with the service from the Service header field to provide a complete domain-level message target."),
    TIMESTAMP("Timestamp", MessagingReferenceType.TIMESTAMP, MessagingParameterUsageType.ALL, "The exact timestamp of the message generation", "date-time", MessagingParameterLocationType.HEADER,true, false,false, false, null, "2021-06-17T08:30:00Z", "The Timestamp is the exact date and time at which the message was sent. It is provided by the client and verified by the server."),
    EDEL_MESSAGE_SIG("Edel-Message-Sig", MessagingReferenceType.EDEL_MESSAGE_SIG, MessagingParameterUsageType.ALL,"The detached JAdES signature signing the message to be sent", null, MessagingParameterLocationType.HEADER,false, false, false, false, JwsCompactDetachedHeader.JWS_COMPACT_DETACHED_HEADER, "eyJhbGciOiJIUzINiIsnRI6IkpXVCJ9..SflKxwRJSMeKKF2QT4fwpMePOk6yJV_adQssw5c", "The Edel-Message-Sig carries the signature of the HTTP Message following the API Core Profile on Message-Level Security. Following the light context constraints, the signature is optional for the client messages, but is RECOMMENDED for server messages."),
    RESPONSE_WEBHOOK("Response-Webhook", MessagingReferenceType.RESPONSE_WEBHOOK, MessagingParameterUsageType.ALL,"The URL to which the server will send the response", "uri", MessagingParameterLocationType.HEADER, false,true,false, true, null, null, "The Response-Webhook provides the callback URL that the server MUST use for sending a response. It is required when implementing the \"Send Message with Asynchronous Response – Push and Webhook Push\" exchange pattern."),
    SIGNAL_WEBHOOK("Signal-Webhook", MessagingReferenceType.SIGNAL_WEBHOOK, MessagingParameterUsageType.ALL,"The URL to which the server will send the signal", "uri", MessagingParameterLocationType.HEADER, false,true,false, true, null, null, "The Signal-Webhook provides the callback URL that the server MUST use for sending a signal back. It is required when implementing the \"Send Message with Asynchronous Response – Push and Webhook Pull\" exchange pattern."),
    SERVICE("service", MessagingReferenceType.SERVICE, MessagingParameterUsageType.ALL,"A representation of the service the message is submitted to", null, MessagingParameterLocationType.PATH, true,false,false, false, null, "dsd-service", "The Service metadata defines the service under which the message should be sent. It is combined with the Action metadata to provide a complete domain-level message target. The service metadata MUST be a URL Safe string."),
    ACTION("action", MessagingReferenceType.ACTION, MessagingParameterUsageType.ALL,"A representation of the action related to the service the message is submitted to", null, MessagingParameterLocationType.PATH,true,false, false, false, null, "dsd-action", "The Action metadata defines the action of the service under which the message should be sent. It is combined with the Service metadata to provide a complete domain-level target of the message. The action metadata MUST be a URL Safe string."),
    RESPONSE_MESSAGE_ID("rMessageId", MessagingReferenceType.RESPONSE_MESSAGE_ID, MessagingParameterUsageType.ALL,"The identifier of the response message being submitted.", null, MessagingParameterLocationType.PATH, true,false,false, false, null, null, "The identifier of the response message being submitted. It MUST be generated by the client submitting the response message."),
    RESPONSE_SERVICE("rService", MessagingReferenceType.RESPONSE_SERVICE, MessagingParameterUsageType.ALL,"A representation of the service the response message is submitted to", null, MessagingParameterLocationType.PATH, true,false,false, false, null, null, "The Response Service metadata defines the service under which the response message should be sent. It is combined with the Response Action metadata to provide a complete domain-level message target. The Response Service metadata MUST be a URL Safe string."),
    RESPONSE_ACTION("rAction", MessagingReferenceType.RESPONSE_ACTION, MessagingParameterUsageType.ALL,"A representation of the action related to the service the response message is submitted to", null, MessagingParameterLocationType.PATH,true,false, false, false, null, null, "The Response Action metadata defines the action of the service under which the response message should be sent. It is combined with the Response Service metadata to provide a complete domain-level target of the response message. The Response Action metadata MUST be a URL Safe string."),
    EDEL_PAYLOAD_SIG("Edel-Payload-Sig", MessagingReferenceType.EDEL_PAYLOAD_SIG, MessagingParameterUsageType.ALL, "The detached JAdES signature signing the payload to be sent", null, MessagingParameterLocationType.HEADER, false,false, true, false, JwsCompactDetachedHeader.JWS_COMPACT_DETACHED_HEADER, "eyJhbGciOiJIUzINiIsnRI6IkpXVCJ9..SflKxwRJSMeKKF2QT4fwpMePOk6yJV_adQssw5c", "The Edel-Payload-Sig carries the signature of a subpart of the Multipart message (see User Message section) following the API Core Profile on Payload Security."),
    CONTENT_DISPOSITION("Content-Disposition", MessagingReferenceType.CONTENT_DISPOSITION, MessagingParameterUsageType.ALL, "The Content-Disposition header, declaring the subpart as an attachment", null, MessagingParameterLocationType.HEADER, true,false, true, false, null, "Attachment", "The Content-Disposition header, declaring the subpart as an attachment. MUST be Attachment Type "),
    CONTENT_TYPE("Content-Type", MessagingReferenceType.CONTENT_TYPE, MessagingParameterUsageType.ALL, "The content type of the subpart of the multipart message", null, MessagingParameterLocationType.HEADER, true,false, true, false, null, "application/json", "The content type of the subpart of the multipart message. The value MUST be One of IANA Media Types!"),
    ;

    String name;
    String title;
    String description;

    String format;
    String example;
    boolean userMessageParameter;
    boolean payloadPart;
    boolean webhookParameters;
    boolean isRequired;
    MessagingParameterLocationType location;
    Schema schema;
    MessagingReferenceType messagingReferenceType;
    MessagingParameterUsageType messagingParameterUsageType;

    MessagingParameterType(String name,
                           MessagingReferenceType messagingReferenceType,
                           MessagingParameterUsageType messagingParameterUsageType,
                           String title,
                           String format,
                           MessagingParameterLocationType type,
                           boolean isRequired,
                           boolean userMessageParameter,
                           boolean payloadPart,
                           boolean webhookParameters,
                           Schema schema,
                           String example,
                           String description
    ) {
        this.name = name;
        this.title = title;
        this.format = format;
        this.location = type;
        this.description = description;
        this.isRequired=isRequired;
        this.userMessageParameter = userMessageParameter;
        this.payloadPart = payloadPart;
        this.webhookParameters = webhookParameters;
        this.schema = schema;
        this.example = example;
        this.messagingReferenceType = messagingReferenceType;
        this.messagingParameterUsageType = messagingParameterUsageType;

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

    public boolean isRequired() {
        return isRequired;
    }

    public boolean isUserMessageParameter() {
        return userMessageParameter;
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

    public MessagingParameterUsageType getMessagingParameterUsageType() {
        return messagingParameterUsageType;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getExample() {
        return example;
    }

    static public Optional<MessagingParameterType> getByName(String name) {
        return Arrays.stream(values()).filter(type -> StringUtils.equalsIgnoreCase(name, type.getName())).findFirst();
    }
    static public Optional<MessagingParameterType> getByMessagingReferenceName(String name) {
        return Arrays.stream(values()).filter(type -> StringUtils.equalsIgnoreCase(name, type.getMessagingReferenceType().getName())).findFirst();
    }
}

