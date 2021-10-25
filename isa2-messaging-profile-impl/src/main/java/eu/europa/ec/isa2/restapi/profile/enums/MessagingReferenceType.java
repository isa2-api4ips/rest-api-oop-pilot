package eu.europa.ec.isa2.restapi.profile.enums;

import static eu.europa.ec.isa2.restapi.profile.enums.SpecificationPartType.CORE_PROFILE;
import static eu.europa.ec.isa2.restapi.profile.enums.SpecificationPartType.MESSAGING_PROFILE;

public enum MessagingReferenceType {

    ORIGINAL_SENDER("Original-Sender", MESSAGING_PROFILE,"original-sender.json"),
    FINAL_RECIPIENT("Final-Recipient", MESSAGING_PROFILE,"final-recipient.json"),
    TIMESTAMP("Timestamp", MESSAGING_PROFILE,"timestamp.json"),
    EDEL_MESSAGE_SIG("Edel-Message-Sig", CORE_PROFILE,"edel-message-sig.json"),
    EDEL_PAYLOAD_SIG("Edel-Payload-Sig", CORE_PROFILE,"edel-payload-sig.json"),
    RESPONSE_WEBHOOK("Response-Webhook", MESSAGING_PROFILE,"response-webhook.json"),
    SIGNAL_WEBHOOK("Signal-Webhook", MESSAGING_PROFILE,"signal-webhook.json"),
    MESSAGE_ID("messageId", MESSAGING_PROFILE,"message-id.json"),
    SERVICE("service", MESSAGING_PROFILE,"service.json"),
    ACTION("action", MESSAGING_PROFILE,"action.json"),
    RESPONSE_MESSAGE_ID("rMessageId", MESSAGING_PROFILE,"response-message-id.json"),
    RESPONSE_SERVICE("rService", MESSAGING_PROFILE,"response-service.json"),
    RESPONSE_ACTION("rAction", MESSAGING_PROFILE,"response-action.json"),
    PROBLEM("Problem", CORE_PROFILE,"problem.json"),
    SIGNAL_MESSAGE("SignalMessage", MESSAGING_PROFILE,"signal-message.json"),
    MESSAGE_REFERENCE("MessageReference", MESSAGING_PROFILE,"message-reference.json"),
    MESSAGE_REFERENCE_LIST("MessageReferenceList", MESSAGING_PROFILE,"message-reference-list.json"),
    JWS_COMPACT("jws-compact", MESSAGING_PROFILE,"jws-compact.json"),
    JWS_COMPACT_DETACHED("jws-compact-detached", CORE_PROFILE,"jws-compact-detached.json");



    String name;
    SpecificationPartType specificationPart;
    String objectURIDefinition;
    MessagingReferenceType(String name, SpecificationPartType specificationPart, String definitionSubContext
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

    public SpecificationPartType getSpecificationPart() {
        return specificationPart;
    }

}
