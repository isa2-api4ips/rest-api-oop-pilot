package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model;


import java.io.Serializable;

public class OriginalSenderTokenSimplePayloadRO implements Serializable {
    String originalSender;

    public OriginalSenderTokenSimplePayloadRO(String originalSender) {
        this.originalSender = originalSender;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }
}