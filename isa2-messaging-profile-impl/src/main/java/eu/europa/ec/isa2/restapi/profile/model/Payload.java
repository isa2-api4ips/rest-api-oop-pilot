package eu.europa.ec.isa2.restapi.profile.model;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "string", format = "binary")
public class Payload {

    byte[] payload;

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
