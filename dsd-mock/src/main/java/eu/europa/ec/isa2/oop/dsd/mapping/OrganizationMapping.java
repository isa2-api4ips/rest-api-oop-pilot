package eu.europa.ec.isa2.oop.dsd.mapping;


import eu.europa.ec.isa2.oop.dsd.dao.entities.AltLabelEntity;
import eu.europa.ec.isa2.oop.dsd.dao.entities.ClassificationEntity;
import eu.europa.ec.isa2.oop.dsd.dao.entities.OrganizationEntity;
import eu.europa.ec.isa2.oop.dsd.dao.entities.PrefLabelEntity;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationRO;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class OrganizationMapping {
    private AddressMapping addressMapping
            = Mappers.getMapper(AddressMapping.class);

    public OrganizationRO entityToRo(OrganizationEntity source) {
        OrganizationRO destination = new OrganizationRO();
        destination.setIdentifier(source.getIdentifier());
        if (source.getAddress()!=null) {
            destination.setAddress(addressMapping.entityToRo(source.getAddress()));
        }
        source.getAltLabels().forEach(altLabel -> destination.getAltLabels().add(altLabel.getStringValue()));
        source.getPrefLabels().forEach(prefLabel -> destination.getPrefLabels().add(prefLabel.getStringValue()));
        source.getClassifications().forEach(classification -> destination.getClassifications().add(classification.getStringValue()));
        return destination;
    };

    public OrganizationEntity roToEntity(OrganizationRO source){
        OrganizationEntity destination = new OrganizationEntity();
        destination.setIdentifier(source.getIdentifier());
        if (source.getAddress()!=null) {
            destination.setAddress(addressMapping.roToEntity(source.getAddress()));
        }
        source.getAltLabels().forEach(altLabel -> destination.getAltLabels().add(new AltLabelEntity(altLabel, destination)));
        source.getPrefLabels().forEach(prefLabel -> destination.getPrefLabels().add(new PrefLabelEntity(prefLabel,destination)));
        source.getClassifications().forEach(classification -> destination.getClassifications().add(new ClassificationEntity(classification,destination)));
        return destination;
    }
}
