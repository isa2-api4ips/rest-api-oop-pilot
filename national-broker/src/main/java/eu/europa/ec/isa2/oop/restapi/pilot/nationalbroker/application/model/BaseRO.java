package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.enums.EntityROStatus;

import java.io.Serializable;

public class BaseRO implements Serializable {
    private int status = EntityROStatus.PERSISTED.getStatusNumber();
    private int index;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
