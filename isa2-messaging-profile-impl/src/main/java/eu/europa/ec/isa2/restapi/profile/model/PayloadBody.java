package eu.europa.ec.isa2.restapi.profile.model;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(type = "object",description = "Object holds multipart/mixed array of payloads")
public class PayloadBody {

    @ArraySchema(schema = @Schema(type = "string", format = "binary", description = "Array of messaging payloads"))
    List<Payload> payloads;

    public List<Payload> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<Payload> payloads) {
        this.payloads = payloads;
    }


}
