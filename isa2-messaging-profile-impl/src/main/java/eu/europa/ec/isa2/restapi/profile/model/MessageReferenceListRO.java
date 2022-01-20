package eu.europa.ec.isa2.restapi.profile.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.isa2.restapi.profile.model.schemas.SchemaDescriptionConstants.*;


@Schema(
        id = MESSAGE_REFERENCE_LIST_ID,
        schema = MESSAGE_REFERENCE_LIST_SCHEMA,
        name = MESSAGE_REFERENCE_LIST_NAME,
        title = MESSAGE_REFERENCE_LIST_TITLE,
        description = MESSAGE_REFERENCE_LIST_DESCRIPTION,
        requiredProperties = {MESSAGE_REFERENCE_LIST_REQ_PROP_MESSAGE_REF_LIST}

)
public class MessageReferenceListRO implements Serializable {

    @Schema(name = "messageReferenceList",
            description = "List of message references.")
    List<MessageReferenceRO> messageReferenceList;
    @Schema(name = "count", description = "Count of all messages", type = "integer")
    Integer count;
    @Schema(name = "limit", description = "limit number of references.", type = "integer")
    Integer limit = -1;
    @Schema(name = "offset", description = "offset of references.", type = "integer")
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
