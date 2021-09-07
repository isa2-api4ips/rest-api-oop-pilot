package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd;


import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.DSDDataUpdateDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping.AddressMapping;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.DSDDatasetMessagingService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.DSDOrganizationMessagingService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.mapping.MessagingDatasetStatusResultMapping;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.eu.dsd.messaging.gen.dataset.model.DatasetStatusResult;
import eu.europa.eu.dsd.messaging.gen.server.dataset.api.MessagingWebhookApi;
import eu.europa.eu.dsd.messaging.gen.server.dataset.model.SignalMessage;
import eu.europa.eu.dsd.messaging.gen.server.dataset.model.StatusRMessageIdBody;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.OffsetDateTime;


@RestController
public class DSDWebhookController implements MessagingWebhookApi {
    private MessagingDatasetStatusResultMapping mapper
            = Mappers.getMapper(MessagingDatasetStatusResultMapping.class);

    private static final Logger LOG = LoggerFactory.getLogger(DSDWebhookController.class);
    DSDOrganizationMessagingService dsdOrganizationMessagingService;
    DSDDatasetMessagingService dsdDatasetMessagingService;
    DSDDataUpdateDao dsdDataUpdateDao;
    NationalBrokerProperties nationalBrokerProperties;

    @Autowired
    public DSDWebhookController(DSDOrganizationMessagingService dsdOrganizationMessagingService,
                                DSDDatasetMessagingService dsdDatasetMessagingService,
                                DSDDataUpdateDao dsdDataUpdateDao,
                                NationalBrokerProperties nationalBrokerProperties) {
        this.dsdOrganizationMessagingService = dsdOrganizationMessagingService;
        this.dsdDatasetMessagingService = dsdDatasetMessagingService;
        this.dsdDataUpdateDao = dsdDataUpdateDao;
        this.nationalBrokerProperties = nationalBrokerProperties;
    }

    @Override
    public ResponseEntity<SignalMessage> datasetSubmitSignalWebhookMethodId(String messageId, String originalSender, String finalRecipient, OffsetDateTime timestamp, String edelMessageSig, @Valid SignalMessage body) {
        LOG.info("Got signal to pull:" + body.getInstance());
        DatasetStatusResult result = dsdDatasetMessagingService.getStatusMessage(body.getInstance());
        updateDatasetStatus(result, body.getInstance());

        return createMessageAcceptedSignal(messageId);
    }

    @Override
    public ResponseEntity<SignalMessage> submitStatusResponseWebhookMessageOperation(String messageId, String rMessageId, String originalSender, String finalRecipient, OffsetDateTime timestamp, String edelMessageSig, @Valid StatusRMessageIdBody body) {
        updateDatasetStatus( mapper.serverRoToMessaging(body.getMessageWebhookStatusResponse()), rMessageId);
        return createMessageAcceptedSignal(rMessageId);
    }

    public void updateDatasetStatus(DatasetStatusResult result, String messageId) {
        // update value to database
        if (result.getObject() == null) {
            LOG.warn("Got null result");
            return;
        }
        dsdDataUpdateDao.updateStatusToRequest(result.getStatus(), result.getDescription(), result.getRefMessage(), messageId);
        // update value to server - there is only one signal
        dsdOrganizationMessagingService.notifyMessageReceived(messageId, nationalBrokerProperties.getApplicationOriginalSender());

    }

    public ResponseEntity<SignalMessage> createMessageAcceptedSignal(String messageId){
        SignalMessage signalMessage = new SignalMessage()
                .status(APIProblemType.MESSAGE_ACCEPTED.getStatus())
                .type(APIProblemType.MESSAGE_ACCEPTED.getType())
                .title(APIProblemType.MESSAGE_ACCEPTED.getTitle())
                .detail(APIProblemType.MESSAGE_ACCEPTED.getDetail())
                .instance("/messaging/organization/status/" + messageId);

        ResponseEntity<SignalMessage> responseEntity = new ResponseEntity<>(signalMessage, HttpStatus.valueOf(APIProblemType.MESSAGE_ACCEPTED.getStatus()));
        return responseEntity;
    }


}
