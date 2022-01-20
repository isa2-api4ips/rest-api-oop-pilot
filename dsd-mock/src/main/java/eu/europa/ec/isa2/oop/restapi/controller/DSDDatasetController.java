package eu.europa.ec.isa2.oop.restapi.controller;

import com.fasterxml.jackson.core.JsonParser;
import eu.europa.ec.isa2.oop.dsd.dao.DatasetDao;
import eu.europa.ec.isa2.oop.dsd.dao.PullMessageDao;
import eu.europa.ec.isa2.oop.dsd.dao.entities.PullMessageEntity;
import eu.europa.ec.isa2.oop.dsd.dao.entities.PullMessagePayloadEntity;
import eu.europa.ec.isa2.oop.dsd.model.*;
import eu.europa.ec.isa2.oop.dsd.model.enums.DSDRequestStatus;
import eu.europa.ec.isa2.oop.dsd.model.enums.PullStatus;
import eu.europa.ec.isa2.oop.dsd.utils.StoragesService;
import eu.europa.ec.isa2.oop.restapi.docsapi.DSDDatasetApi;
import eu.europa.ec.isa2.oop.restapi.utils.DaoQueryUtils;
import eu.europa.ec.isa2.oop.restapi.webhook.DSDWebhookClient;
import eu.europa.ec.isa2.restapi.profile.MessagingOpenApi;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.ec.isa2.restapi.profile.model.MessageReferenceListRO;
import eu.europa.ec.isa2.restapi.profile.model.SignalMessage;
import eu.europa.ec.isa2.restapi.utils.StorageException;
import io.swagger.v3.core.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Component
public class DSDDatasetController extends MessagingOpenApi implements DSDDatasetApi {
    private static final Logger LOG = LoggerFactory.getLogger(DSDDatasetController.class);

    DatasetDao datasetDao;
    PullMessageDao pullMessageDao;
    StoragesService storagesService;
    DSDWebhookClient dsdWebhookClient;

    @Autowired
    public DSDDatasetController(DatasetDao datasetDao,
                                PullMessageDao pullMessageDao,
                                StoragesService storagesService,
                                DSDWebhookClient dsdWebhookClient) {
        super(DSDDatasetApi.class, "dataset");
        this.datasetDao = datasetDao;
        this.pullMessageDao = pullMessageDao;
        this.storagesService = storagesService;
        this.dsdWebhookClient = dsdWebhookClient;
    }

    @Override
    public DatasetSearchResult searchDatasets(String service,
                                              String action,
                                              String messageId, DatasetSearchParameters searchParameters) {
        LOG.info("searchDatasets: [{}] for service  [{}], action  [{}], messageId [{}", Json.pretty(searchParameters), service, action, messageId);

        List<String> sortOrder = DaoQueryUtils.getSortOrderList(searchParameters.getSort());
        DatasetSearchResult serviceResult =
                datasetDao.getAllDatasets(searchParameters.getOffset(), searchParameters.getLimit(),
                        searchParameters.getOrganizationIdentifier(), searchParameters.getDatasetType(), searchParameters.getCountry(), sortOrder);
        return serviceResult;
    }

