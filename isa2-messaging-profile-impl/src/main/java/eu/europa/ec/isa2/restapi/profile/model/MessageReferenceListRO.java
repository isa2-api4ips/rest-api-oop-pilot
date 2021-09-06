package eu.europa.ec.isa2.restapi.profile.model;

import eu.europa.ec.isa2.restapi.profile.constants.JSONConstants;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Schema(
        id = "https://joinup.ec.europa.eu/collection/api4dt/solution/.../message-reference-schema-list.json",
        schema = JSONConstants.SCHEMA_V202012,
        name = "MessageReferenceList",
        title = "A Message Reference object defined by the ISA² IPS REST API Messaging API Specification",
        description = "A Message Reference object to be used when multiple messages could be retrieved from an API operation",
        requiredProperties = {"messageReferenceList"}

)
public class MessageReferenceListRO implements Serializable {

    @Schema(name = "messageReferenceList",
            description = "List of message references.")
    List<MessageReferenceRO> messageReferenceList;

    @Schema(name = "count",   description = "Count of all messages", type = "integer")
    Integer count;

    @Schema(name = "limit",   description = "limit number of references.", type = "integer")
    Integer limit = -1;
    @Schema(name = "offset",   description = "offset of references.", type = "integer")
    Integer offset = 0;

    public List<MessageReferenceRO> getMessageReferenceList() {
        if (messageReferenceList == null) {
            messageReferenceList = new ArrayList<>();
        }
        return messageReferenceList;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
