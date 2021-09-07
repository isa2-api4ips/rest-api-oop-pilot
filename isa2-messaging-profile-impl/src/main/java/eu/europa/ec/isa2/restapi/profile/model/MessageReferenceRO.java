package eu.europa.ec.isa2.restapi.profile.model;

import eu.europa.ec.isa2.restapi.profile.constants.JSONConstants;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;


@Schema(
        id = "https://joinup.ec.europa.eu/collection/api4dt/solution/.../message-reference-schema.json",
        schema = JSONConstants.SCHEMA_V202012,
        name = "MessageReference",
        title = "A Message Reference object defined by the ISA² IPS REST API Messaging API Specification",
        description = "A Message Reference object to be used when multiple messages could be retrieved from an API operation",
        requiredProperties = {"messageId",  "service", "action"}
)
public class MessageReferenceRO implements Serializable {

    @Schema(name = "messageId",   description = "The unique identifier of the message.", type = "string")
    String messageId;
    @Schema(name = "service",   description = "A representation of the service the message is submitted to.", type = "string")
    String service;
    @Schema(name = "action",   description = "A representation of the action related to the service the message is submitted to.", type = "string")
    String action;
    @Schema(name = "href",   description = "The direct link for getting the specific message", type = "string", format = "uri-reference")
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
        this.href = "/" + service + "/"+ action + "/"+messageId;
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
