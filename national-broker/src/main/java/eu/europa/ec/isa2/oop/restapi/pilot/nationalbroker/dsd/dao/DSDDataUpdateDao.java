package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.dao.BasicDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.DSDDataUpdateEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.OrganizationEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.enums.DSDRequestStatus;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping.DSDDataUpdateMapping;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDDataUpdateRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DatasetRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters.OrganizationUpdateFilterRO;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Dao operations for {@link OrganizationEntity} entity
 *
 * @author Joze Rihtarsic
 * @since 1.0
 */
@Repository
public class DSDDataUpdateDao extends BasicDao<DSDDataUpdateEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(DSDDataUpdateDao.class);
    private DSDDataUpdateMapping mapper
            = Mappers.getMapper(DSDDataUpdateMapping.class);


    @Transactional
    public ServiceResult<DSDDataUpdateRO> getAllUpdates(int offset, int limit, OrganizationUpdateFilterRO filter, List<String> sortOrder) {

        if (filter != null) {
            String queryId = filter.getQueryId();
            LOG.info("Query [{}]", queryId);
            // set query id to null because it is not an entity property
            filter.setQueryId(null);
        }

        ServiceResult<DSDDataUpdateRO> sg = new ServiceResult<>();
        sg.setPage(offset < 0 ? 0 : offset);

        long iCnt = getDataListCount(null);
        if (limit < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            limit = (int) iCnt;
        }
        sg.setPageSize(limit);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = limit < 0 ? -1 : offset;

            List<DSDDataUpdateEntity> lst = getDataList(iStartIndex, limit, sortOrder, filter);

            for (DSDDataUpdateEntity d : lst) {
                DSDDataUpdateRO dro = mapper.entityToRo(d);
                dro.setIndex(iStartIndex++);
                sg.getServiceEntities().add(dro);

            }
        }
        return sg;
    }

    @Transactional
    public DSDDataUpdateRO updateDataset(DatasetRO data, String messageIdentifier, String username) {
        return createUpdateLog(data.getClass().getSimpleName(), "dataset", "update", messageIdentifier, username);
    }

    @Transactional
    public DSDDataUpdateRO createDataset(DatasetRO data, String messageIdentifier, String username) {
        return createUpdateLog(data.getClass().getSimpleName(), "dataset", "create", messageIdentifier, username);
    }

    @Transactional
    public DSDDataUpdateRO deleteDataset( String messageIdentifier, String username) {
        return createUpdateLog(DatasetRO.class.getSimpleName(), "dataset", "delete", messageIdentifier, username);
    }

    @Transactional
    public DSDDataUpdateRO createUpdateLog(String dataType, String service, String action, String messageIdentifier, String username) {

        DSDDataUpdateEntity item = new DSDDataUpdateEntity();
        item.setUsername(username);
        item.setUpdateRequestOn(Calendar.getInstance().getTime());
        item.setUpdateRequestId(messageIdentifier);
        item.setEntityType(dataType);
        item.setService(service);
        item.setAction(action);
        item.setDsdStatus(DSDRequestStatus.PENDING.getStatus());
        item = memEManager.merge(item);
        return mapper.entityToRo(item);
    }


    @Transactional
    public DSDDataUpdateRO updateStatusToRequest(String status, String message, String refMessageId, String responseMessageId) {

        List<DSDDataUpdateEntity> requestList = memEManager.createNamedQuery("OrganizationUpdateEntity.getByRequestIdentifier", DSDDataUpdateEntity.class)
                .setParameter("requestId", refMessageId).getResultList();

        if (requestList.isEmpty()) {
            LOG.error("DSD Update request with ID: [{}] does not exists!", refMessageId);
            return null;
        }

        if (requestList.size() > 1) {
            LOG.error("More than [{}] one DSD Update request with ID: [{}]!", requestList.size(), refMessageId);
            ;
        }
        DSDDataUpdateEntity updateEntity = requestList.get(0);

        Date updateResponse = Calendar.getInstance().getTime();
        updateEntity.setDsdStatus(status);
        updateEntity.setDsdMessage(message);
        updateEntity.setUpdateResponseId(responseMessageId);
        updateEntity.setUpdateConfirmedOn(updateResponse);

        return mapper.entityToRo(memEManager.merge(updateEntity));
    }
}
