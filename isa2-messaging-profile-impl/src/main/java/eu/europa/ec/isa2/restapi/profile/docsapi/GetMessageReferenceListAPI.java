package eu.europa.ec.isa2.restapi.profile.docsapi;


import eu.europa.ec.isa2.restapi.profile.annotation.GetMessageOperation;
import eu.europa.ec.isa2.restapi.profile.annotation.GetMessageReferenceListOperation;
import eu.europa.ec.isa2.restapi.profile.enums.MessageReferenceOperationType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RequestMapping("/v1")
@Tag(name=GetMessageReferenceListAPI.TAG_MESSAGE_REFERENCE, description = "This endpoints returns a list of message references available for pulling")
@Tag(name=GetMessageReferenceListAPI.TAG_GET_MESSAGE, description = "This endpoint returns a message for given service action and message identifier")
public interface GetMessageReferenceListAPI {

    String TAG_MESSAGE_REFERENCE = "Message Reference List";
    String TAG_GET_MESSAGE = "Pull Message";


    @GetMessageReferenceListOperation(
            tags = {TAG_MESSAGE_REFERENCE},
            operationId = "GetMessageReferenceListId",
            summary = "Get Message Reference List Endpoint",
            description = "This endpoint returns a list of message references available for pulling, following the Message Reference schema. ")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE},
            path = "/messaging")
    void getMessageReferenceList(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException;


    @GetMessageReferenceListOperation(
            tags = {TAG_MESSAGE_REFERENCE},
            operationType = MessageReferenceOperationType.SERVICE,
            operationId = "GetMessageReferenceListForServiceId",
            summary = "Get Message Reference List for service Endpoint",
            description = "This endpoint returns a list of message references available for pulling for a specific service, " +
                    "following the Message Reference schema.")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE},
            path = "/messaging/{service}")
    void getServiceMessageReferenceList(@PathVariable("service") String service,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws IOException;


    @GetMessageReferenceListOperation(
            tags = {TAG_MESSAGE_REFERENCE},
            operationType = MessageReferenceOperationType.SERVICE_AND_ACTION,
            operationId = "GetMessageReferenceListForServiceAndActionId",
            summary = "Get Message Reference List for service and action Endpoint",
            description = "This endpoint returns a list of message references available for pulling for a specific service and action, " +
                    "following the Message Reference schema." )
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE},
            path = "/messaging/{service}/{action}")
    void getServiceActionMessageReferenceList(@PathVariable("service") String service,
                                              @PathVariable("action") String action,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws IOException;


    @GetMessageOperation(
            tags = {TAG_GET_MESSAGE},
            operationId = "GetMessageId",
            summary = "Get Message Endpoint",
            description = "This endpoint returns the message filed under a specific service and action" +
                    "following the format for the User Message. ")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE,MediaType.MULTIPART_MIXED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            path = "/messaging/{service}/{action}/{messageId}")
    void getMessage(@PathVariable("service") String service,
                    @PathVariable("action") String action,
                    @PathVariable("messageId") String messageId,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws IOException;


}
