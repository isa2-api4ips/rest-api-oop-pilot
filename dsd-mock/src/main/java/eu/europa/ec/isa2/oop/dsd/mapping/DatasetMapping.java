package eu.europa.ec.isa2.oop.dsd.mapping;


import eu.europa.ec.isa2.oop.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.dsd.model.DatasetRO;
import eu.europa.ec.isa2.oop.dsd.model.DistributionRO;
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
public interface DatasetMapping {
    Logger LOG = LoggerFactory.getLogger(DatasetMapping.class);
    OrganizationMapping organizationMapper = Mappers.getMapper(OrganizationMapping.class);
    DatasetRelationshipMapping relationshipMapper = Mappers.getMapper(DatasetRelationshipMapping.class);
    DatasetDistributionMapping datasetDistributionMapper = Mappers.getMapper(DatasetDistributionMapping.class);


    @Mapping(source = "identifiers", target = "identifiers", qualifiedByName = "identifierListToStringList")
    @Mapping(source = "titles", target = "titles", qualifiedByName = "titleListToStringList")
    @Mapping(source = "descriptions", target = "descriptions", qualifiedByName = "descriptionListToStringList")
    @Mapping(source = "publisher", target = "publisher", qualifiedByName = "publisherEntityToRo")
    @Mapping(source = "qualifiedRelationships", target = "qualifiedRelationships", qualifiedByName = "relationshipEntityListToRoList")
    @Mapping(source = "distributions", target = "distributions", qualifiedByName = "distributionsEntityListToRoList")
    DatasetRO entityToRo(DatasetEntity source);


    @Named("identifierListToStringList")
    default List<String> identifierToString(List<DatasetIdentifierEntity> entityList) {
        return entityList.stream().map(entity -> entity.getStringValue()).collect(Collectors.toList());
    }

    @Named("titleListToStringList")
    default List<String> titleToString(List<DatasetTitleEntity> entityList) {
        return entityList.stream().map(entity -> entity.getStringValue()).collect(Collectors.toList());
    }

    @Named("descriptionListToStringList")
    default List<String> descriptionToString(List<DatasetDescriptionEntity> entityList) {
        return entityList.stream().map(entity -> entity.getStringValue()).collect(Collectors.toList());
    }

    @Named("publisherEntityToRo")
    default OrganizationRO publisherEntityToRo(OrganizationEntity entity) {
        return entity == null ? null : organizationMapper.entityToRo(entity);
    }

    @Named("relationshipEntityListToRoList")
    default List<RelationshipRO> relationshipEntityToRo(List<DatasetRelationshipEntity> entityList) {
        return entityList.stream().map(entity -> relationshipMapper.entityToRo(entity)).collect(Collectors.toList());
    }

    @Named("distributionsEntityListToRoList")
    default List<DistributionRO> distributionEntityToRo(List<DatasetDistributionEntity> entityList) {
        return entityList.stream().map(entity -> datasetDistributionMapper.entityToRo(entity)).collect(Collectors.toList());
    }


}
