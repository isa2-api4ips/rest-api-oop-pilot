package eu.europa.ec.isa2.oop.dsd.exceptions;

public class ConfigurationException extends RuntimeException{
    public ConfigurationException() {
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
