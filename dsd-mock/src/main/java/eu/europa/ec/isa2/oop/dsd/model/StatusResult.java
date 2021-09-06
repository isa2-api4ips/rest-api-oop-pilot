package eu.europa.ec.isa2.oop.dsd.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Joze Rihtarsic
 * @since 1.0
 */
public class StatusResult<T> implements Serializable {

    private static final long serialVersionUID = -4971552086560325302L;
    String description;
    String refMessage;
    String status;
    private T object; //NOSONAR

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRefMessage() {
        return refMessage;
    }

    public void setRefMessage(String refMessage) {
        this.refMessage = refMessage;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
