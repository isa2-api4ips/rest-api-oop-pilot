package eu.europa.ec.isa2.oop.restapi.controller.profile.controllers;

import java.io.Serializable;

public class OriginalSenderTokenSimplePayload implements Serializable {
    String originalSender;

    public OriginalSenderTokenSimplePayload(String originalSender) {
        this.originalSender = originalSender;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }
}
