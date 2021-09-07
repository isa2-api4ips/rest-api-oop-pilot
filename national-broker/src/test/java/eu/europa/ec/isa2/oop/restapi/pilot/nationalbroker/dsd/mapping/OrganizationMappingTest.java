package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.DSDRequestStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.EntityStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.AddressRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.*;

public class OrganizationMappingTest {
    private OrganizationMapping mapper
            = Mappers.getMapper(OrganizationMapping.class);

    @Test
    public void entityToRo() {
        // given
        OrganizationEntity source = new OrganizationEntity();
        source.setIdentifier("Identifier");
        source.setAddress(new AddressEntity());
        source.getAddress().setAdminUnitLevel("AdminUnitLevel");
        source.getAddress().setFullAddress("FullAddress");
        source.getPrefLabels().add(new PrefLabelEntity("PrefLabel1", source));
        source.getPrefLabels().add(new PrefLabelEntity("PrefLabel2", source));
        source.getAltLabels().add(new AltLabelEntity("AltLabel1", source));
        source.getAltLabels().add(new AltLabelEntity("AltLabel2", source));
        source.getClassifications().add(new ClassificationEntity("Classification1", source));
        source.getClassifications().add(new ClassificationEntity("Classification2", source));
        // when
        OrganizationRO destination = mapper.entityToRo(source);
        //then
        assertEquals(source.getIdentifier(), destination.getIdentifier());
        assertEquals(source.getAddress().getAdminUnitLevel(), destination.getAddress().getAdminUnitLevel());
        assertEquals(source.getAddress().getFullAddress(), destination.getAddress().getFullAddress());
        assertArrayEquals(new String[]{"PrefLabel1", "PrefLabel2"}, destination.getPrefLabels().toArray(new String[0]));
        assertArrayEquals(new String[]{"AltLabel1", "AltLabel2"}, destination.getAltLabels().toArray(new String[0]));
        assertArrayEquals(new String[]{"Classification1", "Classification2"}, destination.getClassifications().toArray(new String[0]));
    }

    @Test
    public void roToEntity() {
        // given
        OrganizationRO source = new OrganizationRO();
        source.setIdentifier("Identifier");
        source.setAddress(new AddressRO());
        source.getAddress().setAdminUnitLevel("AdminUnitLevel");
        source.getAddress().setFullAddress("FullAddress");
        source.getPrefLabels().add("PrefLabel1");
        source.getPrefLabels().add("PrefLabel2");
        source.getAltLabels().add("AltLabel1");
        source.getAltLabels().add("AltLabel2");
        source.getClassifications().add("Classification1");
        source.getClassifications().add("Classification2");
        // when
        OrganizationEntity destination = mapper.roToEntity(source);
        //then
        assertEquals(source.getIdentifier(), destination.getIdentifier());
        assertEquals(source.getAddress().getAdminUnitLevel(), destination.getAddress().getAdminUnitLevel());
        assertEquals(source.getAddress().getFullAddress(), destination.getAddress().getFullAddress());
        assertEquals(source.getPrefLabels().size(), destination.getPrefLabels().size());
        assertEquals(source.getAltLabels().size(), destination.getAltLabels().size());
        assertEquals(source.getClassifications().size(), destination.getClassifications().size());
        for (int i = 0; i < source.getPrefLabels().size(); i++) {
            assertEquals(source.getPrefLabels().get(i), destination.getPrefLabels().get(i).getStringValue());
        }
        for (int i = 0; i < source.getAltLabels().size(); i++) {
            assertEquals(source.getAltLabels().get(i), destination.getAltLabels().get(i).getStringValue());
        }
        for (int i = 0; i < source.getClassifications().size(); i++) {
            assertEquals(source.getClassifications().get(i), destination.getClassifications().get(i).getClassification());
        }
    }

    @Test
    public void roToEntityStatus() {
        // given
        OrganizationRO source = new OrganizationRO();
        source.setDsdStatus(DSDRequestStatus.PENDING);
        // when
        OrganizationEntity destination = mapper.roToEntity(source);
        //then
        assertEquals(source.getDsdStatus().name(), destination.getDsdStatus());

    }

    @Test
    public void entityToRoStatus() {
        // given
        OrganizationEntity source = new OrganizationEntity();
        source.setDsdStatus(DSDRequestStatus.PENDING.getStatus());
        // when
        OrganizationRO destination = mapper.entityToRo(source);
        //then
        assertEquals(DSDRequestStatus.PENDING, destination.getDsdStatus());
    }

    @Test
    public void entityToRoStatusInvalidValue() throws IllegalArgumentException {
        // given
        OrganizationEntity source = new OrganizationEntity();
        source.setDsdStatus("Invalid");
        // when
        OrganizationRO destination = mapper.entityToRo(source);
        assertNull(destination.getDsdStatus());

    }
}
