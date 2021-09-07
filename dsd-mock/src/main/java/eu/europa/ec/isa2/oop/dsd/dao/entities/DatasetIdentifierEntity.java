package eu.europa.ec.isa2.oop.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractStringEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table(name = "DSD_MOCK_DSET_IDENTIFIER")
public class DatasetIdentifierEntity extends AbstractStringEntity {
    @Id
    @GenericGenerator(name = "DSD_MOCK_DSET_IDENT_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_DSET_IDENT_SEQ")
    @Column(name = "ID_PK")
    private Long id;

    // each identity must exist only once and for one organization!
    @NaturalId
    @Column(name = "IDENTIFIER", length = 255, nullable = false, unique = true)
    String stringValue;

    @ManyToOne
    @JoinColumn(name = "FK_DATASET_ID", nullable = false)
    DatasetEntity dataset;

    public DatasetIdentifierEntity() {
    }

    public DatasetIdentifierEntity(String stringValue, DatasetEntity dataset) {
        this.stringValue = stringValue;
        this.dataset = dataset;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public DatasetEntity getDataset() {
        return dataset;
    }

    public void setDataset(DatasetEntity dataset) {
        this.dataset = dataset;
    }
}
