package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.BaseRO;

import javax.persistence.Column;
import java.util.Date;


public class DSDDataUpdateRO extends BaseRO {

    String dsdStatus;
    String dsdMessage;
    String updateRequestId;
    String updateResponseId;
    String updateRequestOn;
    String updateConfirmedOn;
    String username;
    private String service;
    private String action;
    private String entityType;
    String organizationIdentifier;

    public String getOrganizationIdentifier() {
        return organizationIdentifier;
    }

    public void setOrganizationIdentifier(String organizationIdentifier) {
        this.organizationIdentifier = organizationIdentifier;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setUpdateRequestId(String updateRequestId) {
        this.updateRequestId = updateRequestId;
    }

    public void setUpdateResponseId(String updateResponseId) {
        this.updateResponseId = updateResponseId;
    }

    public void setUpdateRequestOn(String updateRequestOn) {
        this.updateRequestOn = updateRequestOn;
    }

    public void setUpdateConfirmedOn(String updateConfirmedOn) {
        this.updateConfirmedOn = updateConfirmedOn;
    }

    public String getUpdateRequestId() {
        return updateRequestId;
    }

    public String getUpdateResponseId() {
        return updateResponseId;
    }

    public String getUpdateRequestOn() {
        return updateRequestOn;
    }

    public String getUpdateConfirmedOn() {
        return updateConfirmedOn;
    }

    public String getDsdStatus() {
        return dsdStatus;
    }

    public void setDsdStatus(String status) {
        this.dsdStatus = status;
    }

    public String getDsdMessage() {
        return dsdMessage;
    }

    public void setDsdMessage(String dsdMessage) {
        this.dsdMessage = dsdMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
