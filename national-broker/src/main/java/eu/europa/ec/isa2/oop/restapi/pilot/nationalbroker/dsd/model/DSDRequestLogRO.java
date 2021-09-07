package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.BaseRO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;

public class DSDRequestLogRO extends BaseRO {

    private Long id;
    private String dsdMessage;
    private String role;
    String messageId;
    String responseMessageId;
    String requestOn;
    String responseOn;
    String requestStoragePath;
    String responseStoragePath;
    String username;
    String dsdStatus;
    String service;
    String action;
    String httpPath;
    String httpMethod;

    DSDMessageRO requestMessage;
    DSDMessageRO responseMessage;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setResponseMessageId(String responseMessageId) {
        this.responseMessageId = responseMessageId;
    }

    public void setRequestOn(String requestOn) {
        this.requestOn = requestOn;
    }

    public void setResponseOn(String responseOn) {
        this.responseOn = responseOn;
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

    public String getResponseMessageId() {
        return responseMessageId;
    }

    public String getRequestOn() {
        return requestOn;
    }

    public String getResponseOn() {
        return responseOn;
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

    public String getRequestStoragePath() {
        return requestStoragePath;
    }

    public void setRequestStoragePath(String requestStoragePath) {
        this.requestStoragePath = requestStoragePath;
    }

    public String getResponseStoragePath() {
        return responseStoragePath;
    }

    public void setResponseStoragePath(String responseStoragePath) {
        this.responseStoragePath = responseStoragePath;
    }

    public DSDMessageRO getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(DSDMessageRO requestMessage) {
        this.requestMessage = requestMessage;
    }

    public DSDMessageRO getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(DSDMessageRO responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DSDRequestLogRO that = (DSDRequestLogRO) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, that.id)
                .append(dsdStatus, that.dsdStatus)
                .append(dsdMessage, that.dsdMessage)
                .append(messageId, that.messageId)
                .append(responseMessageId, that.responseMessageId)
                .append(requestOn, that.requestOn)
                .append(responseOn, that.responseOn)
                .append(username, that.username)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(dsdStatus)
                .append(dsdMessage)
                .append(messageId)
                .append(responseMessageId)
                .append(requestOn)
                .append(responseOn)
                .append(username)
                .toHashCode();
    }
}
