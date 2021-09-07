package eu.europa.ec.isa2.oop.dsd.dao;

import eu.europa.ec.isa2.oop.dsd.dao.entities.OrganizationEntity;
import eu.europa.ec.isa2.oop.dsd.dao.entities.PullMessageEntity;
import eu.europa.ec.isa2.oop.dsd.mapping.MessageReferenceMapping;
import eu.europa.ec.isa2.oop.dsd.model.enums.PullStatus;
import eu.europa.ec.isa2.restapi.profile.model.MessageReferenceRO;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dao operations for {@link OrganizationEntity} entity
 *
 * @author Joze Rihtarsic
 * @since 1.0
 */
@Repository
public class PullMessageDao extends BasicDao<PullMessageEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(PullMessageDao.class);
    private MessageReferenceMapping mapper
            = Mappers.getMapper(MessageReferenceMapping.class);


    static class SearchParameters {
        private String service;
        private String action;
        private String refIdentifier;
        private String refService;
        private String refAction;
        private String status = PullStatus.READY.name(); // by default return only ready statuses


        public SearchParameters(String service, String action) {
            this.service = service;
            this.action = action;
        }

        public SearchParameters(String refService, String refAction, String refIdentifier, String service, String action) {
            this.service = service;
            this.action = action;
            this.refIdentifier = refIdentifier;
            this.refService = refService;
            this.refAction = refAction;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getRefIdentifier() {
            return refIdentifier;
        }

        public void setRefIdentifier(String refIdentifier) {
            this.refIdentifier = refIdentifier;
        }

        public String getRefService() {
            return refService;
        }

        public void setRefService(String refService) {
            this.refService = refService;
        }

        public String getRefAction() {
            return refAction;
        }

        public void setRefAction(String refAction) {
            this.refAction = refAction;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @Transactional
    public PullMessageEntity getMessageByIdentifier(String identifier) {
        TypedQuery<PullMessageEntity> query = memEManager.createNamedQuery("PullMessageEntity.getByIdentifier", PullMessageEntity.class)
                .setParameter("identifier", identifier);
        List<PullMessageEntity> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            LOG.warn("More than one pull message with identifier:" + identifier);
        }
        // make sure all payloads are loaded (because lazy fetch - do not put it to eager (bad practice)
        // also search will load payloads where they are not needed!:) )

        list.get(0).getPayloads().forEach(payoad -> LOG.debug("Got payload:" + payoad.getName()));
        return list.get(0);
    }

    @Transactional
    public List<MessageReferenceRO> getResponseMessageReferencesForServiceAndAction(String service, String action, String messageId, String responseService, String responseAction, int page, int pageSize) {

        SearchParameters searchParameters = new SearchParameters(service, action, messageId, responseService, responseAction);


        long iCnt = getDataListCount(searchParameters);
        if (pageSize < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            pageSize = (int) iCnt;
        }

        if (iCnt > 0) {
            int iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            if (iStartIndex >= iCnt && page > 0) {
                page = page - 1;
                iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            }
            return getDataList(iStartIndex, pageSize, null, null, searchParameters)
                    .stream().map(mapper::pullMessageToResponseMessageReference).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<MessageReferenceRO> getMessageReferencesForServiceAndAction(String service, String action, int page, int pageSize) {


        // get all count
        SearchParameters searchParameters = new SearchParameters(service, action);
        long iCnt = getDataListCount(searchParameters);
        if (pageSize < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            pageSize = (int) iCnt;
        }

        if (iCnt > 0) {
            int iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            if (iStartIndex >= iCnt && page > 0) {
                page = page - 1;
                iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            }
            return getDataList(iStartIndex, pageSize, null, null, searchParameters)
                    .stream().map(mapper::pullMessageToMessageReference).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    @Transactional
    public void updateStatusByIdentifier(String identifier, String status) {

        PullMessageEntity entity = getMessageByIdentifier(identifier);
        if (entity == null) {
            return;
        }
        entity.setStatus(status);
        memEManager.merge(entity);
    }

}
