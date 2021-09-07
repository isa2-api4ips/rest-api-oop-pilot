package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "NB_DSD_ORGANIZATION")
@NamedQueries({
        @NamedQuery(name = "OrganizationEntity.getAll", query = "SELECT d FROM OrganizationEntity d"),
        @NamedQuery(name = "OrganizationEntity.getById", query = "SELECT d FROM OrganizationEntity d where d.id=:id"),
        @NamedQuery(name = "OrganizationEntity.getByIdentifier", query = "SELECT d FROM OrganizationEntity d where d.identifier=:identifier")
})
public class OrganizationEntity extends AbstractBaseEntity {

    @Id
    @GenericGenerator(name = "NB_DSD_ORG_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "NB_DSD_ORG_SEQ")
    @Column(name = "ID_PK")
    private Long id;

    @NaturalId
    @Column(name = "IDENTIFIER")
    private String identifier;

    @OneToMany(mappedBy = "organization",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<PrefLabelEntity> prefLabels;
    @OneToMany(mappedBy = "organization",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    List<AltLabelEntity> altLabels;
    @OneToMany(mappedBy = "organization",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    List<ClassificationEntity> classifications;
    @OneToMany(mappedBy = "organization",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @OrderBy("id DESC")
    List<DSDDataUpdateEntity> updateRequests;

    @OneToOne(mappedBy = "organization",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    AddressEntity address;
    @Column(name = "DSD_STATUS")
    private String dsdStatus;
    @Column(name = "DSD_MESSAGE")
    private String dsdMessage;

    @Column(name = "DSD_UPDATE_REQ_ID")
    String updateRequestId;
    @Column(name = "DSD_UPDATE_RES_ID")
    String updateResponseId;

    @Column(name = "DSD_UPDATE_REQ_ON")
    Date updateRequestOn;
    @Column(name = "DSD_UPDATE_CONF_ON")
    Date updateConfirmedOn;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
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

    public List<PrefLabelEntity> getPrefLabels() {
        if (prefLabels==null) {
            prefLabels = new ArrayList<>();
        }
        return prefLabels;
    }


    public List<AltLabelEntity> getAltLabels() {
        if (altLabels==null) {
            altLabels = new ArrayList<>();
        }
        return altLabels;
    }

    public List<ClassificationEntity> getClassifications() {
        if (classifications==null) {
            classifications = new ArrayList<>();
        }
        return classifications;
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

    public List<DSDDataUpdateEntity> getUpdateRequests() {
        if (updateRequests==null) {
            updateRequests = new ArrayList<>();
        }
        return updateRequests;
    }

    public void setUpdateRequests(List<DSDDataUpdateEntity> updateRequests) {
        this.updateRequests = updateRequests;
    }
}
