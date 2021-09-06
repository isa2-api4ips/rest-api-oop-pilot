package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.AddressEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.AddressRO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AddressMapping {
    AddressRO entityToRo(AddressEntity source);
    AddressEntity roToEntity(AddressRO source);
}


