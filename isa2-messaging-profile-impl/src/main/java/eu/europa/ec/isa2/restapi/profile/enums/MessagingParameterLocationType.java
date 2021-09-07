package eu.europa.ec.isa2.restapi.profile.enums;

public enum MessagingParameterLocationType {
    QUERY("query"),
    HEADER("header"),
    PATH("path"),
    COOKIE("cookie");

    String name;

    MessagingParameterLocationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
