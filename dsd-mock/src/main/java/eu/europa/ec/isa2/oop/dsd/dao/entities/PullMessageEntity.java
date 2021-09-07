package eu.europa.ec.isa2.oop.dsd.dao.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DSD_PULL_MESSAGE")
@NamedQueries({
        @NamedQuery(name = "PullMessageEntity.getById", query = "SELECT d FROM PullMessageEntity d where d.id=:id"),
        @NamedQuery(name = "PullMessageEntity.getByIdentifier", query = "SELECT d FROM PullMessageEntity d where d.identifier=:identifier")

})
public class PullMessageEntity extends AbstractBaseEntity {

    @Id
    @GenericGenerator(name = "DSD_PULL_MESSAGE_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_PULL_MESSAGE_SEQ")
    @Column(name = "ID_PK")
    private Long id;

    @Column(name = "IDENTIFIER")
    private String identifier;

    @Column(name = "SERVICE")
    private String service;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "REF_IDENTIFIER")
    private String refIdentifier;

    @Column(name = "REF_SERVICE")
    private String refService;

    @Column(name = "REF_ACTION")
    private String refAction;

    @Column(name = "STATUS")
    private String status;

    @OneToMany(mappedBy = "pullMessage",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<PullMessagePayloadEntity> payloads;

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

    public String getRefIdentifier() {
        return refIdentifier;
    }

    public void setRefIdentifier(String refIdentifier) {
        this.refIdentifier = refIdentifier;
    }

    public String getRefService() {
        return refService;
    }

    public void setRefService(String refService) {
        this.refService = refService;
    }

    public String getRefAction() {
        return refAction;
    }

    public void setRefAction(String refAction) {
        this.refAction = refAction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PullMessagePayloadEntity> getPayloads() {
        if(payloads ==null) {
            payloads = new ArrayList<>();
        }
        return payloads;
    }

    public void addPayload(PullMessagePayloadEntity payload) {
        getPayloads().add(payload);
        payload.setPullMessage(this);
    }
}
