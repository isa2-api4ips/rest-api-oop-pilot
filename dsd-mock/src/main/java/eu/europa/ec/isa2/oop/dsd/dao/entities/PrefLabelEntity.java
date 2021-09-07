package eu.europa.ec.isa2.oop.dsd.dao.entities;


import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractStringEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "DSD_MOCK_ORG_PREFLABEL")
public class PrefLabelEntity extends AbstractStringEntity {
    @Id
    @GenericGenerator(name = "DSD_MOCK_ORG_PREFLABEL_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "DSD_MOCK_ORG_PREFLABEL_SEQ")
    @Column(name = "ID_PK")
    private Long id;
    @Column(name = "PREF_LABEL", length = 255)
    String prefLabel;

    @ManyToOne
    @JoinColumn(name = "FK_ORGANIZATION_ID", nullable = false)
    OrganizationEntity organization;

    public PrefLabelEntity() {
    }

    public PrefLabelEntity(String prefLabel, OrganizationEntity organization) {
        this.prefLabel = prefLabel;
        this.organization = organization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStringValue() {
        return prefLabel;
    }

    public void setLabel(String prefLabel) {
        this.prefLabel = prefLabel;
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
                ", prefLabel='" + prefLabel + '\'' +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PrefLabelEntity altLabel1 = (PrefLabelEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, altLabel1.id)
                .append(prefLabel, altLabel1.prefLabel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(prefLabel)
                .toHashCode();
    }
}
