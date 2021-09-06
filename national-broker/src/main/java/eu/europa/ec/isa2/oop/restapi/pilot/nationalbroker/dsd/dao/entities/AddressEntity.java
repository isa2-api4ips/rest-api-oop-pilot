package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.ColumnDescription;
import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;

import javax.persistence.*;


@Entity
@Table(name = "NB_DSD_ADDRESS")
public class AddressEntity extends AbstractBaseEntity {


    @Id
    @Column(name = "ID_PK")
    @ColumnDescription(comment = "Shared primary key with master table DSD_ORGANIZATION")
    private Long id;

    @Column(name = "ADMIN_UNIT_LEVEL")
    String adminUnitLevel;
    @Column(name = "FULL_ADDRESS")
    String fullAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "ID_PK")
    @MapsId
    OrganizationEntity organization;


    public String getAdminUnitLevel() {
        return adminUnitLevel;
    }

    public void setAdminUnitLevel(String adminUnitLevel) {
        this.adminUnitLevel = adminUnitLevel;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organizationEntity) {
        this.organization = organizationEntity;
    }
}
