package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.DSDRequestLoggerDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.mapping.MessagingDatasetMapping;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DatasetRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters.DatasetFilterRO;
import eu.europa.ec.isa2.oop.restapi.utils.DaoQueryUtils;
import eu.europa.eu.dsd.messaging.gen.dataset.api.DsdDatasetMessageReferenceListApi;
import eu.europa.eu.dsd.messaging.gen.dataset.api.DsdDatasetMessageSubmissionApi;
import eu.europa.eu.dsd.messaging.gen.dataset.api.DsdDatasetPullMessageApi;
import eu.europa.eu.dsd.messaging.gen.dataset.invoker.ApiClient;
import eu.europa.eu.dsd.messaging.gen.dataset.model.*;
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
public class DSDDatasetMessagingService {
    private static final Logger LOG = LoggerFactory.getLogger(DSDDatasetMessagingService.class);
    private MessagingDatasetMapping mapper
            = Mappers.getMapper(MessagingDatasetMapping.class);

    DsdDatasetMessageSubmissionApi dsdDatasetMessageSubmissionApi;
    DsdDatasetPullMessageApi dsdDatasetPullMessageApi;
    DsdDatasetMessageReferenceListApi dsdDatasetMessageReferenceListApi;

    JwsService jwsService;
    DSDRequestLoggerDao dsdRequestLoggerDao;
    DSDClientHttpRequestInterceptor interceptor;

    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager;
    NationalBrokerProperties nationalBrokerProperties;

    @Autowired
    public DSDDatasetMessagingService(JwsService jwsService, DSDRequestLoggerDao dsdRequestLoggerDao, DSDClientHttpRequestInterceptor interceptor, AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager, NationalBrokerProperties nationalBrokerProperties) {
        this.jwsService = jwsService;
        this.dsdRequestLoggerDao = dsdRequestLoggerDao;
        this.interceptor = interceptor;
        this.authorizedClientManager = authorizedClientManager;
        this.nationalBrokerProperties = nationalBrokerProperties;
    }

    @PostConstruct
    public void init() {

        RestTemplate restTemplate = new DSDRestTemplate(jwsService, interceptor);
        ApiClient apiClient = new ApiClient(restTemplate);
        // add iso format without miliseconds
        apiClient.setDateFormat(new ISO8601DateFormat());
        if (nationalBrokerProperties.isOAuthSecurityEnabled()) {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("DSDOAuthClient")
                    .principal("NationalBrokerDSDClient")
                    .build();
            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
            OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
            apiClient.setAccessToken(accessToken.getTokenValue());
        }
        apiClient.setBasePath(nationalBrokerProperties.getDsdUrl());

        dsdDatasetMessageSubmissionApi = new DsdDatasetMessageSubmissionApi(apiClient);
        dsdDatasetPullMessageApi = new DsdDatasetPullMessageApi(apiClient);
        dsdDatasetMessageReferenceListApi = new DsdDatasetMessageReferenceListApi(apiClient);

    }


    public ServiceResult<DatasetRO> searchDatasets(String messageId, String userid, String originalSenderToken, int offset, int limit, String sort, String query) {
        LOG.info("Search Dataset by userid [{}] with the originalSenderToken [{}]", userid, originalSenderToken);
        // set call properties
        SearchParameters body = buildSearchQuery(offset, limit, sort, query);
        OffsetDateTime timestamp = OffsetDateTime.now();

        String finalRecipient = nationalBrokerProperties.getDsdFinalRecipient();
        dsdDatasetMessageSubmissionApi.getApiClient().setBasePath(nationalBrokerProperties.getDsdUrl());

        interceptor.setContextMessageId(messageId, userid);
        SearchResponseList responseList = dsdDatasetMessageSubmissionApi.datasetSearchMethodId(userid, originalSenderToken, finalRecipient, timestamp, messageId, body, null);
        interceptor.clearContextData();

        ServiceResult<DatasetRO> result = new ServiceResult<>();
        result.setCount(responseList.getResponseList().getCount());
        result.setPage(responseList.getResponseList().getPage());
        result.setFilter(responseList.getResponseList().getFilter());
        responseList.getResponseList().getServiceEntities().forEach(datasetRO -> result.getServiceEntities().add(mapper.messagingToRo(datasetRO)));

        return result;
    }

