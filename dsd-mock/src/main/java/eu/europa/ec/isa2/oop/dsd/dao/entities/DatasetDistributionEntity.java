package eu.europa.ec.isa2.oop.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DSD_MOCK_DSET_DISTRIBUTION")
public class DatasetDistributionEntity extends AbstractBaseEntity {
    @Id
    @GenericGenerator(name = "DSD_MOCK_DSET_DISTRIBUTION_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_DSET_DISTRIBUTION_SEQ")
    @Column(name = "ID_PK")
    Long id;

    @Column(name = "CONFORMS_TO")
    String conformsTo;
    @Column(name = "FORMAT")
    String format;
    @Column(name = "MEDIA_TYPE")
    String mediaType;
    @Column(name = "ACCESS_URL")
    String accessURL;


    @ManyToOne
    @JoinColumn(name = "FK_DATASET_ID", nullable = false)
    DatasetEntity dataset;

    @OneToMany(mappedBy = "distribution",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    List<DistributionDescriptionEntity> descriptions;

    @OneToMany(mappedBy = "distribution",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    List<DistributionDataServiceEntity> dataServiceEntities;

    public DatasetDistributionEntity() {
    }

    public DatasetDistributionEntity(String conformsTo,
                                     String format,
                                     String mediaType,
                                     String accessURL,
                                     List<DistributionDescriptionEntity> descriptions,
                                     List<DistributionDataServiceEntity> dataServiceEntities,
                                     DatasetEntity dataset) {

        this.conformsTo = conformsTo;
        this.format = format;
        this.mediaType = mediaType;
        this.accessURL = accessURL;
        this.dataset = dataset;
        this.descriptions = descriptions;
        this.dataServiceEntities = dataServiceEntities;

        if (this.descriptions !=null) {
            this.descriptions.forEach(description -> description.setDistribution(this));
        }
        if (this.dataServiceEntities !=null) {
            this.dataServiceEntities.forEach(dataServiceEntity -> dataServiceEntity.setDistribution(this));
        }

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

    public String getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(String conformsTo) {
        this.conformsTo = conformsTo;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public String getAccessURL() {
        return accessURL;
    }

    public void setAccessURL(String accessURL) {
        this.accessURL = accessURL;
    }

    public List<DistributionDescriptionEntity> getDescriptions() {
        if (descriptions==null) {
            descriptions = new ArrayList<>();
        }
        return descriptions;
    }

    public List<DistributionDataServiceEntity> getDataServices() {
        if (dataServiceEntities==null) {
            dataServiceEntities = new ArrayList<>();
        }
        return dataServiceEntities;
    }
}
