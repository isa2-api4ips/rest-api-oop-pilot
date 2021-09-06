package eu.europa.ec.isa2.oop.restapi.webhook;


import eu.europa.ec.isa2.oop.restapi.controller.profile.controllers.JwsService;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.eu.broker.gen.dataset.model.SignalMessage;
import eu.europa.eu.broker.gen.dataset.api.DsdDatasetMessageSubmissionApi;
import eu.europa.eu.broker.gen.dataset.invoker.ApiClient;
import io.swagger.v3.core.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class DSDWebhookClient {
    private static final Logger LOG = LoggerFactory.getLogger(DSDWebhookClient.class);


    DsdDatasetMessageSubmissionApi dsdDatasetMessageSubmissionApi;
    JwsService jwsService;

    @Autowired
    public DSDWebhookClient(JwsService jwsService) {
        this.jwsService = jwsService;

    }

    @PostConstruct
    public void init() {
        ApiClient apiClient = new ApiClient();
        dsdDatasetMessageSubmissionApi = new DsdDatasetMessageSubmissionApi(apiClient);
    }



    public void submitMessageReadySignal(String webhookUrl, String messageId, String userid) {
        LOG.info("Update dataset");

        // set call properties
        OffsetDateTime timestamp = null;
        String originalSender = userid;
        String finalRecipient = UUID.randomUUID().toString();
        ApiClient client= dsdDatasetMessageSubmissionApi.getApiClient();
        client.setBasePath(webhookUrl);

      //  String responseWebhook = nationalBrokerProperties.getDsdWebhookUrl();
        SignalMessage signalMessage  = new SignalMessage();
        signalMessage.setStatus(APIProblemType.MESSAGE_READY.getStatus());
        signalMessage.setType(APIProblemType.MESSAGE_READY.getType());
        signalMessage.setTitle(APIProblemType.MESSAGE_READY.getTitle());
        signalMessage.setDetail(APIProblemType.MESSAGE_READY.getDetail());
        signalMessage.setInstance(messageId);
        LOG.info("Response webhookUrl server is: " + webhookUrl);
        SignalMessage response = dsdDatasetMessageSubmissionApi.datasetSubmitSignalWebhookMethodId(messageId, signalMessage, originalSender, finalRecipient, timestamp, null);
        LOG.info("Response from server is: " + Json.pretty(response));
    }


}
