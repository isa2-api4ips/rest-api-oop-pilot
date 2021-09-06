package eu.europa.ec.isa2.oop.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "DSD_MOCK_DIST_DATASERVICE")
public class DistributionDataServiceEntity extends AbstractBaseEntity {
    @Id
    @GenericGenerator(name = "DSD_MOCK_DIST_DATASERVICE_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_DIST_DATASERVICE_SEQ")
    @Column(name = "ID_PK")
    Long id;

    @Column(name = "IDENTIFIER")
    String identifier;
    @Column(name = "CONFORMS_TO")
    String conformsTo;
    @Column(name = "TITLE")
    String title;
    @Column(name = "ENDPOINT_URL")
    String endpointURL;

    @ManyToOne
    @JoinColumn(name = "FK_DISTRIBUTION_ID", nullable = false)
    DatasetDistributionEntity distribution;

    public DistributionDataServiceEntity() {
    }

    public DistributionDataServiceEntity(String identifier, String conformsTo, String title, String endpointURL, DatasetDistributionEntity distribution) {
        this.identifier = identifier;
        this.conformsTo = conformsTo;
        this.title = title;
        this.endpointURL = endpointURL;
        this.distribution = distribution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DatasetDistributionEntity getDistribution() {
        return distribution;
    }

    public void setDistribution(DatasetDistributionEntity distribution) {
        this.distribution = distribution;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(String conformsTo) {
        this.conformsTo = conformsTo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEndpointURL() {
        return endpointURL;
    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }
}
