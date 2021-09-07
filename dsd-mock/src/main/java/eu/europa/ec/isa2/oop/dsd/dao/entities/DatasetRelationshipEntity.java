package eu.europa.ec.isa2.oop.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "DSD_MOCK_DSET_RELATIONSHIP")
public class DatasetRelationshipEntity extends AbstractBaseEntity {
    @Id
    @GenericGenerator(name = "DSD_MOCK_DSET_RELATIONSHIP_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_DSET_RELATIONSHIP_SEQ")
    @Column(name = "ID_PK")
    Long id;

    @Column(name = "RELATION")
    String relation;

    @Column(name = "HADROLE")
    String hadRole;


    @ManyToOne
    @JoinColumn(name = "FK_DATASET_ID", nullable = false)
    DatasetEntity dataset;

    public DatasetRelationshipEntity() {
        // fixed - initial value according to specs..
        //https://ec.europa.eu/cefdigital/wiki/display/SDGOO/FOR+REVIEW+-+Data+Services+Directory
        hadRole ="https://toop.eu/dataset/supportedIdScheme";
    }

    public DatasetRelationshipEntity(String relation, String hadRole, DatasetEntity dataset) {
        this.relation = relation;
        this.hadRole = hadRole;
        this.dataset = dataset;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DatasetEntity getDataset() {
        return dataset;
    }

    public void setDataset(DatasetEntity dataset) {
        this.dataset = dataset;
    }

    public String getHadRole() {

        return hadRole;
    }

    public void setHadRole(String hadRole) {
        this.hadRole = hadRole;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
