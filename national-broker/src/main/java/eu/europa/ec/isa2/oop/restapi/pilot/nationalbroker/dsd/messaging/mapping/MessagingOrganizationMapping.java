package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.AltLabelEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.ClassificationEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.OrganizationEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.PrefLabelEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.EntityStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping.AddressMapping;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MessagingOrganizationMapping{

    OrganizationRO messagingToRo(eu.europa.eu.dsd.messaging.gen.organization.model.OrganizationRO source);
    eu.europa.eu.dsd.messaging.gen.organization.model.OrganizationRO roToMessaging(OrganizationRO source);

}
