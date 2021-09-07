package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.dao.BasicDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.DSDRequestStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters.OrganizationFilterRO;
import eu.europa.ec.isa2.oop.restapi.utils.EntityCollectionUtils;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.EntityStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping.OrganizationMapping;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Dao operations for {@link OrganizationEntity} entity
 *
 * @author Joze Rihtarsic
 * @since 1.0
 */
@Repository
public class OrganizationDao extends BasicDao<OrganizationEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationDao.class);
    private OrganizationMapping mapper
            = Mappers.getMapper(OrganizationMapping.class);


    @Transactional
    public ServiceResult<OrganizationRO> getAllOrganizations(int offset, int limit, OrganizationFilterRO filter, List<String> sortOrder) {

        if (filter != null) {
            String queryId = filter.getQueryId();
            LOG.info("Query [{}]", queryId);
            // set query id to null because it is not an entity property
            filter.setQueryId(null);
        }

        ServiceResult<OrganizationRO> sg = new ServiceResult<>();
        sg.setPage(offset < 0 ? 0 : offset);

        long iCnt = getDataListCount(null);
        if (limit < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            limit = (int) iCnt;
        }
        sg.setPageSize(limit);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = limit<0?-1:offset;

            List<OrganizationEntity> lst = getDataList(iStartIndex, limit, sortOrder, filter, null);

            for (OrganizationEntity d : lst) {
                OrganizationRO dro = mapper.entityToRo(d);
                dro.setIndex( iStartIndex++);
                sg.getServiceEntities().add(dro);

            }
        }
        return sg;
    }

    @Transactional
    public OrganizationRO setStatusToOrganization(String identifier, String status, String message, String refMessageId, String responseMessageId) {
        OrganizationEntity entity = getOrganizationByIdentifier(identifier);
        if (entity==null) {
            return null;
        }
        entity.setDsdStatus(status);
        entity.setDsdMessage(message);

        Optional<DSDDataUpdateEntity> updateEntityOptional =  entity.
                getUpdateRequests().stream()
                .filter(organizationUpdateEntity -> StringUtils.equals(organizationUpdateEntity.getUpdateRequestId(), refMessageId)).findFirst();

        Date updateResponse = Calendar.getInstance().getTime();
        if (updateEntityOptional.isPresent()) {
            DSDDataUpdateEntity updateEntity = updateEntityOptional.get();
            updateEntity.setDsdStatus(status);
            updateEntity.setDsdMessage(message);
            updateEntity.setUpdateResponseId(responseMessageId);
            updateEntity.setUpdateConfirmedOn(updateResponse);
            memEManager.merge(updateEntity);
        }
        if (StringUtils.equals(refMessageId, entity.getUpdateRequestId())) {
            entity.setUpdateResponseId(responseMessageId);
            entity.setUpdateConfirmedOn(updateResponse);
        }

        return mapper.entityToRo(memEManager.merge(entity));
    }

    @Transactional
    public OrganizationRO updateOrganizations(OrganizationRO organizationRO, String messageIdentifier) {
        TypedQuery<OrganizationEntity> query = memEManager.createNamedQuery(
                "OrganizationEntity.getByIdentifier",OrganizationEntity.class);
        query.setParameter("identifier", organizationRO.getIdentifier());

        List<OrganizationEntity> entities = query.getResultList();
        if (entities.isEmpty()) {
            // throw error
            return null;
        }
        OrganizationEntity entity = entities.get(0);
        DSDDataUpdateEntity item = new DSDDataUpdateEntity();

        item.setUsername("ADMIN_USER");
        item.setOrganization(entity);
        item.setUpdateRequestOn(Calendar.getInstance().getTime());
        item.setUpdateRequestId(messageIdentifier);
        item.setEntityType(organizationRO.getClass().getSimpleName());
        item.setService("organization");
        item.setAction("update");
        item.setDsdStatus(DSDRequestStatus.PENDING.getStatus());

        entity.getUpdateRequests().add(item);

        entity.setUpdateRequestOn(Calendar.getInstance().getTime());
        entity.setUpdateRequestId(messageIdentifier);
        entity.setUpdateResponseId(null);
        entity.setUpdateConfirmedOn(null);

        // update value
        entity.setDsdStatus(organizationRO.getDsdStatus()==null? EntityStatus.OK.name() :organizationRO.getDsdStatus().name());
        entity.setDsdMessage(organizationRO.getDsdMessage());
        if (organizationRO.getAddress() ==null) {
            entity.setAddress(null);
        }else {
            if (entity.getAddress() ==null ) {
                entity.setAddress(new AddressEntity());
            }
            entity.getAddress().setFullAddress(organizationRO.getAddress().getFullAddress());
        }

        OrganizationEntity finalEntity = entity;
        List<AltLabelEntity> updatedAltLabelEntities
                = EntityCollectionUtils.updateLabelList(entity.getAltLabels(),
                organizationRO.getAltLabels(), label -> new AltLabelEntity(label, finalEntity));
        entity.getAltLabels().clear();
        entity.getAltLabels().addAll(updatedAltLabelEntities);


        List<PrefLabelEntity> updatedPrefLabelEntities
                = EntityCollectionUtils.updateLabelList(entity.getPrefLabels(),
                organizationRO.getPrefLabels(), label -> new PrefLabelEntity(label, finalEntity));
        entity.getPrefLabels().clear();
        entity.getPrefLabels().addAll(updatedPrefLabelEntities);

        entity.setDsdStatus(EntityStatus.MODIFIED.name());
        entity.setDsdMessage("DSD organization update in progress");
        entity = memEManager.merge(entity);

        return mapper.entityToRo(entity);
    }

    OrganizationEntity getOrganizationByIdentifier(String identifier) {
        TypedQuery<OrganizationEntity> query = memEManager.createNamedQuery(
                "OrganizationEntity.getByIdentifier",OrganizationEntity.class);
        query.setParameter("identifier", identifier);

        List<OrganizationEntity> entities = query.getResultList();
        if (entities.isEmpty()) {
            // throw error
            return null;
        }
        if (entities.size()>1) {
            LOG.warn("More than one organization entry with identifier [{}]!", identifier);
        }
        return entities.get(0);

    }
}
