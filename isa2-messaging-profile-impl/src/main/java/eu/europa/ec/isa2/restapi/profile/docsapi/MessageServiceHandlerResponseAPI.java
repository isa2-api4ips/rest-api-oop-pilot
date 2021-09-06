package eu.europa.ec.isa2.restapi.profile.docsapi;


import eu.europa.ec.isa2.restapi.profile.annotation.SubmitResponseMessageOperation;
import eu.europa.ec.isa2.restapi.profile.annotation.SubmitSignalOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RequestMapping("/v1")
@Tag(name=MessageServiceHandlerResponseAPI.TAG_RESPONSE_MESSAGE_SUBMISSION, description = "The endpoints defined in this section enable pushing or signalling the availability of responses to initial messages to a Sever or Webhook Server.")
public interface MessageServiceHandlerResponseAPI {

    String TAG_RESPONSE_MESSAGE_SUBMISSION = "Response Message Submission";


    @SubmitResponseMessageOperation(
            tags = {TAG_RESPONSE_MESSAGE_SUBMISSION},
            operationId = "ResponseMessageSubmissionId",
            summary = "Response Message Submission Endpoint",
            description = "The Response Message Submission endpoint is the endpoint used for sending response messages " +
                    "in reply to a previously submitted message. It provides the endpoint to which a client sends the response message, as created by the final recipient.")
    @PostMapping(produces = {"application/json; charset=UTF-8"},
            consumes = {MediaType.MULTIPART_MIXED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            path = "/messaging/{service}/{action}/{messageId}/response/{rService}/{rAction}/{rMessageId}")
    void responseMessageSubmission(@PathVariable("service") String service,
                                              @PathVariable("action") String action,
                                              @PathVariable("messageId") String messageId,
                                              @PathVariable("rService") String rService,
                                              @PathVariable("rAction") String rAction,
                                              @PathVariable("rMessageId") String rMessageId,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws IOException;


    @SubmitResponseMessageOperation(
            tags = {TAG_RESPONSE_MESSAGE_SUBMISSION},
            operationId = "WebhookResponseMessageSubmissionId", isWebhook = true,
            summary = "Webhook Response Message Submission Endpoint",
            description = "The Webhook Response Message Submission endpoint is the webhook endpoint used for sending response messages " +
                    "in reply to a previously submitted message. It provides the endpoint to which a server sends the " +
                    "response message, as created by the final recipient. Table 6 provides an overview of the HTTP Multipart " +
                    "body and fields defined and required for the implementation of this endpoint.")
    @PostMapping(produces = {"application/json; charset=UTF-8"},
            consumes = {MediaType.MULTIPART_MIXED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            path = "messaging-webhook/{messageId}/response/{rService}/{rAction}/{rMessageId}")
    void webhookMessageSubmission( @PathVariable("messageId") String messageId,
                                   @PathVariable("rService") String rService,
                                   @PathVariable("rAction") String rAction,
                                   @PathVariable("rMessageId") String rMessageId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException;


    @SubmitSignalOperation(
            tags = {TAG_RESPONSE_MESSAGE_SUBMISSION},
            operationId = "WebhookSignalSubmissionId",
            summary = "Webhook Signal Submission Endpoint Endpoint",
            description = "The Webhook Signal Submission endpoint is the webhook endpoint used for signalling the availability of " +
                    "a response message in reply to a previously submitted message. It provides the endpoint to which a server signals the availability of the response message. "
    )
    @PostMapping(produces = {"application/json; charset=UTF-8"},
            consumes = {"application/json; charset=UTF-8"},
            path = "/messaging-webhook/{messageId}/response/signal")
    void webhookSignalMessageSubmission( @PathVariable("messageId") String messageId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException;


    @SubmitSignalOperation(
            tags = {TAG_RESPONSE_MESSAGE_SUBMISSION},
            isWebhook = false,
            operationId = "SignalSubmissionId",
            summary = "Signal Submission Endpoint ",
            description = "The  Signal Submission endpoint is used for signalling the successfully pulled message or to response errors/warnings at receiving messages."
    )
    @PostMapping(produces = {"application/json; charset=UTF-8"},
            consumes = {"application/json; charset=UTF-8"},
            path = "/messaging/{messageId}/response/signal")
    void signalMessageSubmission( @PathVariable("messageId") String messageId,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException;

}
