package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.dao.BasicDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.services.StoragesService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.DSDRequestLogEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.OrganizationEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping.DSDRequestLogMapping;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDMessageRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDRequestLogRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters.DSDRequestLogFilterRO;
import eu.europa.ec.isa2.restapi.utils.StorageException;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.io.*;
import java.util.Calendar;
import java.util.List;

/**
 * Dao operations for {@link OrganizationEntity} entity
 *
 * @author Joze Rihtarsic
 * @since 1.0
 */
@Repository
public class DSDRequestLoggerDao extends BasicDao<DSDRequestLogEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(DSDRequestLoggerDao.class);
    private DSDRequestLogMapping mapper
            = Mappers.getMapper(DSDRequestLogMapping.class);


    StoragesService storagesService;

    @Autowired
    public DSDRequestLoggerDao(StoragesService storagesService) {
        this.storagesService = storagesService;
    }

    @Transactional
    public DSDRequestLogEntity logRequest(String storagePath, String messageId, String service, String action, String username, String httpPath, String httpMethod ) {
        DSDRequestLogEntity entity = new DSDRequestLogEntity();
        entity.setRequestOn(Calendar.getInstance().getTime());
        entity.setRequestStoragePath(storagePath);
        entity.setMessageId(messageId);
        entity.setDsdStatus("SUBMIT");
        entity.setRole("REQUESTOR");
        entity.setHttpPath(httpPath);
        entity.setHttpMethod(httpMethod);
        entity.setService(service);
        entity.setAction(action);
        entity.setUsername(username);
        return memEManager.merge(entity);
    }


    public DSDRequestLogRO getEntityById(Long id){
        TypedQuery<DSDRequestLogEntity> entityQuery = memEManager.createNamedQuery("RequestResponseEntity.getById",DSDRequestLogEntity.class)
                .setParameter("id",id);
        List<DSDRequestLogEntity> list =  entityQuery.getResultList();
        return list.isEmpty()?null:mapper.entityToRo(list.get(0));
    }

    @Transactional
    public DSDRequestLogEntity logResponseForRequest(DSDRequestLogEntity entity ){
        return memEManager.merge(entity);
    }


    @Transactional
    public ServiceResult<DSDRequestLogRO> getAllLogs(int offset, int limit, DSDRequestLogFilterRO filter, List<String> sortOrder) {

        if (filter != null) {
            String queryId = filter.getQueryId();
            LOG.info("Query [{}]", queryId);
            // set query id to null because it is not an entity property
            filter.setQueryId(null);
        }

        ServiceResult<DSDRequestLogRO> sg = new ServiceResult<>();
        sg.setPage(offset < 0 ? 0 : offset);

        long iCnt = getDataListCount(filter);
        if (limit < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            limit = (int) iCnt;
        }
        sg.setPageSize(limit);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = limit<0?-1:offset;

            List<DSDRequestLogEntity> lst = getDataList(iStartIndex, limit, sortOrder, filter, null);

            for (DSDRequestLogEntity d : lst) {
                DSDRequestLogRO dro = mapper.entityToRo(d);
                dro.setIndex( iStartIndex++);
                sg.getServiceEntities().add(dro);

            }
        }
        return sg;
    }

    public DSDRequestLogRO getRequestLogById(Long id, boolean withMessages) throws IOException {
        DSDRequestLogRO request = getEntityById(id);
        if (request==null) {
            LOG.warn("Request with id [{}] does not exists!", id);
            return null;
        }
        if(withMessages) {
            File requestFile = storagesService.getFile(request.getRequestStoragePath());
            File responseFile = storagesService.getFile(request.getResponseStoragePath());
            request.setRequestMessage(parseRequest(requestFile));
            request.setResponseMessage(parseRequest(responseFile));
        }

        return request;
    }

    /**
     *  Parse http message: For the pilot purpose, only "string bodies" are expected. In case of binary files (as images
     *  and PDFs, the code must be updated)
     * @param requestFile
     * @throws IOException
     */
    public DSDMessageRO parseRequest(File requestFile) throws IOException {
        DSDMessageRO messageRO = new DSDMessageRO();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(requestFile));

        String header = bufferedReader.readLine();
        // read until the first empty line!
        while (header.length() > 0) {
            String[] headerParameter = parseHeaderParameter(header);
            messageRO.getHeaders().put(headerParameter[0],headerParameter[1]);
            header = bufferedReader.readLine();
        }

        StringWriter bodyWriter = new StringWriter();
        String bodyLine = bufferedReader.readLine();
        while (bodyLine != null) {
            bodyWriter.append(bodyLine);
            bodyWriter.append("\n");
            bodyLine = bufferedReader.readLine();
        }
        messageRO.setBody(bodyWriter.toString());
        return messageRO;
    }


    private String[] parseHeaderParameter(String header) throws StorageException {
        int idx = header.indexOf(": ");
        if (idx == -1) {
            LOG.warn("Invalid Header Parameter: [{}] ", header);
            return null;
        }
        return new String[]{header.substring(0, idx), header.substring(idx + 2, header.length())};
    }
}
