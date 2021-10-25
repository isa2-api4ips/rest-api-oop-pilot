package eu.europa.ec.isa2.restapi.profile.enums;

public enum SpecificationPartType {
    CORE_PROFILE("api-core-profile"),
    DOCUMENT_PROFILE("api-documentation"),
    MESSAGING_PROFILE("messaging-api-specification");

    String name;

    SpecificationPartType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
