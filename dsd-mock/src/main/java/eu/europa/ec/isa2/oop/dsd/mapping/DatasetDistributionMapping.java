package eu.europa.ec.isa2.oop.dsd.mapping;


import eu.europa.ec.isa2.oop.dsd.dao.entities.DatasetDistributionEntity;
import eu.europa.ec.isa2.oop.dsd.dao.entities.DistributionDataServiceEntity;
import eu.europa.ec.isa2.oop.dsd.dao.entities.DistributionDescriptionEntity;
import eu.europa.ec.isa2.oop.dsd.model.DataServiceRO;
import eu.europa.ec.isa2.oop.dsd.model.DistributionRO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DatasetDistributionMapping {
    DistributionDataServiceMapping distributionDataServiceMapper = Mappers.getMapper(DistributionDataServiceMapping.class);

    @Mapping(source = "descriptions", target = "descriptions", qualifiedByName = "descriptionListToStringList")
    @Mapping(source = "dataServices", target = "dataServices", qualifiedByName = "dataServiceEntityListToRoList")
    DistributionRO entityToRo(DatasetDistributionEntity source);

    //DatasetDistributionEntity roToEntity(DistributionRO source);


    @Named("descriptionListToStringList")
    default List<String> descriptionToString(List<DistributionDescriptionEntity> entityList) {
        return entityList.stream().map(entity -> entity.getStringValue()).collect(Collectors.toList());
    }


    @Named("dataServiceEntityListToRoList")
    default List<DataServiceRO> relationshipEntityToRo(List<DistributionDataServiceEntity> entityList) {
        return entityList.stream().map(entity -> distributionDataServiceMapper.entityToRo(entity)).collect(Collectors.toList());
    }


}
