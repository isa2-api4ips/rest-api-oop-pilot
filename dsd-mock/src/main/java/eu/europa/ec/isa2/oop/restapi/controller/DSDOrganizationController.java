package eu.europa.ec.isa2.oop.restapi.controller;

import com.fasterxml.jackson.core.JsonParser;
import eu.europa.ec.isa2.oop.dsd.dao.OrganizationDao;
import eu.europa.ec.isa2.oop.dsd.dao.PullMessageDao;
import eu.europa.ec.isa2.oop.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationRO;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationSearchParameters;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationSearchResult;
import eu.europa.ec.isa2.oop.dsd.model.StatusResult;
import eu.europa.ec.isa2.oop.dsd.model.enums.DSDRequestStatus;
import eu.europa.ec.isa2.oop.dsd.model.enums.PullStatus;
import eu.europa.ec.isa2.oop.restapi.docsapi.DSDOrganizationApi;
import eu.europa.ec.isa2.restapi.profile.docsapi.exceptions.MessagingAPIException;
import eu.europa.ec.isa2.restapi.utils.StorageException;
import eu.europa.ec.isa2.oop.dsd.utils.StoragesService;
import eu.europa.ec.isa2.restapi.profile.MessagingOpenApi;
import eu.europa.ec.isa2.restapi.profile.model.MessageReferenceListRO;
import eu.europa.ec.isa2.restapi.profile.model.SignalMessage;
import io.swagger.v3.core.util.Json;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static eu.europa.ec.isa2.restapi.profile.enums.APIProblemType.*;


@Component
public class DSDOrganizationController extends MessagingOpenApi implements DSDOrganizationApi {
    private static final Logger LOG = LoggerFactory.getLogger(DSDOrganizationController.class);

    OrganizationDao organizationDao;
    PullMessageDao pullMessageDao;
    StoragesService storagesService;

    @Autowired
    public DSDOrganizationController(OrganizationDao organizationDao,
                                     PullMessageDao pullMessageDao,
                                     StoragesService storagesService) {
        super(DSDOrganizationApi.class, "organization");
        this.organizationDao = organizationDao;
        this.pullMessageDao = pullMessageDao;
        this.storagesService = storagesService;

    }


    @Override
    public OrganizationSearchResult searchOrganizations(String service,
                                                        String action,
                                                        String messageId,OrganizationSearchParameters searchParameters) {
        LOG.info("searchOrganizations: [{}] for service  [{}], action  [{}], messageId [{}", Json.pretty(searchParameters), service, action, messageId);
        OrganizationSearchResult serviceResult =
                organizationDao.getAllOrganizations(searchParameters.getOffset(), searchParameters.getLimit());

        return serviceResult;
    }

    @Override
    public void updateOrganization(String service,
                                   String action,
                                   String messageId,OrganizationRO organization) {
        LOG.info("updateOrganization: [{}] for service  [{}], action  [{}], messageId [{}] ", Json.pretty(organization), service, action, messageId);
        OrganizationRO updatedOrganization = organizationDao.updateOrganizations(organization);

        StatusResult<OrganizationRO> statusResult = new StatusResult<>();
        statusResult.setObject(organization);
        statusResult.setDescription("Organization was successfully updated!");
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
            File payload = storagesService.getNewStorageFile("json","org-status");
            Json.mapper().writeValue(payload, statusResult);
            payloadEntity.setPath(storagesService.getRelativePath(payload));
        } catch (StorageException | IOException e) {
            e.printStackTrace();
        }

        pullMessageDao.persistFlushDetach(pullMessage);
    }

    @Override
    public MessageReferenceListRO getOrganizationMessageReferenceList(){
        LOG.info("getOrganizationMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getMessageReferencesForServiceAndAction(null, null, -1, -1));
        return result;
    }


    @Override
    public MessageReferenceListRO getOrganizationServiceMessageReferenceList(){
        LOG.info("getOrganizationServiceMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getMessageReferencesForServiceAndAction("organization", null,-1, -1));
        return result;
    }


    @Override
    public MessageReferenceListRO getOrganizationStatusMessageReferenceList() {
        LOG.info("getOrganizationServiceActionMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getMessageReferencesForServiceAndAction("organization", "status",-1, -1));
        return result;
    }

    @Override
    public MessageReferenceListRO getOrganizationStatusResponseMessageReferenceList(String messageId) {
        LOG.info("getOrganizationServiceActionMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getResponseMessageReferencesForServiceAndAction(
                "organization", "update", messageId, null, null, -1, -1));
        return result;
    }

    @Override
    public MessageReferenceListRO getOrganizationStatusResponseMessageReferenceListForService(String messageId)  {
        LOG.info("getOrganizationServiceActionMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getResponseMessageReferencesForServiceAndAction(
                "organization", "update", messageId, "organization", null, -1, -1));
        return result;
    }

    @Override
    public MessageReferenceListRO getOrganizationStatusResponseMessageReferenceListForServiceAndAction(String messageId)  {
        LOG.info("getOrganizationServiceActionMessageReferenceList");
        MessageReferenceListRO result = new MessageReferenceListRO();
        result.getMessageReferenceList().addAll(pullMessageDao.getResponseMessageReferencesForServiceAndAction(
                "organization", "update", messageId, "organization", "status", -1, -1));
        return result;
    }

    @Override
    public StatusResult<OrganizationRO> getOrganizationStatusMessage(String messageId) throws IOException {
        LOG.info("getOrganizationStatusMessage [{}]", messageId);
        PullMessageEntity entity =  pullMessageDao.getMessageByIdentifier(messageId);
        if (entity == null){
            LOG.info("getOrganizationStatusMessage: No ready message found for the messageId [{}]", messageId);
            return null;
        }

        File payloadFile = storagesService.getFile(entity.getPayloads().get(0).getPath());
        JsonParser parser = Json.mapper().reader().createParser(payloadFile);
        LOG.info("getOrganizationStatusMessage: return message for: [{}]", messageId);
        return parser.readValueAs(StatusResult.class);
    }

    @Override
    public StatusResult<OrganizationRO> getOrganizationStatusResponseMessage(String referenceMessageId, String messageId) throws IOException {
        LOG.info("getOrganizationStatusResponseMessage");
        PullMessageEntity entity =  pullMessageDao.getMessageByIdentifier(messageId);
        if (entity == null){
            LOG.info("getOrganizationStatusResponseMessage: No ready message found for the messageId [{}]", messageId);
            return null;
        }
        File payloadFile = storagesService.getFile(entity.getPayloads().get(0).getPath());
        JsonParser parser = Json.mapper().reader().createParser(payloadFile);
        LOG.info("getOrganizationStatusResponseMessage: return message for: [{}]", messageId);
        return parser.readValueAs(StatusResult.class);
    }

    @Override
    public void signalMessageSubmission(String messageId, SignalMessage signalMessage) {
        LOG.info("signalMessageSubmission");

        SignalMessage responseSignalMessage;
        if (StringUtils.equals(signalMessage.getType(), MESSAGE_ACCEPTED.getType() )) {
            LOG.info("signalMessageSubmission: message accepted for id:" + messageId);
            pullMessageDao.updateStatusByIdentifier(messageId, "RECEIVED");
        } else {
            throw new MessagingAPIException(INVALID_MESSAGE_ID,
                    "Reference message id ["+messageId+"] does not exists! Signal is not accepted",
                    null, null);
        }
    }
}
