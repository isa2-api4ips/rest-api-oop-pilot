package eu.europa.ec.isa2.oop.dsd.mapping;


import eu.europa.ec.isa2.oop.dsd.dao.entities.DatasetRelationshipEntity;
import eu.europa.ec.isa2.oop.dsd.dao.entities.DistributionDataServiceEntity;
import eu.europa.ec.isa2.oop.dsd.model.DataServiceRO;
import eu.europa.ec.isa2.oop.dsd.model.RelationshipRO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DistributionDataServiceMapping {

    DataServiceRO entityToRo(DistributionDataServiceEntity source);
    DistributionDataServiceEntity roToEntity(DataServiceRO source);
}
