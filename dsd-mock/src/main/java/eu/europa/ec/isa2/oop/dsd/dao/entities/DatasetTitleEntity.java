package eu.europa.ec.isa2.oop.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractStringEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "DSD_MOCK_DSET_TITLE")
public class DatasetTitleEntity extends AbstractStringEntity {
    @Id
    @GenericGenerator(name = "DSD_MOCK_DSET_TITLE_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_DSET_TITLE_SEQ")
    @Column(name = "ID_PK")
    private Long id;
    @Column(name = "TITLE", length = 255, nullable = false)
    String stringValue;

    @ManyToOne
    @JoinColumn(name = "FK_DATASET_ID", nullable = false)
    DatasetEntity dataset;

    public DatasetTitleEntity() {
    }

    public DatasetTitleEntity(String stringValue, DatasetEntity dataset) {
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
