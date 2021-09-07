package eu.europa.ec.isa2.restapi.profile.docsapi.exceptions;

import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;

public class MessagingAPIException extends RuntimeException {

    String details;
    String instance;
    APIProblemType type;

    public MessagingAPIException(APIProblemType type, String details, String instance) {
        this(type, details, instance, null);
    }

    public MessagingAPIException(APIProblemType type, String details, String instance, Throwable throwable) {
        super(details, throwable);
        this.details = details;
        this.instance = instance;
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public String getInstance() {
        return instance;
    }

    public APIProblemType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MessagingAPIException{" +
                "details='" + details + '\'' +
                ", instance='" + instance + '\'' +
                ", type=" + type +
                '}';
    }
}
