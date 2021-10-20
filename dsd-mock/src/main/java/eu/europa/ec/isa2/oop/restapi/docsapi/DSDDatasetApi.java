package eu.europa.ec.isa2.oop.restapi.docsapi;

import eu.europa.ec.isa2.oop.dsd.model.*;
import eu.europa.ec.isa2.restapi.profile.annotation.*;
import eu.europa.ec.isa2.restapi.profile.enums.MessageReferenceOperationType;
import eu.europa.ec.isa2.restapi.profile.enums.MessageReferencePullOperationType;
import eu.europa.ec.isa2.restapi.profile.model.MessageReferenceListRO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

import javax.ws.rs.Path;
import java.io.IOException;

import static eu.europa.ec.isa2.oop.restapi.docsapi.DSDDatasetApi.*;


@Path("/v1/messaging/")
@Tag(name = TAG_DSD_DATASET, description = "DSD dataset REST Interface")
@Tag(name = TAG_GET_MESSAGE_LIST, description = "Services for returning list of ready message to pull")
@Tag(name = TAG_GET_RESPONSE_MESSAGE_LIST, description = "Services for returning list of ready response message to pull")
@Tag(name = TAG_GET_MESSAGE, description = "Services for pulling ready messages ")
@Tag(name = TAG_GET_RESPONSE_MESSAGE, description = "Services for pulling ready response messages ")

@SecurityRequirements({
        @SecurityRequirement(name = "DSD_ClientCredentials_OAuthSecurity", scopes = {"ROLE_DSD"})
        , @SecurityRequirement(name="DSD_Http_BearerTokenAuthorization", scopes = {"ROLE_DSD"})
})
public interface DSDDatasetApi extends DSDOpenApiSecuritySchemes {
    String TAG_DSD_DATASET = "DSD dataset: Message submission";
    String TAG_GET_MESSAGE_LIST = "DSD dataset: Message Reference List";
    String TAG_GET_RESPONSE_MESSAGE_LIST = "DSD dataset: Response Message Reference List";
    String TAG_GET_MESSAGE = "DSD dataset: Pull Message";
    String TAG_GET_RESPONSE_MESSAGE = "DSD dataset: Pull Response Message";

    @SubmitMessageOperation(service = "dataset", action = "query", sync = true,
            tags = {TAG_DSD_DATASET},
            operationId = "datasetSearchMethodId",
            summary = "DSD Mock: REST Interface to query mock organizations",
            description = "The Message Submission with synchronous Response of list datasets.",
            requestTitle = "SearchParameters",
            requestDescription = "Payload contain search parameters",
            requestPayloads = {
                    @MultipartPayload(
                            name = "search-parameter",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = DatasetSearchParameters.class,
                            example = ""
                    )
            },
            responseTitle = "SearchResponseList",
            responseDescription = "Payload contain list for search results",
            responsePayloads = {
                    @MultipartPayload(
                            name = "response-list",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = DatasetSearchResult.class,
                            example = ""
                    )
            })
    DatasetSearchResult searchDatasets(String service,
                                       String action,
                                       String messageId, DatasetSearchParameters searchParameters);


    @SubmitMessageOperation(service = "dataset", action = "update", sync = false,
            useSignalWebhook = true,
            tags = {TAG_DSD_DATASET},
            operationId = "datasetUpdateMethodId",
            summary = "DSD Component: Dataset update request ",
            description = "Asynchronous DSD dataset update request submission. Service returns message " +
                    "acknowledgment signal. Request status can be obtained via pull dataset/status operation",
            requestTitle = "DatasetRequest",
            requestDescription = "Massage request contains Dataset objects with updated dataset data!",
            requestPayloads = {
                    @MultipartPayload(
                            name = "dataset-update",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = DatasetRO.class,
                            example = ""
                    )
            })
    void updateDataset(String service,
                       String action,
                       String messageId,
                       String webhookUrl,
                       DatasetRO organization);

    @SubmitMessageOperation(service = "dataset", action = "create", sync = false,
            tags = {TAG_DSD_DATASET},

            operationId = "datasetCreateMethodId",
            summary = "DSD Component: Dataset create request ",
            description = "Asynchronous DSD dataset create request submission. Service returns message " +
                    "acknowledgment signal. Request status can be obtained via pull dataset/status operation",
            requestTitle = "DatasetCreateRequest",
            requestDescription = "Massage request contains Dataset objects with new dataset data!",
            requestPayloads = {
                    @MultipartPayload(
                            name = "dataset-create",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = DatasetRO.class,
                            example = ""
                    )
            })
    void createDataset(String service,
                       String action,
                       String messageId, DatasetRO datasetRO);


    @SubmitMessageOperation(service = "dataset", action = "delete", sync = false,
            tags = {TAG_DSD_DATASET},
            operationId = "datasetDeleteMethodId",
            summary = "DSD Component: Dataset Delete request ",
            description = "Asynchronous DSD dataset Delete request submission. Service returns message " +
                    "acknowledgment signal. Request status can be obtained via pull dataset/status operation",
            requestTitle = "DatasetDeleteRequest",
            requestDescription = "Massage request contains Dataset objects with at least one dataset identifier!",
            requestPayloads = {
                    @MultipartPayload(
                            name = "dataset-delete",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = DatasetRO.class,
                            example = ""
                    )
            })
    void deleteDataset(String service,
                       String action,
                       String messageId, DatasetRO datasetRO);


