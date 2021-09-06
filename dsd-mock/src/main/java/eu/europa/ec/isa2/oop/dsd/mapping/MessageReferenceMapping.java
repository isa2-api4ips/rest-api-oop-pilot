package eu.europa.ec.isa2.oop.dsd.mapping;


import eu.europa.ec.isa2.oop.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationRO;
import eu.europa.ec.isa2.restapi.profile.model.MessageReferenceRO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

@Mapper(componentModel = "spring")
public abstract class MessageReferenceMapping {
    public MessageReferenceRO pullMessageToMessageReference(PullMessageEntity source) {
        MessageReferenceRO referenceRO = new MessageReferenceRO();
        referenceRO.setAction(source.getAction());
        referenceRO.setService(source.getService());
        referenceRO.setMessageId(source.getIdentifier());
        referenceRO.setHref("/v1/messaging/" + source.getService() + "/"+ source.getAction() + "/" + source.getIdentifier());
        return referenceRO;
    };
    public MessageReferenceRO pullMessageToResponseMessageReference(PullMessageEntity source) {
        MessageReferenceRO referenceRO = new MessageReferenceRO();
        referenceRO.setAction(source.getAction());
        referenceRO.setService(source.getService());
        referenceRO.setMessageId(source.getIdentifier());
        referenceRO.setHref("/v1/messaging/" + source.getRefService() + "/"+ source.getRefAction() + "/" + source.getRefIdentifier() +"/response/"+ source.getService() + "/"+ source.getAction() + "/" + source.getIdentifier());
        return referenceRO;
    };


}
