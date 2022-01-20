package eu.europa.ec.isa2.restapi.profile.docsapi;


import eu.europa.ec.isa2.restapi.profile.annotation.GetResponseMessageOperation;
import eu.europa.ec.isa2.restapi.profile.annotation.GetResponseMessageReferenceListOperation;
import eu.europa.ec.isa2.restapi.profile.enums.MessageReferencePullOperationType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RequestMapping("/v1")
@Tag(name = PullResponseMessageAPI.TAG_RESPONSE_MESSAGE_REFERENCE, description = "The endpoints return a list of response message references available for pulling.")
@Tag(name = PullResponseMessageAPI.TAG_GET_RESPONSE_MESSAGE, description = "The endpoint return a response message for a given service, action and message identifier.")
public interface PullResponseMessageAPI {

    String TAG_RESPONSE_MESSAGE_REFERENCE = "Response Message Reference List";
    String TAG_GET_RESPONSE_MESSAGE = "Pull Response Message";

    @GetResponseMessageReferenceListOperation(
            tags = {TAG_RESPONSE_MESSAGE_REFERENCE},
            operationId = "GetResponseMessageReferenceListId",
            summary = "Get Response Message Reference List Endpoint",
            description = "This endpoint returns a list of response message references available for pulling, " +
                    "following the Message Reference schema, representing responses to a previous message sent by the original sender.")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE},
            path = "/messaging/{service}/{action}/{messageId}/response")
    void getResponseMessageReferenceList(@PathVariable("service") String service,
                                         @PathVariable("action") String action,
                                         @PathVariable("messageId") String messageId,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException;


    @GetResponseMessageReferenceListOperation(
            tags = {TAG_RESPONSE_MESSAGE_REFERENCE},
            operationType = MessageReferencePullOperationType.RESPONSE_SERVICE,
            operationId = "GetResponseMessageReferenceListForServiceEndpointId",
            summary = "Get Response Message Reference List for service  Endpoint",
            description = "This endpoint returns a list of response message references available for pulling for a specific service, " +
                    "following the Message Reference schema, representing responses to a previous message sent by the original sender.")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE},
            path = "/messaging/{service}/{action}/{messageId}/response/{rService}")
    void getResponseMessageReferenceListForService(@PathVariable("service") String service,
                                                   @PathVariable("action") String action,
                                                   @PathVariable("messageId") String messageId,
                                                   @PathVariable("rService") String rService,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws IOException;


    @GetResponseMessageReferenceListOperation(
            tags = {TAG_RESPONSE_MESSAGE_REFERENCE},
            operationType = MessageReferencePullOperationType.RESPONSE_ACTION,
            operationId = "GetResponseMessageReferenceListForServiceAndActionEndpointId",
            summary = "Get Response Message Reference List for service and action Endpoint",
            description = "This endpoint returns a list of response message references available for pulling for a specific service and action, " +
                    "following the Message Reference schema, representing responses to a previous message sent by the original sender.")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE},
            path = "/messaging/{service}/{action}/{messageId}/response/{rService}/{rAction}")
    void getResponseMessageReferenceListForServiceAndAction(@PathVariable("service") String service,
                                                            @PathVariable("action") String action,
                                                            @PathVariable("messageId") String messageId,
                                                            @PathVariable("rService") String rService,
                                                            @PathVariable("rAction") String rAction,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) throws IOException;


    @GetResponseMessageOperation(
            tags = {TAG_GET_RESPONSE_MESSAGE},
            operationId = "GetResponseMessageId",
            summary = "Get Response Message Endpoint",
            description = "This endpoint returns the message filed under a specific service and action, following the format defined in the User Message section," +
                    " representing a response to a previous message sent by the original sender.")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_PROBLEM_JSON_VALUE, MediaType.MULTIPART_MIXED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            path = "/messaging/{service}/{action}/{messageId}/response/{rService}/{rAction}/{rMessageId}")
    void getResponseMessage(@PathVariable("service") String service,
                            @PathVariable("action") String action,
                            @PathVariable("messageId") String messageId,
                            @PathVariable("rService") String rService,
                            @PathVariable("rAction") String rAction,
                            @PathVariable("rMessageId") String rMessageId,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException;
}
