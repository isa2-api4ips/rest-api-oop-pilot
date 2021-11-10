package eu.europa.ec.isa2.oop.restapi.docsapi;

import eu.europa.ec.isa2.oop.dsd.model.*;
import eu.europa.ec.isa2.restapi.profile.annotation.*;
import eu.europa.ec.isa2.restapi.profile.enums.MessageReferenceOperationType;
import eu.europa.ec.isa2.restapi.profile.enums.MessageReferencePullOperationType;
import eu.europa.ec.isa2.restapi.profile.model.MessageReferenceListRO;
import eu.europa.ec.isa2.restapi.profile.model.SignalMessage;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

import javax.ws.rs.Path;
import java.io.IOException;

import static eu.europa.ec.isa2.oop.restapi.docsapi.DSDOrganizationApi.*;
import static eu.europa.ec.isa2.oop.restapi.examples.OrganizationExample.*;


@Path("/v1/messaging/")
@Tag(name = TAG_DSD_ORGANIZATION, description = "DSD Organization REST Interface")
@Tag(name = TAG_GET_MESSAGE_LIST, description = "Services for returning list of ready message to pull")
@Tag(name = TAG_GET_RESPONSE_MESSAGE_LIST, description = "Services for returning list of ready response message to pull")
@Tag(name = TAG_GET_MESSAGE, description = "Services for pulling ready messages ")
@Tag(name = TAG_GET_RESPONSE_MESSAGE, description = "Services for pulling ready response messages ")

@SecurityRequirements({
        @SecurityRequirement(name = "DSD_ClientCredentials_OAuthSecurity", scopes = {"ROLE_DSD"})
        , @SecurityRequirement(name="DSD_Http_BearerTokenAuthorization", scopes = {"ROLE_DSD"})
})
public interface DSDOrganizationApi extends DSDOpenApiSecuritySchemes {

    String TAG_DSD_ORGANIZATION = "DSD Organization: Message submission";
    String TAG_GET_MESSAGE_LIST = "DSD Organization: Message Reference List";
    String TAG_GET_RESPONSE_MESSAGE_LIST = "DSD Organization: Response Message Reference List";
    String TAG_GET_MESSAGE = "DSD Organization: Pull Message";
    String TAG_GET_RESPONSE_MESSAGE = "DSD Organization: Pull Response Message";

    @SubmitMessageOperation(service = "organization", action = "query", sync = true,
            tags = {TAG_DSD_ORGANIZATION},
            operationId = "organizationSearchMethodId",
            summary = "DSD Mock: REST Interface to query mock organizations",
            description = "The Message Submission with synchronous Response of list of organizations.",
            requestTitle = "SearchParameters",
            requestDescription = "Payload contain search parameters",
            requestPayloads = {
                    @MultipartPayload(
                            name = "search-parameter",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = OrganizationSearchParameters.class,
                            example = ORGANIZATION_SEARCH
                    )
            },
            responseTitle = "SearchResponseList",
            responseDescription = "Payload contain list for search results",
            responsePayloads = {
                    @MultipartPayload(
                            name = "response-list",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = OrganizationSearchResult.class,
                            example = ORGANIZATION_RESULT
                    )
            })
    OrganizationSearchResult searchOrganizations(String service,
                                                 String action,
                                                 String messageId, OrganizationSearchParameters organization);

    @SubmitMessageOperation(service = "organization", action = "update", sync = false,
            tags = {TAG_DSD_ORGANIZATION},
            operationId = "organizationUpdateMethodId",
            summary = "DSD Component: Organization update request ",
            description = "Asynchronous DSD Organization update request submission. Service returns message " +
                    "acknowledgment signal. Request status can be obtained via pull organization/status operation",
            requestTitle = "UpdateOrganizationRequest",
            requestDescription = "Massage request contains Organization objects with updated organization data!",
            requestPayloads = {
                    @MultipartPayload(
                            name = "update-organization",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = OrganizationRO.class,
                            example = ORGANIZATION_UPDATE
                    )
            })
    void updateOrganization(String service,
                            String action,
                            String messageId, OrganizationRO organization);

    @GetMessageReferenceListOperation(
            tags = {TAG_GET_MESSAGE_LIST},
            operationId = "GetMessageReferenceListId",
            summary = "DSD Mock: Message Reference List Endpoint",
            description = "This endpoint returns a list of message references available for pulling, following the Message Reference schema.")
    MessageReferenceListRO getOrganizationMessageReferenceList();


    @GetMessageReferenceListOperation(
            tags = {TAG_GET_MESSAGE_LIST},
            operationType = MessageReferenceOperationType.SERVICE,
            service = "organization",
            operationId = "GetMessageReferenceListForOrganizationId",
            summary = "DSD Mock:Get Message Reference List for service Endpoint",
            description = "This endpoint returns a list of message references available for pulling for a Organization service, " +
                    "following the Message Reference schema.")
    MessageReferenceListRO getOrganizationServiceMessageReferenceList();


