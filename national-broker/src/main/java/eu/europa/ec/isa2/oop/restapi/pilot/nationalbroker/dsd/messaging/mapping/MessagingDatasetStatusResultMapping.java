package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DatasetRO;
import eu.europa.eu.dsd.messaging.gen.dataset.model.DatasetStatusResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessagingDatasetStatusResultMapping {

    DatasetStatusResult serverRoToMessaging(eu.europa.eu.dsd.messaging.gen.server.dataset.model.DatasetStatusResult source);
    eu.europa.eu.dsd.messaging.gen.server.dataset.model.DatasetStatusResult messagingToServerRo(DatasetStatusResult source);

}
