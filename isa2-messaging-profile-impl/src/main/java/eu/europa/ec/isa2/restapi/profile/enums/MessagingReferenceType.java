package eu.europa.ec.isa2.restapi.profile.enums;

public enum MessagingReferenceType {

    ORIGINAL_SENDER("Original-Sender", "messaging-api-specification","original-sender.json"),
    FINAL_RECIPIENT("Final-Recipient", "messaging-api-specification","final-recipient.json"),
    TIMESTAMP("Timestamp", "messaging-api-specification","timestamp.json"),
    EDEL_MESSAGE_SIG("Edel-Message-Sig", "api-core-profile","edel-message-sig.json"),
    EDEL_PAYLOAD_SIG("Edel-Payload-Sig", "api-core-profile","edel-payload-sig.json"),
    RESPONSE_WEBHOOK("Response-Webhook", "messaging-api-specification","response-webhook.json"),
    SIGNAL_WEBHOOK("Signal-Webhook","messaging-api-specification","signal-webhook.json"),
    MESSAGE_ID("messageId", "messaging-api-specification","message-id.json"),
    SERVICE("Service","messaging-api-specification","service.json"),
    ACTION("Action","messaging-api-specification","action.json"),
    RESPONSE_MESSAGE_ID("rMessageId","messaging-api-specification","response-message-id.json"),
    RESPONSE_SERVICE("rService","messaging-api-specification","response-service.json"),
    RESPONSE_ACTION("rAction", "messaging-api-specification","response-action.json"),
    PROBLEM("Problem", "api-core-profile","problem.json"),
    SIGNAL_MESSAGE("SignalMessage", "messaging-api-specification","signal-message.json"),
    MESSAGE_REFERENCE("MessageReference", "messaging-api-specification","message-reference.json"),
    MESSAGE_REFERENCE_LIST("MessageReferenceList", "messaging-api-specification","message-reference-list.json");


    String name;
    String specificationPart;
    String objectURIDefinition;
    MessagingReferenceType(String name, String specificationPart, String definitionSubContext
    ) {
        this.name = name;
        this.objectURIDefinition = definitionSubContext;
        this.specificationPart=specificationPart;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectURIDefinition() {
        return objectURIDefinition;
    }

    public String getSpecificationPart() {
        return specificationPart;
    }
}
