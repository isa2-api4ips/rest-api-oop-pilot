package eu.europa.ec.isa2.restapi.profile.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

import static eu.europa.ec.isa2.restapi.profile.model.schemas.SchemaDescriptionConstants.*;


@Schema(
        id = MESSAGE_REFERENCE_ID,
        schema = MESSAGE_REFERENCE_SCHEMA,
        name = MESSAGE_REFERENCE_NAME,
        title = MESSAGE_REFERENCE_TITLE,
        description = MESSAGE_REFERENCE_DESCRIPTION,
        requiredProperties = {MESSAGE_REFERENCE_REQ_PROP_MESSAGE_ID, MESSAGE_REFERENCE_REQ_PROP_SERVICE, MESSAGE_REFERENCE_REQ_PROP_ACTION, MESSAGE_REFERENCE_REQ_PROP_HREF}
)
public class MessageReferenceRO implements Serializable {

    @Schema(name = "messageId", description = "The unique identifier of the message.", type = "string")
    String messageId;
    @Schema(name = "service", description = "A representation of the service the message is submitted to.", type = "string")
    String service;
    @Schema(name = "action", description = "A representation of the action related to the service the message is submitted to.", type = "string")
    String action;
    @Schema(name = "href", description = "The direct link for getting the specific message", type = "string", format = "uri-reference")
    String href;

    public MessageReferenceRO() {
    }

    public MessageReferenceRO(String messageId, String service, String action, String href) {
        this.messageId = messageId;
        this.service = service;
        this.action = action;
        this.href = href;
    }

    public MessageReferenceRO(String messageId, String service, String action) {
        this.messageId = messageId;
        this.service = service;
        this.action = action;
        this.href = "/" + service + "/" + action + "/" + messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
