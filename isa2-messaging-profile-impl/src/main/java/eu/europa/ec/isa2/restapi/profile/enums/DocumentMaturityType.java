package eu.europa.ec.isa2.restapi.profile.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(implementation = DocumentMaturityType.class)
public enum DocumentMaturityType {
    DEVELOPMENT("development"),
    SUPPORTED("supported"),
    DEPRECATED("deprecated");

    private String value;

    DocumentMaturityType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