    @Override
    public void updateDataset(String service,
                              String action,
                              String messageId,
                              String webhookUrl,
                              DatasetRO datasetRO) {
        LOG.info("updateDataset: [{}] for service  [{}], action  [{}], messageId [{}], webhook url [{}] ", Json.pretty(datasetRO), service, action, messageId, webhookUrl);
        DatasetRO updatedDataset = datasetDao.updateDataset(datasetRO);

        StatusResult<DatasetRO> statusResult = new StatusResult<>();
        if(updatedDataset ==null) {
            statusResult.setDescription("Dataset was not updated! Inspect logs for error!");
            statusResult.setStatus(DSDRequestStatus.REJECTED.getStatus());
        } else {
            statusResult.setObject(updatedDataset);
            statusResult.setDescription("Dataset was successfully updated!");
            statusResult.setStatus(DSDRequestStatus.COMPLETED.getStatus());
        }

        ////
        statusResult.setRefMessage(messageId);


        PullMessagePayloadEntity payloadEntity = new PullMessagePayloadEntity();
        payloadEntity.setMimeType(MediaType.APPLICATION_JSON_VALUE);
        payloadEntity.setName(statusResult.getClass().getSimpleName());
        payloadEntity.setPath("");

        // save stat
        PullMessageEntity pullMessage = new PullMessageEntity();

        pullMessage.setIdentifier(UUID.randomUUID().toString());
        pullMessage.setAction("status");
        pullMessage.setService(service);

        pullMessage.setRefIdentifier(messageId);
        pullMessage.setRefAction(action);
        pullMessage.setRefService(service);
        pullMessage.setStatus(PullStatus.READY.name());

        pullMessage.addPayload(payloadEntity);

        try {
            File payload = storagesService.getNewStorageFile("json", "dataset-status-");
            Json.mapper().writeValue(payload, statusResult);
            payloadEntity.setPath(storagesService.getRelativePath(payload));
        } catch (StorageException | IOException e) {
            // for the demo just log!
            LOG.error("Error occurred while storing the payload", e);
        }

        pullMessageDao.persistFlushDetach(pullMessage);

        dsdWebhookClient.submitMessageReadySignal(webhookUrl, pullMessage.getIdentifier(), "dsd-application");
    }

    @Override
    public void createDataset(String service,
                              String action,
                              String messageId, DatasetRO datasetRO) {
        LOG.info("createDataset: [{}] for service  [{}], action  [{}], messageId [{}] ", Json.pretty(datasetRO), service, action, messageId);
        DatasetRO createdDataset = datasetDao.createDataset(datasetRO);

        StatusResult<DatasetRO> statusResult = new StatusResult<>();
        statusResult.setObject(createdDataset);
        statusResult.setDescription("Dataset was successfully created!");
        statusResult.setStatus(DSDRequestStatus.COMPLETED.getStatus());
        statusResult.setRefMessage(messageId);


        PullMessagePayloadEntity payloadEntity = new PullMessagePayloadEntity();
        payloadEntity.setMimeType(MediaType.APPLICATION_JSON_VALUE);
        payloadEntity.setName(statusResult.getClass().getSimpleName());
        payloadEntity.setPath("");

        // save stat
        PullMessageEntity pullMessage = new PullMessageEntity();

        pullMessage.setIdentifier(UUID.randomUUID().toString());
        pullMessage.setAction("status");
        pullMessage.setService(service);

        pullMessage.setRefIdentifier(messageId);
        pullMessage.setRefAction(action);
        pullMessage.setRefService(service);
        pullMessage.setStatus(PullStatus.READY.name());

        pullMessage.addPayload(payloadEntity);

        try {
            File payload = storagesService.getNewStorageFile("json", "dataset-status-");
            Json.mapper().writeValue(payload, statusResult);
            payloadEntity.setPath(storagesService.getRelativePath(payload));
        } catch (StorageException | IOException e) {
            // for the demo just log!
            LOG.error("Error occurred while storing the payload", e);
        }

        pullMessageDao.persistFlushDetach(pullMessage);
    }

    @Override
    public void deleteDataset(String service, String action, String messageId, DatasetRO datasetRO) {
        LOG.info("deleteDataset: [{}] for service  [{}], action  [{}], messageId [{}] ", Json.pretty(datasetRO), service, action, messageId);
        DatasetRO deleteDataset = datasetDao.deleteDataset(datasetRO);

        StatusResult<DatasetRO> statusResult = new StatusResult<>();
        if(deleteDataset ==null) {
            statusResult.setDescription("Dataset was not deleted! Inspect logs for error!");
            statusResult.setStatus(DSDRequestStatus.REJECTED.getStatus());
        } else {
            statusResult.setObject(deleteDataset);
            statusResult.setDescription("Dataset was successfully deleted!");
            statusResult.setStatus(DSDRequestStatus.COMPLETED.getStatus());
        }
        statusResult.setRefMessage(messageId);


        PullMessagePayloadEntity payloadEntity = new PullMessagePayloadEntity();
        payloadEntity.setMimeType(MediaType.APPLICATION_JSON_VALUE);
        payloadEntity.setName(statusResult.getClass().getSimpleName());
        payloadEntity.setPath("");

        // save stat
        PullMessageEntity pullMessage = new PullMessageEntity();

        pullMessage.setIdentifier(UUID.randomUUID().toString());
        pullMessage.setAction("status");
        pullMessage.setService(service);

        pullMessage.setRefIdentifier(messageId);
        pullMessage.setRefAction(action);
        pullMessage.setRefService(service);
        pullMessage.setStatus(PullStatus.READY.name());

        pullMessage.addPayload(payloadEntity);

        try {
            File payload = storagesService.getNewStorageFile("json", "dataset-status-");
            Json.mapper().writeValue(payload, statusResult);
            payloadEntity.setPath(storagesService.getRelativePath(payload));
        } catch (StorageException | IOException e) {
            // for the demo just log!
            LOG.error("Error occurred while storing the payload", e);
        }

        pullMessageDao.persistFlushDetach(pullMessage);
    }

