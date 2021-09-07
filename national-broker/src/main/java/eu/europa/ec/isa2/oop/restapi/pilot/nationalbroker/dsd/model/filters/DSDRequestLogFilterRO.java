package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters;

public class DSDRequestLogFilterRO {

    String queryId;
    String service;
    String action;
    String messageId;

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "DSDRequestLogFilterRO{" +
                "service='" + service + '\'' +
                ", action='" + action + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