    @GetMessageReferenceListOperation(
            tags = {TAG_GET_MESSAGE_LIST},
            operationType = MessageReferenceOperationType.SERVICE,
            service = "dataset",
            operationId = "GetMessageReferenceListForDataSetId",
            summary = "DSD Mock:Get Message Reference List for service Endpoint",
            description = "This endpoint returns a list of message references available for pulling for a DataSet service, " +
                    "following the Message Reference schema.")
    MessageReferenceListRO getDataSetServiceMessageReferenceList();


    @GetMessageReferenceListOperation(
            tags = {TAG_GET_MESSAGE_LIST},
            operationType = MessageReferenceOperationType.SERVICE_AND_ACTION,
            service = "dataset",
            action = "status",
            operationId = "GetMessageReferenceListForDataSetStatusId",
            summary = "DSD Mock: Get Message Reference List for service and action Endpoint",
            description = "This endpoint returns a list of message references available for pulling for a organization/status, " +
                    "following the Message Reference schema.")
    MessageReferenceListRO getDataSetStatusMessageReferenceList();


    @GetResponseMessageReferenceListOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE_LIST},
            service = "dataset",
            action = "update",
            operationId = "GetDataSetResponseMessageReferenceListId",
            summary = "Get Response Message Reference List Endpoint",
            description = "This endpoint returns a list of response message references available for pulling, " +
                    "following the Message Reference schema, representing responses to a previous message sent by the original sender")
    MessageReferenceListRO getDataSetStatusResponseMessageReferenceList(
            @PathVariable("messageId") String messageId);


    @GetResponseMessageReferenceListOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE_LIST},
            operationType = MessageReferencePullOperationType.RESPONSE_SERVICE,
            service = "dataset",
            action = "update",
            responseService = "dataset",
            operationId = "GetDataSetResponseMessageReferenceListForServiceEndpointId",
            summary = "Get Response Message Reference List for service  Endpoint",
            description = "This endpoint returns a list of response message references available for pulling for a specific service, " +
                    "following the Message R    eference schema, representing responses to a previous message sent by the original sender. ")
    MessageReferenceListRO getDataSetStatusResponseMessageReferenceListForService(@PathVariable("messageId") String messageId);


    @GetResponseMessageReferenceListOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE_LIST},
            service = "dataset",
            action = "update",
            responseService = "dataset",
            responseAction = "status",
            operationType = MessageReferencePullOperationType.RESPONSE_ACTION,
            operationId = "GetDataSetResponseMessageReferenceListForServiceAndActionEndpointId",
            summary = "Get Response Message Reference List for service and action Endpoint",
            description = "This endpoint returns a list of response message references available for pulling for a specific service and action, " +
                    "following the Message Reference schema, representing responses to a previous message sent by the original sender. ")
    MessageReferenceListRO getDataSetStatusResponseMessageReferenceListForServiceAndAction(@PathVariable("messageId") String messageId);


    @GetMessageOperation(
            tags = {TAG_GET_MESSAGE},
            service = "dataset",
            action = "status",
            operationId = "GetDataSetMessageId",
            summary = "Get DataSet Message Endpoint",
            description = "This endpoint returns any status message for organization service" +
                    "following the format for the User Message. ",
            responseTitle = "MessageStatusResponse",
            responseDescription = "Payload contain status object for the update request",
            responsePayloads = {
                    @MultipartPayload(
                            name = "message-status-response",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = DatasetStatusResult.class,
                            example = ""
                    )
            })
    StatusResult<DatasetRO> getDataSetStatusMessage(@PathVariable("messageId") String messageId) throws IOException;

    @GetResponseMessageOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE},
            service = "dataset",
            action = "update",
            responseService = "dataset",
            responseAction = "status",
            operationId = "GetDataSetResponseMessageId",
            summary = "Get DataSet Response Message Endpoint",
            description = "This endpoint returns status message for organization update action " +
                    "following the format for the User Message. ",
            responseTitle = "MessageStatusResponse",
            responseDescription = "Payload contain status object for the update request",
            responsePayloads = {
                    @MultipartPayload(
                            name = "message-status-response",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = DatasetStatusResult.class,
                            example = ""
                    )
            })
    StatusResult<DatasetRO> getDataSetStatusResponseMessage(@PathVariable("messageId") String messageId,
                                                            @PathVariable("rMessageId") String responseMessageId) throws IOException;

    @SubmitSignalOperation(isWebhook = true,
            tags = {TAG_DSD_DATASET},
            operationId = "datasetSubmitSignalWebhookMethodId",
            summary = "DSD Component: Dataset update webhook ",
            description = "Asynchronous DSD dataset update request submission. Service returns message " +
                    "acknowledgment signal. Request status can be obtained via pull dataset/status operation")
    // this is dummy implementation just for api generation - it must be generated by the "client server"
    default void submitSignal(String service,
                              String action,
                              String messageId, DatasetRO organization){

    };

    @SubmitResponseMessageOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE},
            isWebhook = true,
            responseService = "dataset",
            responseAction = "status",
            operationId = "SubmitStatusResponseWebhookMessageOperation",
            summary = "Submit DataSet Response Message to webhook endpoint",
            description = "This endpoint returns status message for dataset update action" +
                    "following the format for the dataset Message. ",
            responseTitle = "MessageWebhookStatusResponse",
            responseDescription = "Payload contain status object for the update request",
            requestPayloads =  {
                    @MultipartPayload(
                            name = "message-webhook-status-response",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = DatasetStatusResult.class,
                            example = ""
                    )
            })
    default void getDataSetStatusResponseMessage(@PathVariable("messageId") String messageId,
                                                 @PathVariable("rMessageId") String responseMessageId,
                                                 StatusResult<DatasetRO> statusResult){}

}