    protected SearchParameters buildSearchQuery(int offset, int limit, String sort, String query) {
        SearchParameters body = new SearchParameters();
        DatasetQuery queryObject = new DatasetQuery();
        queryObject.setLimit(limit);
        queryObject.setOffset(offset);

        DatasetFilterRO filterRO = DaoQueryUtils.generateFilterFromJson(query, DatasetFilterRO.class);
        List<String> sortOrder = DaoQueryUtils.getSortOrderList(sort);
        if (filterRO != null) {
            queryObject.setOrganizationIdentifier(filterRO.getOrganizationIdentifier());
            queryObject.setDatasetType(filterRO.getDatasetType());
            queryObject.setQueryId(filterRO.getQueryId());

        }
        queryObject.setSort(String.join(",", sortOrder));
        body.setSearchParameter(queryObject);
        return body;
    }

    public void updateDataset(DatasetRO datasetRO, String messageId, String userid, String originalSenderToken) {
        LOG.info("Update dataset");

        DatasetRequest body = new DatasetRequest();
        body.setDatasetUpdate(mapper.roToMessaging(datasetRO));
        // set call properties
        OffsetDateTime timestamp = OffsetDateTime.now();
        String finalRecipient = nationalBrokerProperties.getDsdFinalRecipient();
        dsdDatasetMessageSubmissionApi.getApiClient().setBasePath(nationalBrokerProperties.getDsdUrl());

        String responseWebhook = nationalBrokerProperties.getDsdWebhookUrl();

        interceptor.setContextMessageId(messageId, userid);
        SignalMessage response = dsdDatasetMessageSubmissionApi.datasetUpdateMethodId(userid, originalSenderToken, finalRecipient, timestamp, messageId, body, null, responseWebhook);
        interceptor.clearContextData();
        LOG.info("Response from server is: " + Json.pretty(response));
    }


    public void createDataset(DatasetRO datasetRO, String messageId, String userid, String originalSenderToken) {
        LOG.info("Create dataset");
        DatasetCreateRequest body = new DatasetCreateRequest();
        body.setDatasetCreate(mapper.roToMessaging(datasetRO));
        // set call properties
        OffsetDateTime timestamp = OffsetDateTime.now();
        String finalRecipient = nationalBrokerProperties.getDsdFinalRecipient();
        dsdDatasetMessageSubmissionApi.getApiClient().setBasePath(nationalBrokerProperties.getDsdUrl());

        interceptor.setContextMessageId(messageId, userid);
        SignalMessage response = dsdDatasetMessageSubmissionApi.datasetCreateMethodId(userid, originalSenderToken, finalRecipient, timestamp, messageId, body, null);
        interceptor.clearContextData();
        LOG.info("Response from server is: " + Json.pretty(response));
    }


    public void deleteDataset(DatasetRO datasetRO, String messageId, String userid, String originalSenderToken) {
        LOG.info("Create dataset");
        DatasetDeleteRequest body = new DatasetDeleteRequest();
        body.setDatasetDelete(mapper.roToMessaging(datasetRO));

        // set call properties
        OffsetDateTime timestamp = OffsetDateTime.now();
        String finalRecipient = nationalBrokerProperties.getDsdFinalRecipient();
        dsdDatasetMessageSubmissionApi.getApiClient().setBasePath(nationalBrokerProperties.getDsdUrl());

        interceptor.setContextMessageId(messageId, userid);
        SignalMessage response = dsdDatasetMessageSubmissionApi.datasetDeleteMethodId(userid, originalSenderToken, finalRecipient, timestamp, messageId, body, null);
        interceptor.clearContextData();
        LOG.info("Response from server is: " + Json.pretty(response));
    }

    public List<String> getReadyStatusMessageIds() {
        MessageReferenceList list = dsdDatasetMessageReferenceListApi.getMessageReferenceListForDataSetStatusId();
        return list.getMessageReferenceList().stream().map(messageReference -> messageReference.getMessageId()).collect(Collectors.toList());
    }

    public DatasetStatusResult getStatusMessage(String messageId) {
        LOG.info("getStatusMessage for id: [{}]", messageId);
        interceptor.setContextMessageId(messageId, "BROKER");
        MessageStatusResponse body = dsdDatasetPullMessageApi.getDataSetMessageId(messageId);
        interceptor.clearContextData();
        if (body == null) {
            LOG.info("Status response from server is null ");
            return null;
        }
        LOG.info("Status response from server has payload count: [{}]", body.getMessageStatusResponse());
        return body.getMessageStatusResponse();
    }
}
