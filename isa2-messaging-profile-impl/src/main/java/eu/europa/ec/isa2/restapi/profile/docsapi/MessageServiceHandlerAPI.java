package eu.europa.ec.isa2.restapi.profile.docsapi;


import eu.europa.ec.isa2.restapi.profile.annotation.SubmitMessageOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@RequestMapping("/v1")
@Tag(name=MessageServiceHandlerAPI.TAG_MESSAGE_SUBMISSION, description = "The Message Submission endpoints are providing the endpoint to which a client sends the message")
public interface MessageServiceHandlerAPI {

    String TAG_MESSAGE_SUBMISSION = "Message Submission";

    @SubmitMessageOperation(
            tags = {TAG_MESSAGE_SUBMISSION},
            operationId = "AsynchronousMessageSubmission", useMessageWebhook = true, useSignalWebhook = true,
            summary = "Asynchronous Message Submission Endpoint",
            description = "The Message submission endpoint is the main endpoint of the messaging API. " +
                    "It provides the endpoint to which a client sends the message, as created by the original sender.")


    @PostMapping(produces = {"application/json; charset=UTF-8"},
            consumes = {MediaType.MULTIPART_MIXED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},

            path = "/messaging/{service}/{action}/{messageId}")
    void asynchronousMessageSubmission(@PathVariable("service") String service,
                                              @PathVariable("action") String action,
                                              @PathVariable("messageId") String messageId,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws IOException;



    @SubmitMessageOperation(sync = true,
            tags = {TAG_MESSAGE_SUBMISSION},
            operationId = "MessageSubmissionWithSynchronousResponse",
            summary = "Message Submission with Synchronous Response Endpoint",
            description = "The Message submission With Synchronous Response endpoint is the alternative endpoint of the messaging API. " +
                    "It provides the endpoint to which a client sends the message, as created by the original sender, and responds synchronously with a response message. ")

    @PostMapping(
            produces = { MediaType.MULTIPART_MIXED_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE},
            consumes = {MediaType.MULTIPART_MIXED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            path = "/messaging//{service}/{action}/{messageId}/sync")
    void synchronousMessageSubmission(@PathVariable("service") String service,
                                      @PathVariable("action") String action,
                                      @PathVariable("messageId") String messageId,
                                      HttpServletRequest request,
                                      HttpServletResponse response);


}
