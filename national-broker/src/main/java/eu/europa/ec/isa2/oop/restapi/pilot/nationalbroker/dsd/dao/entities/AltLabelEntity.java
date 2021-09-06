package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractStringEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "NB_DSD_ORG_ALTLABEL")
public class AltLabelEntity extends AbstractStringEntity {
    @Id
    @GenericGenerator(name = "NB_DSD_ORG_ALTLABEL_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "NB_DSD_ORG_ALTLABEL_SEQ")
    @Column(name = "ID_PK")
    private Long id;
    @Column(name = "ALT_LABEL", length = 255)
    String altLabel;

    @ManyToOne
    @JoinColumn(name = "FK_ORGANIZATION_ID", nullable = false)
    OrganizationEntity organization;

    public AltLabelEntity() {
    }

    public AltLabelEntity(String altLabel, OrganizationEntity organization) {
        this.altLabel = altLabel;
        this.organization = organization;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getStringValue() {
        return altLabel;
    }

    public void setLabel(String altLabel) {
        this.altLabel = altLabel;
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
                ", altLabel='" + altLabel + '\'' +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AltLabelEntity altLabel1 = (AltLabelEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, altLabel1.id)
                .append(altLabel, altLabel1.altLabel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(altLabel)
                .toHashCode();
    }
}
