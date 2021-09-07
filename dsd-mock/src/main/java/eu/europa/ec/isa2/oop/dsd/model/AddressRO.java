package eu.europa.ec.isa2.oop.dsd.model;

import java.io.Serializable;

public class AddressRO implements Serializable {
    String adminUnitLevel;
    String fullAddress;

    public String getAdminUnitLevel() {
        return adminUnitLevel;
    }

    public void setAdminUnitLevel(String adminUnitLevel) {
        this.adminUnitLevel = adminUnitLevel;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
