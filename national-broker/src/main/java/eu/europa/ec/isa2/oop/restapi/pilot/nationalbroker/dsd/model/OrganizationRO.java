package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model;


import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.BaseRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.DSDRequestStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.EntityStatus;

import java.util.ArrayList;
import java.util.List;

public class OrganizationRO extends BaseRO {

    String identifier;
    List<String> prefLabels;
    List<String> altLabels;
    List<String> classifications;
    List<DSDDataUpdateRO>  updateRequests;
    AddressRO addressRO;
    DSDRequestStatus dsdStatus;
    String dsdMessage;



    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public AddressRO getAddress() {
        return addressRO;
    }

    public void setAddress(AddressRO addressRO) {
        this.addressRO = addressRO;
    }


    public List<DSDDataUpdateRO> getUpdateRequests() {
        if (updateRequests == null){
            updateRequests = new ArrayList<>();
        }
        return updateRequests;
    }

    public List<String> getPrefLabels() {
        if (prefLabels == null){
            prefLabels = new ArrayList<>();
        }
        return prefLabels;
    }


    public List<String> getAltLabels() {
        if (altLabels == null){
            altLabels = new ArrayList<>();
        }
        return altLabels;
    }


    public List<String> getClassifications() {
        if (classifications == null){
            classifications = new ArrayList<>();
        }
        return classifications;
    }

    public DSDRequestStatus getDsdStatus() {
        return dsdStatus;
    }

    public void setDsdStatus(DSDRequestStatus dsdStatus) {
        this.dsdStatus = dsdStatus;
    }

    public String getDsdMessage() {
        return dsdMessage;
    }

    public void setDsdMessage(String dsdMessage) {
        this.dsdMessage = dsdMessage;
    }
}
