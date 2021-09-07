package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters;

public class OrganizationUpdateFilterRO {
    String queryId;
    String dsdStatus;
    String dsdMessage;
    String updateRequestId;
    String updateResponseId;
    String username;

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getDsdStatus() {
        return dsdStatus;
    }

    public void setDsdStatus(String dsdStatus) {
        this.dsdStatus = dsdStatus;
    }

    public String getDsdMessage() {
        return dsdMessage;
    }

    public void setDsdMessage(String dsdMessage) {
        this.dsdMessage = dsdMessage;
    }

    public String getUpdateRequestId() {
        return updateRequestId;
    }

    public void setUpdateRequestId(String updateRequestId) {
        this.updateRequestId = updateRequestId;
    }

    public String getUpdateResponseId() {
        return updateResponseId;
    }

    public void setUpdateResponseId(String updateResponseId) {
        this.updateResponseId = updateResponseId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
