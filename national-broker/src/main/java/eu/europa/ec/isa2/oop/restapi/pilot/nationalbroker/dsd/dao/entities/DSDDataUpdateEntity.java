package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "NB_DSD_UPDATE_DATA")
@NamedQueries({
        @NamedQuery(name = "OrganizationUpdateEntity.getAll", query = "SELECT d FROM DSDDataUpdateEntity d"),
        @NamedQuery(name = "OrganizationUpdateEntity.getById", query = "SELECT d FROM DSDDataUpdateEntity d where d.id=:id"),
        @NamedQuery(name = "OrganizationUpdateEntity.getByRequestIdentifier", query = "SELECT d FROM DSDDataUpdateEntity d where d.updateRequestId=:requestId")

})
public class DSDDataUpdateEntity extends AbstractBaseEntity {

    @Id
    @GenericGenerator(name = "NB_DSD_UPDATE_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "NB_DSD_UPDATE_SEQ")
    @Column(name = "ID_PK")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_ORGANIZATION_ID", nullable = true)
    OrganizationEntity organization;

    @Column(name = "SERVICE")
    private String service;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "ENTITY_TYPE")
    private String entityType;

    @Column(name = "STATUS")
    private String dsdStatus;
    @Column(name = "MESSAGE")
    private String dsdMessage;

    @Column(name = "UPDATE_REQ_ID")
    String updateRequestId;
    @Column(name = "UPDATE_RES_ID")
    String updateResponseId;

    @Column(name = "UPDATE_REQ_ON")
    Date updateRequestOn;
    @Column(name = "UPDATE_CONF_ON")
    Date updateConfirmedOn;

    @Column(name = "USERNAME")
    String username;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public void setUpdateRequestId(String updateRequestId) {
        this.updateRequestId = updateRequestId;
    }

    public void setUpdateResponseId(String updateResponseId) {
        this.updateResponseId = updateResponseId;
    }

    public void setUpdateRequestOn(Date updateRequestOn) {
        this.updateRequestOn = updateRequestOn;
    }

    public void setUpdateConfirmedOn(Date updateConfirmedOn) {
        this.updateConfirmedOn = updateConfirmedOn;
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

    public String getUpdateRequestId() {
        return updateRequestId;
    }

    public String getUpdateResponseId() {
        return updateResponseId;
    }

    public Date getUpdateRequestOn() {
        return updateRequestOn;
    }

    public Date getUpdateConfirmedOn() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DSDDataUpdateEntity that = (DSDDataUpdateEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, that.id)
                .append(organization, that.organization)
                .append(dsdStatus, that.dsdStatus)
                .append(dsdMessage, that.dsdMessage)
                .append(updateRequestId, that.updateRequestId)
                .append(updateResponseId, that.updateResponseId)
                .append(updateRequestOn, that.updateRequestOn)
                .append(updateConfirmedOn, that.updateConfirmedOn)
                .append(username, that.username)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organization)
                .append(dsdStatus)
                .append(dsdMessage)
                .append(updateRequestId)
                .append(updateResponseId)
                .append(updateRequestOn)
                .append(updateConfirmedOn)
                .append(username)
                .toHashCode();
    }
}
