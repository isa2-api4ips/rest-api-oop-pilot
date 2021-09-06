package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.enums;

public enum EntityROStatus {
    PERSISTED(0),
    UPDATED(1),
    NEW(2),
    REMOVE(3);

    int statusNumber;

    EntityROStatus(int statusNumber) {
        this.statusNumber = statusNumber;
    }

    public int getStatusNumber() {
        return statusNumber;
    }
}
