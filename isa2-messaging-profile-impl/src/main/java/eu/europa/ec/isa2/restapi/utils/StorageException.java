package eu.europa.ec.isa2.restapi.utils;

public class StorageException extends RuntimeException {

    /**
     *
     * @param message
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public StorageException(Throwable cause) {
        super(cause);
    }

}