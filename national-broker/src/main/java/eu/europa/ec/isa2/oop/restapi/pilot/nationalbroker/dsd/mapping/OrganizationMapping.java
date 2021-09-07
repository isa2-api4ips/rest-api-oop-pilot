package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.AltLabelEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.ClassificationEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.OrganizationEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.PrefLabelEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.DSDRequestStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.EntityStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.text.SimpleDateFormat;

@Mapper(componentModel = "spring")
public abstract class OrganizationMapping {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private AddressMapping addressMapping
            = Mappers.getMapper(AddressMapping.class);

    private DSDDataUpdateMapping DSDDataUpdateMapping
            = Mappers.getMapper(DSDDataUpdateMapping.class);


    public OrganizationRO entityToRo(OrganizationEntity source) {
        OrganizationRO destination = new OrganizationRO();
        destination.setIdentifier(source.getIdentifier());

        if (source.getAddress()!=null) {
            destination.setAddress(addressMapping.entityToRo(source.getAddress()));
        }

        source.getUpdateRequests().forEach(request ->  destination.getUpdateRequests().add(DSDDataUpdateMapping.entityToRo(request)));
        source.getAltLabels().forEach(altLabel -> destination.getAltLabels().add(altLabel.getStringValue()));
        source.getPrefLabels().forEach(prefLabel -> destination.getPrefLabels().add(prefLabel.getStringValue()));
        source.getClassifications().forEach(classification -> destination.getClassifications().add(classification.getClassification()));

        if (source.getDsdStatus()!=null) {
            destination.setDsdStatus(DSDRequestStatus.getValueByStatus(source.getDsdStatus()));
        }
        return destination;
    };

    public OrganizationEntity roToEntity(OrganizationRO source){
        OrganizationEntity destination = new OrganizationEntity();
        destination.setIdentifier(source.getIdentifier());


        if (source.getAddress()!=null) {
            destination.setAddress(addressMapping.roToEntity(source.getAddress()));
        }
        source.getUpdateRequests().forEach(request ->  destination.getUpdateRequests().add(DSDDataUpdateMapping.roToEntity(request)));

        source.getAltLabels().forEach(altLabel -> destination.getAltLabels().add(new AltLabelEntity(altLabel, destination)));
        source.getPrefLabels().forEach(prefLabel -> destination.getPrefLabels().add(new PrefLabelEntity(prefLabel,destination)));
        source.getClassifications().forEach(classification -> destination.getClassifications().add(new ClassificationEntity(classification,destination)));

        destination.setDsdStatus(source.getDsdStatus()==null? EntityStatus.OK.name() :source.getDsdStatus().name());
        return destination;
    }
}
