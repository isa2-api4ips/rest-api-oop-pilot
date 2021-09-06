package eu.europa.ec.isa2.oop.dsd.mapping;


import eu.europa.ec.isa2.oop.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.dsd.model.DatasetRO;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationRO;
import eu.europa.ec.isa2.oop.dsd.model.RelationshipRO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DatasetRelationshipMapping {


    RelationshipRO entityToRo(DatasetRelationshipEntity source);
    DatasetRelationshipEntity roToEntity(RelationshipRO source);




}
