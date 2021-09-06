package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.AddressEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.AddressRO;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.*;

public class AddressMappingTest {

    private AddressMapping mapper
            = Mappers.getMapper(AddressMapping.class);

    @Test
    public void testRoToEntity() {
        // given
        AddressRO addressRO = new AddressRO();
        addressRO.setFullAddress("FullAddress");
        addressRO.setAdminUnitLevel("AdminUnitLevel");
        // when
        AddressEntity destination = mapper.roToEntity(addressRO);
        //then
        assertEquals(addressRO.getFullAddress(), destination.getFullAddress());
        assertEquals(addressRO.getAdminUnitLevel(),
                destination.getAdminUnitLevel());
    }

    @Test
    public void testEntityToRo() {
        // given
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setFullAddress("FullAddress");
        addressEntity.setAdminUnitLevel("AdminUnitLevel");

        AddressRO destination = mapper.entityToRo(addressEntity);

        assertEquals(addressEntity.getFullAddress(), destination.getFullAddress());
        assertEquals(addressEntity.getAdminUnitLevel(),
                destination.getAdminUnitLevel());
    }
}