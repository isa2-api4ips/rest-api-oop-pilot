package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.AddressRO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessagingAddressMapping {
    AddressRO messagingToRo(eu.europa.eu.dsd.messaging.gen.organization.model.AddressRO source);
    eu.europa.eu.dsd.messaging.gen.organization.model.AddressRO roToMessaging(AddressRO source);
}