    @GetMessageReferenceListOperation(
            tags = {TAG_GET_MESSAGE_LIST},
            operationType = MessageReferenceOperationType.SERVICE_AND_ACTION,
            service = "organization",
            action = "status",
            operationId = "GetMessageReferenceListForOrganizationStatusId",
            summary = "DSD Mock: Get Message Reference List for service and action Endpoint",
            description = "This endpoint returns a list of message references available for pulling for a organization/status, " +
                    "following the Message Reference schema.")
    MessageReferenceListRO getOrganizationStatusMessageReferenceList();


    @GetResponseMessageReferenceListOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE_LIST},
            service = "organization",
            action = "update",
            operationId = "GetOrganizationResponseMessageReferenceListId",
            summary = "Get Response Message Reference List Endpoint",
            description = "This endpoint returns a list of response message references available for pulling, " +
                    "following the Message Reference schema, representing responses to a previous message sent by the original sender")
    MessageReferenceListRO getOrganizationStatusResponseMessageReferenceList(
            @PathVariable("messageId") String messageId);


    @GetResponseMessageReferenceListOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE_LIST},
            operationType = MessageReferencePullOperationType.RESPONSE_SERVICE,
            service = "organization",
            action = "update",
            responseService = "organization",
            operationId = "GetOrganizationResponseMessageReferenceListForServiceEndpointId",
            summary = "Get Response Message Reference List for service  Endpoint",
            description = "This endpoint returns a list of response message references available for pulling for a specific service, " +
                    "following the Message R    eference schema, representing responses to a previous message sent by the original sender.")
    MessageReferenceListRO getOrganizationStatusResponseMessageReferenceListForService(@PathVariable("messageId") String messageId);


    @GetResponseMessageReferenceListOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE_LIST},
            service = "organization",
            action = "update",
            responseService = "organization",
            responseAction = "status",
            operationType = MessageReferencePullOperationType.RESPONSE_ACTION,
            operationId = "GetOrganizationResponseMessageReferenceListForServiceAndActionEndpointId",
            summary = "Get Response Message Reference List for service and action Endpoint",
            description = "This endpoint returns a list of response message references available for pulling for a specific service and action, " +
                    "following the Message Reference schema, representing responses to a previous message sent by the original sender.")
    MessageReferenceListRO getOrganizationStatusResponseMessageReferenceListForServiceAndAction(@PathVariable("messageId") String messageId);


    @GetMessageOperation(
            tags = {TAG_GET_MESSAGE},
            service = "organization",
            action = "status",
            operationId = "GetOrganizationMessageId",
            summary = "Get Organization Message Endpoint",
            description = "This endpoint returns any status message for organization service" +
                    "following the format for the User Message. ",
            responseTitle = "MessageStatusResponse",
            responseDescription = "Payload contain status object for the update request",
            responsePayloads = {
                    @MultipartPayload(
                            name = "message-status-response",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = OrganizationStatusResult.class,
                            example = ORGANIZATION_RESULT
                    )
            })
    StatusResult<OrganizationRO> getOrganizationStatusMessage(@PathVariable("messageId") String messageId) throws IOException;

    @GetResponseMessageOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE},
            service = "organization",
            action = "update",
            responseService = "organization",
            responseAction = "status",
            operationId = "GetOrganizationResponseMessageId",
            summary = "Get Organization Response Message Endpoint",
            description = "This endpoint returns status message for organization update action " +
                    "following the format for the User Message. ",
            responseTitle = "MessageStatusResponse",
            responseDescription = "Payload contain status object for the update request",
            responsePayloads = {
                    @MultipartPayload(
                            name = "message-status-response",
                            contentType = MediaType.APPLICATION_JSON_VALUE,
                            instance = OrganizationStatusResult.class,
                            example = ORGANIZATION_RESULT
                    )
            })
    StatusResult<OrganizationRO> getOrganizationStatusResponseMessage(@PathVariable("messageId") String messageId,
                                                                      @PathVariable("rMessageId") String responseMessageId) throws IOException;


    @SubmitSignalOperation(
            tags = {TAG_DSD_ORGANIZATION},
            isWebhook = false,
            operationId = "OrganizationSignalSubmissionId",
            summary = "Organization Signal Submission Endpoint",
            description = "The Organization Signal Submission endpoint is used for signalling the successfully pulled message or to response errors/warnings at receiving messages.")
    void signalMessageSubmission(@PathVariable("messageId") String messageId, SignalMessage signalMessage);


}
