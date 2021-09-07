package eu.europa.ec.isa2.oop.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractStringEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "DSD_MOCK_DIST_DESCRIPTION")
public class DistributionDescriptionEntity extends AbstractStringEntity {
    @Id
    @GenericGenerator(name = "DSD_MOCK_DIST_DESC_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_DIST_DESC_SEQ")
    @Column(name = "ID_PK")
    private Long id;
    @Column(name = "DESCRIPTION", length = 255, nullable = false)
    String stringValue;

    @ManyToOne
    @JoinColumn(name = "FK_DISTRIBUTION_ID", nullable = false)
    DatasetDistributionEntity distribution;

    public DistributionDescriptionEntity() {
    }

    public DistributionDescriptionEntity(String stringValue, DatasetDistributionEntity distribution) {
        this.stringValue = stringValue;
        this.distribution = distribution;
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

    public DatasetDistributionEntity getDistribution() {
        return distribution;
    }

    public void setDistribution(DatasetDistributionEntity distribution) {
        this.distribution = distribution;
    }
}
