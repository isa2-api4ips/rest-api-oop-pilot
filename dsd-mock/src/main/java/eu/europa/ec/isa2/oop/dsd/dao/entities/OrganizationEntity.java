package eu.europa.ec.isa2.oop.dsd.dao.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DSD_MOCK_ORGANIZATION")
@NamedQueries({
        @NamedQuery(name = "OrganizationEntity.getAll", query = "SELECT d FROM OrganizationEntity d"),
        @NamedQuery(name = "OrganizationEntity.getById", query = "SELECT d FROM OrganizationEntity d where d.id=:id"),
        @NamedQuery(name = "OrganizationEntity.getByIdentifier", query = "SELECT d FROM OrganizationEntity d where d.identifier=:identifier")
})
public class OrganizationEntity extends AbstractBaseEntity {

    @Id
    @GenericGenerator(name = "DSD_MOCK_ORG_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_ORG_SEQ")
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
    @OneToOne(mappedBy = "organization",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    AddressEntity address;

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

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
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


}
