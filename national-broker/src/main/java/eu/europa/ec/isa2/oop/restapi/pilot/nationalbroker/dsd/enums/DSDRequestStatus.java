package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

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

    public static DSDRequestStatus getValueByStatus(String status){
        Optional<DSDRequestStatus> result =  Arrays.stream(values()).filter(enumVal -> StringUtils.equalsIgnoreCase(status, enumVal.status)).findFirst();
        return result.isPresent()?result.get():null;
    }
}
