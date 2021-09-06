package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "NB_DSD_REQUEST_RESPONSE")
@NamedQueries({
        @NamedQuery(name = "RequestResponseEntity.getAll", query = "SELECT d FROM DSDRequestLogEntity d"),
        @NamedQuery(name = "RequestResponseEntity.getById", query = "SELECT d FROM DSDRequestLogEntity d where d.id=:id"),

})
public class DSDRequestLogEntity extends AbstractBaseEntity {

    @Id
    @GenericGenerator(name = "NB_REQ_RESP_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "NB_REQ_RESP_SEQ")
    @Column(name = "ID_PK")
    private Long id;

    @Column(name = "STATUS")
    private String dsdStatus;
    @Column(name = "MESSAGE")
    private String dsdMessage;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "MESSAGE_ID")
    String messageId;
    @Column(name = "RESPONSE_MESSAGE_ID")
    String responseMessageId;

    @Column(name = "UPDATE_REQ_ON")
    Date requestOn;
    @Column(name = "UPDATE_CONF_ON")
    Date responseOn;

    @Column(name = "REQUEST_STORAGE_PATH")
    String requestStoragePath;
    @Column(name = "RESPONSE_STORAGE_PATH")
    String responseStoragePath;

    @Column(name = "USERNAME")
    String username;

    @Column(name = "SERVICE")
    String service;
    @Column(name = "ACTION")
    String action;

    @Column(name = "HTTP_PATH")
    String httpPath;

    @Column(name = "HTTP_METHOD")
    String httpMethod;


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

    public void setMessageId(String updateRequestId) {
        this.messageId = updateRequestId;
    }

    public void setResponseMessageId(String updateResponseId) {
        this.responseMessageId = updateResponseId;
    }

    public void setRequestOn(Date updateRequestOn) {
        this.requestOn = updateRequestOn;
    }

    public void setResponseOn(Date updateConfirmedOn) {
        this.responseOn = updateConfirmedOn;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getResponseMessageId() {
        return responseMessageId;
    }

    public Date getRequestOn() {
        return requestOn;
    }

    public Date getResponseOn() {
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

    public String getRequestStoragePath() {
        return requestStoragePath;
    }

    public void setRequestStoragePath(String requestPath) {
        this.requestStoragePath = requestPath;
    }

    public String getResponseStoragePath() {
        return responseStoragePath;
    }

    public void setResponseStoragePath(String responsePath) {
        this.responseStoragePath = responsePath;
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

        DSDRequestLogEntity that = (DSDRequestLogEntity) o;

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