    @Override
    public MessageReferenceListRO getDataSetServiceMessageReferenceList(){
        LOG.info("getDatasetServiceMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getMessageReferencesForServiceAndAction("dataset", null,-1, -1));
        return result;
    }


    @Override
    public MessageReferenceListRO getDataSetStatusMessageReferenceList() {
        LOG.info("getDatasetServiceActionMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getMessageReferencesForServiceAndAction("dataset", "status",-1, -1));
        return result;
    }

    @Override
    public MessageReferenceListRO getDataSetStatusResponseMessageReferenceList(String messageId) {
        LOG.info("getDatasetServiceActionMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getResponseMessageReferencesForServiceAndAction(
                "dataset", "update", messageId, null, null, -1, -1));
        return result;
    }

    @Override
    public MessageReferenceListRO getDataSetStatusResponseMessageReferenceListForService(String messageId)  {
        LOG.info("getDatasetServiceActionMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getResponseMessageReferencesForServiceAndAction(
                "dataset", "update", messageId, "organization", null, -1, -1));
        return result;
    }

    @Override
    public MessageReferenceListRO getDataSetStatusResponseMessageReferenceListForServiceAndAction(String messageId)  {
        LOG.info("getDatasetServiceActionMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getResponseMessageReferencesForServiceAndAction(
                "dataset", "update", messageId, "dataset", "status", -1, -1));
        return result;
    }

    @Override
    public StatusResult<DatasetRO> getDataSetStatusMessage(String messageId) throws IOException {
        LOG.info("getDatasetStatusMessage [{}]", messageId);
        PullMessageEntity entity =  pullMessageDao.getMessageByIdentifier(messageId);
        if (entity == null){
            LOG.info("getDatasetStatusMessage: No ready message found for the messageId [{}]", messageId);
            return null;
        }

        File payloadFile = storagesService.getFile(entity.getPayloads().get(0).getPath());
        JsonParser parser = Json.mapper().reader().createParser(payloadFile);
        LOG.info("getDatasetStatusMessage: return message for: [{}]", messageId);
        return parser.readValueAs(StatusResult.class);
    }

    @Override
    public StatusResult<DatasetRO> getDataSetStatusResponseMessage(String referenceMessageId, String messageId) throws IOException {
        LOG.info("getDatasetStatusResponseMessage");
        PullMessageEntity entity =  pullMessageDao.getMessageByIdentifier(messageId);
        if (entity == null){
            LOG.info("getDatasetStatusResponseMessage: No ready message found for the messageId [{}]", messageId);
            return null;
        }
        File payloadFile = storagesService.getFile(entity.getPayloads().get(0).getPath());
        JsonParser parser = Json.mapper().reader().createParser(payloadFile);
        LOG.info("getDatasetStatusResponseMessage: return message for: [{}]", messageId);
        return parser.readValueAs(StatusResult.class);
    }


    private void submitWebhookSignalMessageReady(String webhookUrl, String messageId ) {
        SignalMessage signalMessage = new SignalMessage();
        signalMessage.setStatus(APIProblemType.PULL_MESSAGE_READY.getStatus());
        signalMessage.setType(APIProblemType.PULL_MESSAGE_READY.getType());
        signalMessage.setTitle(APIProblemType.PULL_MESSAGE_READY.getTitle());
        signalMessage.setDetail(APIProblemType.PULL_MESSAGE_READY.getDetail());
        signalMessage.setInstance(messageId);

    }
    
    
}
