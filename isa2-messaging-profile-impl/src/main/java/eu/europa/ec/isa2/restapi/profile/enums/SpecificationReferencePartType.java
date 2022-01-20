package eu.europa.ec.isa2.restapi.profile.enums;

public enum SpecificationReferencePartType {
    COMPONENTS("components"),
    HEADERS("headers"),
    PARAMETERS("parameters"),
    SCHEMAS("schemas");

    String name;

    SpecificationReferencePartType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
