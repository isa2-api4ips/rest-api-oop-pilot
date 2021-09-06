package eu.europa.ec.isa2.oop.dsd.dao.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractStringEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "DSD_MOCK_DATASET")
@NamedQueries({
        @NamedQuery(name = "DatasetEntity.getAll", query = "SELECT d FROM DatasetEntity d"),
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "DatasetEntity.getByCompositeSearch", query = "SELECT d.* FROM DSD_MOCK_DATASET d " +
                " INNER JOIN DSD_MOCK_ORGANIZATION org  ON d.FK_ORGANIZATION_ID = org.ID_PK  " +
                " INNER JOIN DSD_MOCK_ADDRESS adr  ON org.ID_PK = adr.ID_PK  " +
                " where (:organizationIdentifier IS NULL OR :organizationIdentifier='' OR org.IDENTIFIER =:organizationIdentifier)" +
                " AND (:type IS NULL OR :type='' OR d.DATASET_TYPE = :type)" +
                " AND (:country IS NULL OR :country='' OR adr.ADMIN_UNIT_LEVEL = :country)" +
                " ORDER BY :sort",
                resultClass=DatasetEntity.class),
        @NamedNativeQuery(name = "DatasetEntity.getCountByCompositeSearch", query = "SELECT count(d.ID_PK) as cnt FROM DSD_MOCK_DATASET d " +
                " INNER JOIN DSD_MOCK_ORGANIZATION org  ON d.FK_ORGANIZATION_ID = org.ID_PK  " +
                " INNER JOIN DSD_MOCK_ADDRESS adr  ON org.ID_PK = adr.ID_PK  " +
                " where (:organizationIdentifier IS NULL OR :organizationIdentifier='' OR org.IDENTIFIER =:organizationIdentifier)" +
                " AND (:type IS NULL OR :type='' OR d.DATASET_TYPE = :type)" +
                " AND (:country IS NULL OR :country='' OR adr.ADMIN_UNIT_LEVEL = :country)",
                resultSetMapping = "count"),

        @NamedNativeQuery(name = "DatasetEntity.getByOneOfTheIdentifier", query = "SELECT distinct d.* FROM DSD_MOCK_DATASET d " +
                " INNER JOIN DSD_MOCK_DSET_IDENTIFIER ident  ON ident.FK_DATASET_ID = d.ID_PK  " +
                " where ident.IDENTIFIER in (:identifiers)",
                resultClass=DatasetEntity.class
        )


})

@SqlResultSetMapping(name = "count", classes = {
        @ConstructorResult(targetClass = BigInteger.class,
                columns = {@ColumnResult(name = "cnt", type = String.class)})
})

public class DatasetEntity extends AbstractBaseEntity {

    @Id
    @GenericGenerator(name = "DSD_MOCK_DATASET_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_DATASET_SEQ")
    @Column(name = "ID_PK")
    Long id;

    @Column(name = "DATASET_TYPE")
    String type;

    @Column(name = "CONFORMS_TO")
    String conformsTo;

    @OneToMany(mappedBy = "dataset",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    List<DatasetIdentifierEntity> identifiers;

    @OneToMany(mappedBy = "dataset",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    List<DatasetTitleEntity> titles;

    @OneToMany(mappedBy = "dataset",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    List<DatasetDescriptionEntity> descriptions;

    @OneToMany(mappedBy = "dataset",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
        List<DatasetRelationshipEntity> qualifiedRelationshipEntities;

    @OneToMany(mappedBy = "dataset",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    List<DatasetDistributionEntity> distributionEntities;


    @ManyToOne
    @JoinColumn(name = "FK_ORGANIZATION_ID", nullable = false)
    OrganizationEntity publisher;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(String conformsTo) {
        this.conformsTo = conformsTo;
    }

    public OrganizationEntity getPublisher() {
        return publisher;
    }

    public void setPublisher(OrganizationEntity publisher) {
        this.publisher = publisher;
    }

    public List<DatasetIdentifierEntity> getIdentifiers() {
        if (identifiers==null) {
            identifiers = new ArrayList<>();
        }
        return identifiers;
    }

    public List<DatasetTitleEntity> getTitles() {
        if (titles==null) {
            titles = new ArrayList<>();
        }
        return titles;
    }

    public List<DatasetDescriptionEntity> getDescriptions() {
        if (descriptions==null) {
            descriptions = new ArrayList<>();
        }
        return descriptions;
    }

    public List<DatasetRelationshipEntity> getQualifiedRelationships() {
        if (qualifiedRelationshipEntities==null) {
            qualifiedRelationshipEntities = new ArrayList<>();
        }
        return qualifiedRelationshipEntities;
    }

    public List<DatasetDistributionEntity> getDistributions() {
        if (distributionEntities==null) {
            distributionEntities = new ArrayList<>();
        }
        return distributionEntities;
    }
/*
    public <T extends AbstractStringEntity> void  addIdentifier(String value, List<T> list) {

        Optional<T> optional =
                list.stream().filter(identifiers -> StringUtils.equals(value, identifiers.getStringValue())).findFirst();

        if (!optional.isPresent()) {
            list.add(new T(value, this));
        }

    }*/


}
