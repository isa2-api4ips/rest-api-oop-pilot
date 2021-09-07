package eu.europa.ec.isa2.oop.dsd.dao.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DSD_PULL_MESSAGE_PAYLOAD")
@NamedQueries({
        @NamedQuery(name = "PullMessagePayloadEntity.getById", query = "SELECT d FROM PullMessagePayloadEntity d where d.id=:id"),

})
public class PullMessagePayloadEntity extends AbstractBaseEntity {

    @Id
    @GenericGenerator(name = "DSD_PULL_MESSAGE_PAYLOAD_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_PULL_MESSAGE_PAYLOAD_SEQ")
    @Column(name = "ID_PK")
    private Long id;

    @Column(name = "NAME")
    private String name;


    @Column(name = "MIME_TPYE")
    private String mimeType;

    @Column(name = "PATH")
    private String path;

    @ManyToOne
    @JoinColumn(name = "FK_PULL_MESSAGE_ID", nullable = false)
    PullMessageEntity pullMessage;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PullMessageEntity getPullMessage() {
        return pullMessage;
    }

    public void setPullMessage(PullMessageEntity messageEntity) {
        this.pullMessage = messageEntity;
    }

}
