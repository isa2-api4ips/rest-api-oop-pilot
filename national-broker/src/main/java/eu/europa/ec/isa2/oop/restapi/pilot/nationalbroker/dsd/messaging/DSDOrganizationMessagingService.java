package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.DSDRequestLoggerDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.mapping.MessagingOrganizationMapping;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.eu.dsd.messaging.gen.organization.api.DsdOrganizationMessageReferenceListApi;
import eu.europa.eu.dsd.messaging.gen.organization.api.DsdOrganizationMessageSubmissionApi;
import eu.europa.eu.dsd.messaging.gen.organization.api.DsdOrganizationPullMessageApi;
import eu.europa.eu.dsd.messaging.gen.organization.invoker.ApiClient;
import eu.europa.eu.dsd.messaging.gen.organization.model.*;
import io.swagger.v3.core.util.Json;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DSDOrganizationMessagingService {
    private static final Logger LOG = LoggerFactory.getLogger(DSDOrganizationMessagingService.class);
    private MessagingOrganizationMapping mapper
            = Mappers.getMapper(MessagingOrganizationMapping.class);

    DsdOrganizationMessageSubmissionApi dsdOrganizationRestClient;

    DsdOrganizationMessageReferenceListApi dsdOrganizationListOfMessagesToPullApi;
    DsdOrganizationPullMessageApi dsdOrganizationPullResponseMessageApi;

    DSDRequestLoggerDao dsdRequestLoggerDao;
    DSDClientHttpRequestInterceptor interceptor;
    JwsService jwsService;

    NationalBrokerProperties nationalBrokerProperties;

    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager;

    @Autowired
    public DSDOrganizationMessagingService(DSDRequestLoggerDao dsdRequestLoggerDao, DSDClientHttpRequestInterceptor interceptor, JwsService jwsService, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager, NationalBrokerProperties nationalBrokerProperties) {
        this.dsdRequestLoggerDao = dsdRequestLoggerDao;
        this.interceptor = interceptor;
        this.jwsService = jwsService;
        this.authorizedClientManager = authorizedClientManager;
        this.nationalBrokerProperties = nationalBrokerProperties;
    }

    @PostConstruct
    public void init() {

        RestTemplate restTemplate = new DSDRestTemplate(jwsService, interceptor);
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setUserAgent("NationalBroker");
        if(nationalBrokerProperties.isOAuthSecurityEnabled()){
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("DSDOAuthClient")
                    .principal("NationalBrokerDSDClient")
                    .build();
            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
            OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
            apiClient.setAccessToken(accessToken.getTokenValue());
        }
        dsdOrganizationRestClient = new DsdOrganizationMessageSubmissionApi(apiClient);
        dsdOrganizationListOfMessagesToPullApi = new DsdOrganizationMessageReferenceListApi(apiClient);
        dsdOrganizationPullResponseMessageApi = new DsdOrganizationPullMessageApi(apiClient);
    }

    public void updateOrganization(OrganizationRO organizationRO, String messageId, String userid, String originalSender) {

        UpdateOrganizationRequest body = new UpdateOrganizationRequest();
        body.setUpdateOrganization(mapper.roToMessaging(organizationRO));

        // set call properties
        OffsetDateTime timestamp = OffsetDateTime.now();
        String finalRecipient = nationalBrokerProperties.getDsdFinalRecipient();
        dsdOrganizationRestClient.getApiClient().setBasePath(nationalBrokerProperties.getDsdUrl());

        interceptor.setContextMessageId(messageId, userid);
        SignalMessage response = dsdOrganizationRestClient.organizationUpdateMethodId(messageId, body, originalSender, finalRecipient, timestamp, null);
        interceptor.clearContextData();
        LOG.info("Response from server is: " + Json.pretty(response));
    }

    public List<String> getReadyStatusMessageIds() {
        MessageReferenceList list = dsdOrganizationListOfMessagesToPullApi.getMessageReferenceListForOrganizationStatusId();
        return list.getMessageReferenceList().stream().map(messageReference -> messageReference.getMessageId()).collect(Collectors.toList());
    }

    public OrganizationStatusResult getStatusMessage(String messageId) {
        LOG.info("getStatusMessage for id: [{}]", messageId);

        interceptor.setContextMessageId(messageId, "BROKER");
        MessageStatusResponse body = dsdOrganizationPullResponseMessageApi.getOrganizationMessageId(messageId);
        interceptor.clearContextData();
        if (body == null) {
            LOG.info("Status response from server is null ");
            return null;
        }
        LOG.info("Status response from server has payload count: [{}]", body.getMessageStatusResponse());
        return body.getMessageStatusResponse();
    }

    public void notifyMessageReceived(String messageId, String userid) {
        LOG.info("notifyMessageReceived for id: [{}]", messageId);

        SignalMessage signalMessage = new SignalMessage()
                .status(APIProblemType.MESSAGE_ACCEPTED.getStatus())
                .type(APIProblemType.MESSAGE_ACCEPTED.getType())
                .title(APIProblemType.MESSAGE_ACCEPTED.getTitle())
                .detail(APIProblemType.MESSAGE_ACCEPTED.getDetail())
                .instance("/messaging/organization/status/" + messageId);


        // set call properties
        OffsetDateTime timestamp = OffsetDateTime.now();
        String originalSender = userid;
        String finalRecipient = nationalBrokerProperties.getDsdFinalRecipient();
        dsdOrganizationRestClient.getApiClient().setBasePath(nationalBrokerProperties.getDsdUrl());


        SignalMessage response = dsdOrganizationRestClient.organizationSignalSubmissionId(
                messageId,
                signalMessage,
                originalSender, finalRecipient, timestamp, null);

        LOG.info("notifyMessageReceived for id: [{}] gor response: [{}]", messageId, response);

    }
}
