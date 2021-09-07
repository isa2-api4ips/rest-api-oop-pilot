package eu.europa.ec.isa2.oop.dsd.dao;

import eu.europa.ec.isa2.oop.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.dsd.mapping.DatasetMapping;
import eu.europa.ec.isa2.oop.dsd.mapping.OrganizationMapping;
import eu.europa.ec.isa2.oop.dsd.model.DatasetRO;
import eu.europa.ec.isa2.oop.dsd.model.DatasetSearchResult;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationRO;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationSearchResult;
import eu.europa.ec.isa2.oop.restapi.controller.profile.controllers.JwsService;
import eu.europa.ec.isa2.oop.restapi.utils.EntityCollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Dao operations for {@link OrganizationEntity} entity
 *
 * @author Joze Rihtarsic
 * @since 1.0
 */
@Repository
public class DatasetDao extends BasicDao<DatasetEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(DatasetDao.class);

    private DatasetMapping mapper = Mappers.getMapper(DatasetMapping.class);


    private String buildDataSetOrderByParam(List<String> sortOrders) {
        StringWriter stringWriter = new StringWriter();

        boolean notFirst = false;
        for (String sortData : sortOrders) {
            String sortField = sortData;
            boolean ascending = true;
            if (StringUtils.startsWithAny(sortData, "+", "-")) {
                ascending = StringUtils.startsWith(sortData, "+");
                sortField = sortData.substring(1);
            }
            if (notFirst) {
                stringWriter.write(", ");
            } else {
                stringWriter.write(" ");
                notFirst = true;
            }

            stringWriter.write(getMappedFiledName(sortField));
            stringWriter.write(' ');
            stringWriter.write(ascending ? "ASC" : "DESC");
        }


        String sortOrder = stringWriter.toString();

        return StringUtils.isBlank(sortOrder) ? " d.ID ASC" : sortOrder;
    }

    public String getMappedFiledName(String filedName){
        switch (StringUtils.lowerCase(filedName)) {
            case "organizationidentifier":
                return "org.IDENTIFIER";
            case "datasetType":
                return "d.DATASET_TYPE";
            case "country":
                return "adr.ADMIN_UNIT_LEVEL";
            default:
                LOG.info("Sort order for filed [{}] is not supported! ", filedName);
                return "d.ID_PK";
        }
    }

    @Transactional
    public DatasetSearchResult getAllDatasets(int page, int pageSize, String organizationId, String datesetType, String country, List<String> sortData) {

        LOG.info("get dataset for organizationId [{}], datasetType [{}], sort [{}]", organizationId, datesetType, String.join(",", sortData));
        String orderBy = buildDataSetOrderByParam(sortData);

        // quick solution for pilot project
        // implement generic code with query builder!
        Query query = memEManager.createNativeQuery("SELECT d.* FROM DSD_MOCK_DATASET d " +
                " INNER JOIN DSD_MOCK_ORGANIZATION org  ON d.FK_ORGANIZATION_ID = org.ID_PK  " +
                " INNER JOIN DSD_MOCK_ADDRESS adr  ON org.ID_PK = adr.ID_PK  " +
                " where (:organizationIdentifier IS NULL OR :organizationIdentifier='' OR org.IDENTIFIER =:organizationIdentifier)" +
                " AND (:type IS NULL OR :type='' OR d.DATASET_TYPE = :type)" +
                " AND (:country IS NULL OR :country='' OR adr.ADMIN_UNIT_LEVEL = :country)" +
                " ORDER BY " + orderBy, DatasetEntity.class)
                .setParameter("type", StringUtils.isBlank(datesetType) ? "" : datesetType)
                .setParameter("organizationIdentifier", StringUtils.isBlank(organizationId) ? "" : organizationId)
                .setParameter("country", StringUtils.isBlank(country) ? "" : country)
                ;

        TypedQuery<BigInteger> queryCount = memEManager.createNamedQuery("DatasetEntity.getCountByCompositeSearch", BigInteger.class)
                .setParameter("type", StringUtils.isBlank(datesetType) ? "" : datesetType)
                .setParameter("organizationIdentifier", StringUtils.isBlank(organizationId) ? "" : organizationId)
                .setParameter("country", StringUtils.isBlank(country) ? "" : country);


        long iCnt = queryCount.getSingleResult().longValue();
        if (pageSize < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            pageSize = (int) iCnt;
        }
        page = page < 0 ? 0 : page;

        DatasetSearchResult sg = new DatasetSearchResult();
        sg.setPage(page);
        sg.setPageSize(pageSize);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            if (iStartIndex >= iCnt && page > 0) {
                page = page - 1;
                sg.setPage(page); // go back for a page
                iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            }

            query.setFirstResult(iStartIndex);
            query.setMaxResults(pageSize);
            List<DatasetEntity> lst = query.getResultList();

            for (DatasetEntity d : lst) {
                DatasetRO dro = mapper.entityToRo(d);
                dro.setIndex(iStartIndex++);
                sg.getServiceEntities().add(dro);

            }
        }
        return sg;
    }

    @Transactional
    public DatasetRO updateDataset(DatasetRO datasetRO) {
        TypedQuery<DatasetEntity> query = memEManager.createNamedQuery(
                "DatasetEntity.getByOneOfTheIdentifier",DatasetEntity.class);

        query.setParameter("identifiers", datasetRO.getIdentifiers());

        List<DatasetEntity> entities = query.getResultList();
        if (entities.isEmpty()) {
            // throw error
            LOG.error("No Dataset with identifier [{}]", String.join(",",datasetRO.getIdentifiers() ));
            return null;
        }
        if (entities.size()>1) {
            // throw error
            LOG.error("Found more than one Dataset with identifiers [{}]", String.join(",",datasetRO.getIdentifiers() ));
            return null;
        }

        DatasetEntity entity = entities.get(0);
        entity.setConformsTo(datasetRO.getConformsTo());
        DatasetEntity finalEntity = entity;
        // update Titles
        List<DatasetTitleEntity> updatedTitlesEntities
                = EntityCollectionUtils.updateLabelList(entity.getTitles(),
                datasetRO.getTitles(), label -> new DatasetTitleEntity(label, finalEntity));
        entity.getTitles().clear();
        entity.getTitles().addAll(updatedTitlesEntities);
        // update Descriptions
        List<DatasetDescriptionEntity> updatedDescriptionsEntities
                = EntityCollectionUtils.updateLabelList(entity.getDescriptions(),
                datasetRO.getDescriptions(), label -> new DatasetDescriptionEntity(label, finalEntity));
        entity.getDescriptions().clear();
        entity.getDescriptions().addAll(updatedDescriptionsEntities);
        // update QualifiedRelationships
        entity.getQualifiedRelationships().clear();
        datasetRO.getQualifiedRelationships().forEach(relationshipRO -> {
            entity.getQualifiedRelationships().add(new DatasetRelationshipEntity(relationshipRO.getRelation(),relationshipRO.getHadRole(),entity ));
        });
        // update Distributions
        entity.getDistributions().clear();
        datasetRO.getDistributions().forEach(distributionRO -> {
            DatasetDistributionEntity distributionEntity = new DatasetDistributionEntity(
                    distributionRO.getConformsTo(),
                    distributionRO.getFormat(),
                    distributionRO.getMediaType(),
                    distributionRO.getAccessURL(),
                    null,
                    null,

                    entity );
             // set Distribution description
            List<DistributionDescriptionEntity> distributionDescriptionsEntities
                    = EntityCollectionUtils.updateLabelList(distributionEntity.getDescriptions(),
                    distributionRO.getDescriptions(),
                    label -> new DistributionDescriptionEntity(label, distributionEntity));
            distributionEntity.getDescriptions().addAll(distributionDescriptionsEntities);
            // set DataServices
            distributionRO.getDataServices().forEach(dataServiceRO -> {
                distributionEntity.getDataServices().add(
                        new DistributionDataServiceEntity(dataServiceRO.getIdentifier(),
                                dataServiceRO.getConformsTo(),
                                dataServiceRO.getTitle(),
                                dataServiceRO.getEndpointURL(),
                                distributionEntity));
            });

            entity.getDistributions().add(distributionEntity);
        });

        return mapper.entityToRo(entity);
    }

    @Transactional
    public DatasetRO deleteDataset(DatasetRO datasetRO) {
        TypedQuery<DatasetEntity> query = memEManager.createNamedQuery(
                "DatasetEntity.getByOneOfTheIdentifier",DatasetEntity.class);

        query.setParameter("identifiers", datasetRO.getIdentifiers());

        List<DatasetEntity> entities = query.getResultList();
        if (entities.isEmpty()) {
            // throw error
            LOG.error("No Dataset with identifier [{}]", String.join(",",datasetRO.getIdentifiers() ));
            return null;
        }
        if (entities.size()>1) {
            // throw error
            LOG.error("Found more than one Dataset with identifiers [{}]", String.join(",",datasetRO.getIdentifiers() ));
            return null;
        }

        DatasetEntity entity = entities.get(0);
        memEManager.remove(entity);

        return mapper.entityToRo(entity);
    }

    @Transactional
    public DatasetRO createDataset(DatasetRO datasetRO) {

        DatasetEntity entity = new DatasetEntity();
        if (datasetRO.getIdentifiers().isEmpty()) {
            entity.getIdentifiers().add(new DatasetIdentifierEntity(UUID.randomUUID().toString(), entity));
        } else {
            datasetRO.getIdentifiers().forEach(identity -> entity.getIdentifiers().add(new DatasetIdentifierEntity(identity, entity)));
        }
        datasetRO.getTitles().forEach(title-> entity.getTitles().add(new DatasetTitleEntity(title, entity)));
        datasetRO.getDescriptions().forEach(desc-> entity.getDescriptions().add(new DatasetDescriptionEntity(desc, entity)));
        datasetRO.getQualifiedRelationships().forEach(relationshipRO-> entity.getQualifiedRelationships().add(
                new DatasetRelationshipEntity(relationshipRO.getRelation(), relationshipRO.getHadRole(), entity)));

        datasetRO.getDistributions().forEach(distributionRO-> entity.getDistributions().add(
                new DatasetDistributionEntity(distributionRO.getConformsTo(),
                        distributionRO.getFormat(),
                        distributionRO.getMediaType(),
                        distributionRO.getAccessURL(),
                        distributionRO.getDescriptions().stream().map(desc-> new DistributionDescriptionEntity(desc, null)).collect(Collectors.toList()),
                        distributionRO.getDataServices().stream().map(dataServiceRO-> new DistributionDataServiceEntity(
                                dataServiceRO.getIdentifier(), dataServiceRO.getConformsTo(), dataServiceRO.getTitle(), dataServiceRO.getEndpointURL(),
                                null)).collect(Collectors.toList()),

                        entity)));

        entity.setConformsTo(datasetRO.getConformsTo());
        entity.setType(datasetRO.getType());

        TypedQuery<OrganizationEntity> queryOrganization = memEManager.createNamedQuery(
                "OrganizationEntity.getByIdentifier",OrganizationEntity.class);
        queryOrganization.setParameter("identifier", datasetRO.getPublisher().getIdentifier());

        entity.setPublisher(queryOrganization.getSingleResult());

        DatasetEntity entityResult = memEManager.merge(entity);
        return mapper.entityToRo(entityResult);
    }

}
