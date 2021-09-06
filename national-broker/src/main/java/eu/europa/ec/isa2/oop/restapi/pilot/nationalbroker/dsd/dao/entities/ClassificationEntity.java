package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "NB_DSD_ORG_CLASSIFICATION")
public class ClassificationEntity extends AbstractBaseEntity {
    @Id
    @GenericGenerator(name = "NB_DSD_ORG_CLASS_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "NB_DSD_ORG_CLASS_SEQ")
    @Column(name = "ID_PK")
    private Long id;
    @Column(name = "CLASSIFICATION", length = 255)
    String classification;

    @ManyToOne
    @JoinColumn(name = "FK_ORGANIZATION_ID", nullable = false)
    OrganizationEntity organization;

    public ClassificationEntity() {
    }

    public ClassificationEntity(String classification, OrganizationEntity organization) {
        this.classification = classification;
        this.organization = organization;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return "AltLabel{" +
                "id=" + id +
                ", classification='" + classification + '\'' +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ClassificationEntity object = (ClassificationEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, object.id)
                .append(classification, object.classification)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(classification)
                .toHashCode();
    }
}
