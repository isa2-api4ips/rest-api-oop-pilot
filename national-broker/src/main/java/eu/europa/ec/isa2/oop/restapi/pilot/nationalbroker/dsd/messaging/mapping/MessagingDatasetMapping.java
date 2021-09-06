package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DatasetRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessagingDatasetMapping {

    DatasetRO messagingToRo(eu.europa.eu.dsd.messaging.gen.dataset.model.DatasetRO source);
    eu.europa.eu.dsd.messaging.gen.dataset.model.DatasetRO roToMessaging(DatasetRO source);

}
