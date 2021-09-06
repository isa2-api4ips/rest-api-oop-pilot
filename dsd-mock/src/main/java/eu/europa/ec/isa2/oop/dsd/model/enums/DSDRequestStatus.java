package eu.europa.ec.isa2.oop.dsd.model.enums;

public enum DSDRequestStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    REJECTED("Rejected");

   String status;

    DSDRequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
