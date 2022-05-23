package eu.europa.ec.isa2.oop.restapi.controller.profile.controllers;


import com.fasterxml.jackson.core.JsonParser;
import eu.europa.ec.isa2.oop.dsd.property.DsdMockProperties;
import eu.europa.ec.isa2.oop.restapi.APIRegistration;
import eu.europa.ec.isa2.oop.restapi.config.OpenApiConfig;
import eu.europa.ec.isa2.restapi.jws.SignedMimeMultipart;
import eu.europa.ec.isa2.restapi.profile.GeneralOpenApi;
import eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants;
import eu.europa.ec.isa2.restapi.profile.controllers.InputStreamDataSource;
import eu.europa.ec.isa2.restapi.profile.docsapi.MessageSubmissionEndpointAPI;
import eu.europa.ec.isa2.restapi.profile.docsapi.PullMessageAPI;
import eu.europa.ec.isa2.restapi.profile.docsapi.PullResponseMessageAPI;
import eu.europa.ec.isa2.restapi.profile.docsapi.ResponseMessageSubmissionEndpointAPI;
import eu.europa.ec.isa2.restapi.profile.docsapi.exceptions.MessagingAPIException;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingEndpointType;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import eu.europa.ec.isa2.restapi.profile.model.APIProblem;
import eu.europa.ec.isa2.restapi.profile.model.SignalMessage;
import eu.europa.ec.isa2.restapi.reader.MessagingAPIOperation;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.jades.HTTPHeader;
import eu.europa.esig.dss.jades.HTTPHeaderDigest;
import eu.europa.esig.dss.model.CommonDocument;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static eu.europa.ec.isa2.restapi.profile.enums.APIProblemType.*;

@RestController
@CrossOrigin(origins = "*")

@SecuritySchemes({
        @SecurityScheme(name = "DSD_ClientCredentials_OAuthSecurity"
                , description = "<p>Client Credentials authorization between National Broker and DSD using OKTA authorization server.<br/></p>" +
                " <p><b>Important Note:</b>OKTA Oauth server does not allow client credentials authorization from web browser like when using Swagger UI. " +
                "When testing the services with Swagger UI, use <b>DSD_Http_BearerTokenAuthorization</b> to provide an access token that has been obtained previously outside of Swagger UI.</p>"
                , type = SecuritySchemeType.OAUTH2
                , flows = @OAuthFlows(
                clientCredentials = @OAuthFlow(tokenUrl = "properties:dsd.oauth2.spring.security.token.url"
                        , scopes = @OAuthScope(name = "ROLE_DSD", description = "Authorization DSD token"))))
        ,
        @SecurityScheme(name = "DSD_Http_BearerTokenAuthorization"
                , description = "OKTA OAuth server does not allow client credentials authorization from web browser. Hence providing Bearer Token Authorization for use in **Swagger UI**. <br/><br/>" +
                "**Note:** this is only a workaround for a limitation of using Swagger UI. For system integration please use **DSD_ClientCredentials_OAuthSecurity** . <br/><br/>" +
                "**Usage:** Submit an HTTPS POST request to the token url of **DSD_ClientCredentials_OAuthSecurity** with body contents **grant_type=client_credentials** and **scope=ROLE_DSD** in **x-www-form-encoded** format. " +
                "As HTTP basic authorization send the **Client ID** and **Client Secret** of the National Broker. The OKTA server will return a JSON response containing an access token as a JWT." +
                "Submit the JWT in this **DSD_Http_BearerTokenAuthorization**."
                , type = SecuritySchemeType.HTTP
                , scheme = "Bearer"
                , bearerFormat = "Opaque")
})

@SecurityRequirements({
        @SecurityRequirement(name = "DSD_ClientCredentials_OAuthSecurity", scopes = {"ROLE_DSD"})
        , @SecurityRequirement(name = "DSD_Http_BearerTokenAuthorization", scopes = {"ROLE_DSD"})
})
public class MessageServiceHandlerController extends GeneralOpenApi
        implements MessageSubmissionEndpointAPI,
        ResponseMessageSubmissionEndpointAPI,
        PullMessageAPI,
        PullResponseMessageAPI {

    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceHandlerController.class);

    APIRegistration apiDocuments;
    JwsService jwsService;
    DsdMockProperties dsdMockProperties;

    /**
     * Sign headers if exits
     */
    public static final List<MessagingParameterType> SIGN_HEADERS = Arrays.asList(
            MessagingParameterType.ORIGINAL_SENDER,
            MessagingParameterType.ORIGINAL_SENDER_TOKEN,
            MessagingParameterType.FINAL_RECIPIENT,
            MessagingParameterType.MESSAGE_ID_HEADER,
            MessagingParameterType.SERVICE_HEADER,
            MessagingParameterType.ACTION_HEADER
    );

    @Autowired
    public MessageServiceHandlerController(APIRegistration controller, JwsService jwsService, DsdMockProperties dsdMockProperties) {
        super(MessageServiceHandlerController.class, OpenApiConfig.MESSAGING_API_GROUP);
        this.apiDocuments = controller;
        this.jwsService = jwsService;
        this.dsdMockProperties = dsdMockProperties;
    }

    /**
     * The generic asynchronous Message submission endpoint implementation.  It provides the endpoint for submission the message
     * and returning only acknowledgment.
     * The method locates by service and action registered implementations sync submission endpoints and invokes it.
     * The results Error as defined in Problem Schema or acknowledge as defined in SignalMessage if message invoking is successful,
     *
     * @param service   A representation of the service the message is submitted to
     * @param action    A representation of the action related to the service the message is submitted to
     * @param messageId - The identifier of the message being submitted. It MUST be generated by the client submitting the message
     * @param request   - HttpServletRequest
     * @param response  - HttpServletResponse object to which response is written to.
     */

    //@PreAuthorize("hasRole('ROLE_UPDATE_DSD')")
    public void asynchronousMessageSubmission(String service,
                                              String action,
                                              String messageId,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {

        LOG.debug("asynchronousMessageSubmission service:[{}], action :[{}], messageId:[{}]", service, action, messageId);

        messageSubmissionPrivate(MessagingEndpointType.SUBMIT_MESSAGE_ASYNC, request, service, action, messageId);
        // return  Acknowledge SignalMessage
        responseAcceptMessage(request, response);
    }

    /**
     * The generic synchronous Message submission endpoint implementation.  It provides the endpoint for submission the message
     * and returning the "user message" response.
     * The method locates by service and action registered implementations sync submission endpoints and invokes it. The result of the method
     * is serialized and returned as multipart/mixed object.
     *
     * @param service   - A representation of the service the message is submitted to
     * @param action    - A representation of the action related to the service the message is submitted to
     * @param messageId - The identifier of the message being submitted. It MUST be generated by the client submitting the message
     * @param request   - HttpServletRequest
     * @param response  - HttpServletResponse object to which response is written to.
     */

    @Override
    public void synchronousMessageSubmission(String service,
                                             String action,
                                             String messageId,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        LOG.info("*****************************************************************");
        LOG.info("synchronousMessageSubmission service:[{}], action :[{}], messageId:[{}] request: [{}]", service, action, messageId, request);

        Object result = messageSubmissionPrivate(MessagingEndpointType.SUBMIT_MESSAGE_SYNC, request, service, action, messageId);

        //TODO: This logic is ONLY for demo purposes!
        String originalSender = request.getHeader(MessagingParameterType.ORIGINAL_SENDER.getName());
        String finalRecipient = request.getHeader(MessagingParameterType.FINAL_RECIPIENT.getName());
        response.setHeader(MessagingParameterType.ORIGINAL_SENDER.getName(), finalRecipient);
        response.setHeader(MessagingParameterType.ORIGINAL_SENDER_TOKEN.getName(), jwsService.createOriginalSenderToken(finalRecipient));
        response.setHeader(MessagingParameterType.FINAL_RECIPIENT.getName(), originalSender);
        //TODO: response service and action are part of business logic - defined by business message exchange.
        // This implementation is just for DEMO!!
        response.setHeader(MessagingParameterType.SERVICE_HEADER.getName(), service);
        response.setHeader(MessagingParameterType.ACTION_HEADER.getName(), action + "-response");
        response.setHeader(MessagingParameterType.MESSAGE_ID_HEADER.getName(), UUID.randomUUID().toString());

        respondMultipartFromJson(result, response);

    }


    /**
     * "The Response Message Submission endpoint is the endpoint used for sending response messages
     *
     * @param service    - A representation of the service the message is submitted to
     * @param action     - A representation of the action related to the service the message is submitted to
     * @param messageId  - The identifier of the message being submitted. It MUST be generated by the client submitting the message
     * @param rService
     * @param rAction
     * @param rMessageId
     * @param request    - HttpServletRequest
     * @param response   - HttpServletResponse object to which response is written to.
     */
    @Override
    public void responseMessageSubmission(String service,
                                          String action,
                                          String messageId,
                                          String rService,
                                          String rAction,
                                          String rMessageId,
                                          HttpServletRequest request, HttpServletResponse response) {

        LOG.info("responseMessageSubmission,  service:[{}], action :[{}], messageId:[{}],  rService:[{}], rAction :[{}], rMessageId:[{}], request: [{}],",
                service, action, messageId, rService, rAction, rMessageId, request);

        messageSubmissionPrivate(MessagingEndpointType.SUBMIT_RESPONSE_MESSAGE, request, service, action, messageId, rService, rAction, rMessageId);
        // return  Acknowledge SignalMessage
        responseAcceptMessage(request, response);
    }

    @Override
    public void webhookMessageSubmission(String messageId, String rService, String rAction, String rMessageId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("webhookMessageSubmission, messageId:[{}],  rService:[{}], rAction :[{}], rMessageId:[{}], request: [{}],",
                messageId, rService, rAction, rMessageId, request);
        messageSubmissionPrivate(MessagingEndpointType.SUBMIT_SIGNAL_WEBHOOK, request, null, null, messageId, rService, rAction, rMessageId);
        // return  Acknowledge SignalMessage
        responseAcceptMessage(request, response);
    }

    @Override
    public void webhookSignalMessageSubmission(String messageId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("webhookSignalMessageSubmission, messageId:[{}], request: [{}],", messageId, request);
        signalMessageSubmissionPrivate(MessagingEndpointType.SUBMIT_SIGNAL_WEBHOOK, request, messageId);
        // return Acknowledge SignalMessage
        responseAcceptMessage(request, response);
    }

    @Override
    public void signalMessageSubmission(String messageId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("signalMessageSubmission, messageId:[{}], request: [{}],", messageId, request);
        signalMessageSubmissionPrivate(MessagingEndpointType.SUBMIT_SIGNAL, request, messageId);
        // return Acknowledge SignalMessage
        responseAcceptMessage(request, response);
    }


    @Override
    public void getMessageReferenceList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("getMessageReferenceListId [{}]", request);
        getServiceActionMessageReferenceListPrivate(null, null, request, response);
    }

    @Override
    public void getServiceMessageReferenceList(String service, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("getServiceMessageReferenceList service [{}], request [{}]", service, request);
        getServiceActionMessageReferenceListPrivate(service, null, request, response);
    }

    @Override
    public void getServiceActionMessageReferenceList(String service, String action, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("getServiceActionMessageReferenceList service [{}], action [{}], request [{}]", service, action, request);
        getServiceActionMessageReferenceListPrivate(service, action, request, response);
    }

    @Override
    public void getResponseMessageReferenceList(String service, String action, String messageId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("getResponseMessageReferenceList service [{}], action [{}], messageId [{}], request [{}]", service, action, messageId, request);
        getServiceActionResponseMessageReferenceListPrivate(service, action, messageId, null, null, request, response);
    }

    @Override
    public void getResponseMessageReferenceListForService(String service, String action, String messageId, String rService, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("getResponseMessageReferenceListForService service [{}], action [{}], messageId [{}],  rservice [{}], request [{}]",
                service, action, messageId, rService, request);
        getServiceActionResponseMessageReferenceListPrivate(service, action, messageId, rService, null, request, response);
    }

    @Override
    public void getResponseMessageReferenceListForServiceAndAction(String service, String action, String messageId,
                                                                   String rService, String rAction, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("getResponseMessageReferenceListForService service [{}], action [{}], messageId [{}],  rservice [{}], rAction [{}], request [{}]",
                service, action, messageId, rService, rAction, request);
        getServiceActionResponseMessageReferenceListPrivate(service, action, messageId, rService, rAction, request, response);
    }

    @Override
    public void getMessage(String service, String action, String messageId, HttpServletRequest request, HttpServletResponse response) throws IOException {

        LOG.info("getMessage service [{}], action [{}], messageId [{}], request [{}]", service, action, messageId, request);
        Object result = getResponseMessagePrivate(MessagingEndpointType.GET_MESSAGE, request, service, action, messageId, null, null, null);


        //TODO: This logic is ONLY for demo purposes! Original sender and responder MUST be retrieved from "backedn!"
        String originalSender = getMessageOriginalSender(messageId);
        String finalRecipient = getMessageFinalRecipient(messageId);
        response.setHeader(MessagingParameterType.ORIGINAL_SENDER.getName(), originalSender);
        response.setHeader(MessagingParameterType.ORIGINAL_SENDER_TOKEN.getName(), jwsService.createOriginalSenderToken(originalSender));
        response.setHeader(MessagingParameterType.FINAL_RECIPIENT.getName(), finalRecipient);

        respondMultipartFromJson(result, response);
    }


    @Override
    public void getResponseMessage(String service, String action, String messageId, String rService, String rAction, String rMessageId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("getResponseMessage service [{}], action [{}], messageId [{}], rService [{}], rAction [{}], rMessageId [{}], request [{}]",
                service, action, messageId, rService, rAction, rMessageId, request);

        Object result = getResponseMessagePrivate(MessagingEndpointType.GET_RESPONSE_MESSAGE, request, service, action, messageId, rService, rAction, rMessageId);
        //TODO: This logic is ONLY for demo purposes! Original sender and responder MUST be retrieved from "backedn!"

        String originalSender = getMessageOriginalSender(messageId);
        String finalRecipient = getMessageFinalRecipient(messageId);
        response.setHeader(MessagingParameterType.ORIGINAL_SENDER.getName(), originalSender);
        response.setHeader(MessagingParameterType.ORIGINAL_SENDER_TOKEN.getName(), jwsService.createOriginalSenderToken(originalSender));
        response.setHeader(MessagingParameterType.FINAL_RECIPIENT.getName(), finalRecipient);

        respondMultipartFromJson(result, response);
    }


    public Object getResponseMessagePrivate(MessagingEndpointType type, HttpServletRequest request,
                                            String service, String action, String messageId, String rService, String rAction, String rMessageId) {

        logRequestHeaders(request);
        MessagingAPIOperation operation = apiDocuments.getMethodByType(type, service, action, rService, rAction);
        List<Object> params = type == MessagingEndpointType.GET_MESSAGE ? Arrays.asList(messageId) :
                Arrays.asList(messageId, rMessageId);
        try {
            return operation.getMethod().invoke(operation.getObject(), params.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    "Error occurred while processing the request",
                    request.getServletPath(), e);
        }
    }


    /**
     * Log message headers!
     *
     * @param request
     */
    public void logRequestHeaders(HttpServletRequest request) {
        Collections.list(request.getHeaderNames()).forEach(headerValue -> LOG.debug("[{}]: [{}]", headerValue, request.getHeader(headerValue)));
    }

    /**
     * Message submission method implementation for types
     *
     * @param service
     * @param action
     * @param messageId
     * @param request
     * @param type
     * @return
     */
    public Object messageSubmissionPrivate(MessagingEndpointType type, HttpServletRequest request, String service,
                                           String action,
                                           String messageId) {

        return messageSubmissionPrivate(type, request, service, action, messageId, null, null, null);
    }

    public Object messageSubmissionPrivate(MessagingEndpointType type, HttpServletRequest request, String service,
                                           String action,
                                           String messageId,
                                           String rService,
                                           String rAction,
                                           String rMessageId) {


        logRequestHeaders(request);
        InputStream inputStream = validateRequest(request);
        // validate body
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    String.format("Multipart content is expected for the service [%s] and action: [%s] ", service, action),
                    request.getServletPath());
        }
        MimeMultipart multipart = extractPostRequestBody(inputStream);

        MessagingAPIOperation operation = apiDocuments.getMethodByType(type, service, action);

        Class[] paramTypes = validateParameterCount(operation, multipart);

        List<Object> params = new ArrayList<>();
        params.addAll(Arrays.asList(service, action, messageId));
        if (operation.isUseMessageWebhook()) {
            String webhookUrl = request.getHeader(MessagingParameterType.RESPONSE_WEBHOOK.getName());
            params.add(webhookUrl);
        }
        if (operation.isUseSignalWebhook()) {
            String webhookUrl = request.getHeader(MessagingParameterType.SIGNAL_WEBHOOK.getName());
            params.add(webhookUrl);
        }

        try {
            params.addAll(getParameters(multipart, paramTypes, operation));
        } catch (IOException | MessagingException e) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    "Error occurred while parsing multipart payloads",
                    request.getServletPath(), e);
        }

        try {
            return operation.getMethod().invoke(operation.getObject(), params.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    "Error occurred while processing the request",
                    request.getServletPath(), e);
        }
    }

    public String getMessageOriginalSender(String messageId) {
        // TODO implement logic for retrieving original sender per message id! This is just for DEMO
        return dsdMockProperties.getDemoDsdOriginalSender();
    }

    public String getMessageFinalRecipient(String messageId) {
        // TODO implement logic for retrieving final recipient  per message id! This is just for DEMO
        return dsdMockProperties.getDemoDsdFinalRecipient();
    }

    public void signalMessageSubmissionPrivate(MessagingEndpointType type, HttpServletRequest request, String messageId) throws IOException {
        logRequestHeaders(request);
        InputStream inputStream = validateRequest(request);
        MessagingAPIOperation operation = apiDocuments.getMethodByType(type, null, null);

        // create signal object
        JsonParser parser = Json.mapper().createParser(inputStream);
        SignalMessage message = parser.readValueAs(SignalMessage.class);
        List<Object> params = Arrays.asList(messageId, message);
        try {
            operation.getMethod().invoke(operation.getObject(), params.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    "Error occurred while processing the request",
                    request.getServletPath(), e);
        }
    }

    public void getServiceActionMessageReferenceListPrivate(String service, String action, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logRequestHeaders(request);
        List<MessagingAPIOperation> methods = apiDocuments.getMessageReferenceListMethods(service, action);
        if (methods.isEmpty()) {
            String endpoint = "/messaging" + (StringUtils.isBlank(service) ? "" : "/" + service +
                    (StringUtils.isBlank(action) ? "" : "/" + action));
            respondProblem(VALIDATION_FAILED, response,
                    String.format("No method found for endpoint [%s]", endpoint),
                    endpoint
            );
            return;
        }
        MessagingAPIOperation operation = methods.get(0);
        Object result;
        try {
            result = operation.getMethod().invoke(operation.getObject());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    "Error occurred while processing the request",
                    request.getServletPath(), e);
        }

        responseJsonObject(result, response);
    }

    public void getServiceActionResponseMessageReferenceListPrivate(String service, String action, String messageId, String responseService, String responseAction, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logRequestHeaders(request);

        List<MessagingAPIOperation> methods = apiDocuments.getResponseMessageReferenceMethods(service, action, responseService, responseAction);
        if (methods.isEmpty()) {
            String endpoint = "/messaging" + "/" + service + "/" + action + "/" + messageId + "/response"
                    + (StringUtils.isBlank(responseService) ? "" : "/" + responseService +
                    (StringUtils.isBlank(responseAction) ? "" : "/" + responseAction));
            respondProblem(VALIDATION_FAILED, response,
                    String.format("No method found for endpoint [%s]", endpoint),
                    endpoint
            );
            return;
        }
        List<Object> params = Arrays.asList(messageId);
        MessagingAPIOperation operation = methods.get(0);
        Object result = null;
        try {
            result = operation.getMethod().invoke(operation.getObject(), params.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    "Error occurred while processing the request",
                    request.getServletPath(), e);
        }
        responseJsonObject(result, response);
    }


    public void responseJsonObject(Object json, HttpServletResponse response) {
        Map<String, String> signIfExists = getHeadersToSign(response, true);
        responseJsonObject(json, response, MediaType.APPLICATION_JSON, signIfExists);
    }

    public void responseJsonObject(Object json, HttpServletResponse response, MediaType mediaType, Map<String, String> headers) {
        jwsService.signJsonResponse(json, response, mediaType, headers);
    }

    public void respondProblem(APIProblemType type, HttpServletResponse response, String details, String instance) {
        // return problem

        response.setStatus(type.getStatus());
        Map<String, String> signIfExists = getHeadersToSign(response, true);
        APIProblem problem = new APIProblem(type, details, instance);
        responseJsonObject(problem, response, MediaType.APPLICATION_PROBLEM_JSON, signIfExists);
    }

    public Map<String, String> getHeadersToSign(HttpServletResponse response, boolean signal) {
        Map<String, String> signIfExists = new HashMap<>();
        // put all header from response if they exists
        SIGN_HEADERS.forEach(parameterType -> {
            if (response.containsHeader(parameterType.getName())) {
                signIfExists.put(parameterType.getName(), response.getHeader(parameterType.getName()));
            }
        });

        // set the right datetime format
        if (!signIfExists.containsKey(MessagingParameterType.TIMESTAMP.getName())) {
            signIfExists.put(MessagingParameterType.TIMESTAMP.getName(), OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        } else {
            // just for demo reformat the timestamp to truncate ms  -:)
            String strTimestamp = signIfExists.get(MessagingParameterType.TIMESTAMP.getName());
            OffsetDateTime dateTime = OffsetDateTime.parse(strTimestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            signIfExists.put(MessagingParameterType.TIMESTAMP.getName(),
                    dateTime.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));
        }
        // json signal or reference message must have message id header!
        if (signal && !signIfExists.containsKey(MessagingParameterType.MESSAGE_ID_HEADER.getName())) {

            signIfExists.put(MessagingParameterType.MESSAGE_ID_HEADER.getName(), UUID.randomUUID().toString());
        }

        return signIfExists;
    }

    public void respondMultipartFromJson(Object jsonPayload, HttpServletResponse response) {
        try {
            Map<String, String> signIfExists = getHeadersToSign(response, false);

            SignedMimeMultipart signedMimeMultipart = jwsService.createSignedMimeMultipartFromJson(jsonPayload, signIfExists);
            // set headers
            signedMimeMultipart.getHttpHeaders().forEach((header, value) -> response.setHeader(header, value));
            // write to
            signedMimeMultipart.writeTo(response.getOutputStream());

        } catch (IOException | MessagingException e) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    "Error occurred while writing response to out channel",
                    null, e);
        }
    }

    public void signAndRespond(CommonDocument document, HttpServletResponse response, Map<String, String> headers) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        DigestAlgorithm algorithm = DigestAlgorithm.forName(dsdMockProperties.getPayloadDigestAlgorithm());

        List<DSSDocument> headersToSign = new ArrayList<>();
        headers.forEach((key, value) -> headersToSign.add(new HTTPHeader(key, value)));
        // set digest
        headersToSign.add(new HTTPHeaderDigest(document, algorithm));


        response.addHeader(MessagingParameterType.EDEL_MESSAGE_SIG.getName(), jwsService.signMessageHeaders(headersToSign));

        headersToSign.forEach(header -> response.addHeader(header.getName(), ((HTTPHeader) header).getValue()));

        document.writeTo(response.getOutputStream());

    }


    /**
     * MessagingAPIException error handling. Methods writes APIProblemType as defined in messaging API Problem scheme
     * to the response stream
     *
     * @param request   - servlet request
     * @param response  - servlet response object
     * @param exception - messaging api exception
     */
    @ExceptionHandler(MessagingAPIException.class)
    public void handleError(HttpServletRequest request, HttpServletResponse response, MessagingAPIException exception) {
        LOG.error("Request url [{}], response error [{}]", request.getRequestURL(), exception);

        respondProblem(exception.getType(), response,
                exception.getDetails(),
                StringUtils.isBlank(exception.getInstance()) ? request.getServletPath() : exception.getInstance());
    }

    /**
     * Respond message accepted
     *
     * @param request  incoming request - needed to retrieve request body digest!
     * @param response - response
     */
    public void responseAcceptMessage(HttpServletRequest request,
                                      HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setStatus(MESSAGE_ACCEPTED.getStatus());
        SignalMessage signalMessage = new SignalMessage(MESSAGE_ACCEPTED,
                request.getServletPath(), request.getHeader(MessagingConstants.HEADER_DIGEST));
        responseJsonObject(signalMessage, response);
    }


    public List<Object> getParameters(MimeMultipart multipart, Class[] paramTypes, MessagingAPIOperation operation) throws MessagingException, IOException {
        List<Object> params = new ArrayList<>();
        // skip first 3 parameters
        int defaultParameterCount = 3; //service, action, messageOd
        if (operation.isUseMessageWebhook()) {
            defaultParameterCount++;
        }
        if (operation.isUseSignalWebhook()) {
            defaultParameterCount++;
        }

        for (int i = defaultParameterCount; i < paramTypes.length; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i - defaultParameterCount);
            JsonParser parser = Json.mapper().createParser(bodyPart.getInputStream());
            params.add(parser.readValueAs(paramTypes[i]));
        }
        return params;
    }

    MimeMultipart extractPostRequestBody(InputStream inputStream) {
        try {
            return new MimeMultipart(new InputStreamDataSource(inputStream, "payload"));
        } catch (MessagingException e) {
            throw new MessagingAPIException(INTERNAL_SERVER_ERROR, "Error occurred while parsing the request multipart-body!", null, e);
        }
    }

    public Class[] validateParameterCount(MessagingAPIOperation operation, Multipart multipart) {
        // validate parameter count
        Class[] paramTypes = operation.getMethod().getParameterTypes();
        int defaultParameterCount = 3; //service, action, messageOd
        if (operation.isUseMessageWebhook()) {
            defaultParameterCount++;
        }
        if (operation.isUseSignalWebhook()) {
            defaultParameterCount++;
        }
        try {
            // first 3 parameters are service, action and message id
            if (paramTypes.length - defaultParameterCount != multipart.getCount()) {
                throw new MessagingAPIException(VALIDATION_FAILED, String.format("Payload count [{}] does not match parameter [{}] count!", paramTypes.length - 3, multipart.getCount()), null);
            }
        } catch (MessagingException e) {
            throw new MessagingAPIException(INTERNAL_SERVER_ERROR, "Error occurred while parsing the request multipart-body!", null, e);
        }
        return paramTypes;
    }

    public InputStream validateRequest(HttpServletRequest request) {
        //get digest hash and get
        boolean securityEnabled = dsdMockProperties.isMessagingSecurityEnabled();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ServletInputStream inputStream = request.getInputStream();
            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                baos.write(data, 0, nRead);
            }
            inputStream.close();
            baos.flush();
        } catch (IOException e) {
            LOG.error("Error occurred while reading the request", e);
            throw new MessagingAPIException(INTERNAL_SERVER_ERROR,
                    "Error occurred while reading the message!",
                    request.getServletPath());
        }
        InMemoryDocument doc = new InMemoryDocument(baos.toByteArray());
        // validate digest
        String digest = request.getHeader("Digest");
        if (digest == null) {
            LOG.error("Missing Digest http header for [{}]!", request.getServletPath());
            if (securityEnabled) {
                throw new MessagingAPIException(VALIDATION_FAILED,
                        "Missing Digest http header!",
                        request.getServletPath());
            }
            return doc.openStream();
        }

        if (!digest.contains("=")) {
            LOG.error("Invalid Digest http header format [{}]. Path [{}]!", digest, request.getServletPath());
            if (securityEnabled) {
                throw new MessagingAPIException(VALIDATION_FAILED,
                        "Invalid Digest http header format!",
                        request.getServletPath());
            }
            return doc.openStream();

        }


        int dSplit = digest.indexOf("=");
        String digestAlg = digest.substring(0, dSplit);
        String digestVal = digest.substring(dSplit + 1);

        DigestAlgorithm digestAlgorithm = DigestAlgorithm.forHttpHeader(digestAlg);
        LOG.info("Got DigestAlgorithm: [{}]", digestAlgorithm);

        // check if digest hash matches payload
        String recalcDigest = doc.getDigest(digestAlgorithm);
        if (!StringUtils.equals(digestVal, recalcDigest)) {
            LOG.error("Input request digest [{}] does not match re-calculated digest [{}]", digestVal, recalcDigest);
            if (securityEnabled) {
                throw new MessagingAPIException(INTERNAL_SERVER_ERROR,
                        "Input request digest [" + digestVal + "] is not valid!",
                        request.getServletPath());
            }
        }
        LOG.info("Input request digest [{}] match re-calculated digest [{}]", digestVal, recalcDigest);
        // validate signature!
        String signature = request.getHeader(MessagingParameterType.EDEL_MESSAGE_SIG.getName());
        if (signature == null) {
            LOG.error("Input request [{}] is not signed! Missing header Edel-Message-Sig ", request.getServletPath());
            if (securityEnabled) {
                throw new MessagingAPIException(INTERNAL_SERVER_ERROR,
                        "Input request [{}] is not signed! Missing header Edel-Message-Sig!",
                        request.getServletPath());
            }
            // return
            return doc.openStream();
        }


        List<DSSDocument> headersToSign = new ArrayList<>();
        headersToSign.add(new HTTPHeaderDigest(doc, digestAlgorithm));
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if (StringUtils.equalsIgnoreCase(MessagingParameterType.EDEL_MESSAGE_SIG.getName(), header)) {
                continue;
            }
            // do not add digest - it is added by the HTTPHeaderDigest header
            if (StringUtils.equalsIgnoreCase("Digest", header)) {
                continue;
            }
            headersToSign.add(new HTTPHeader(header, request.getHeader(header)));
        }

        String requestTarget = request.getMethod().toLowerCase() + " " + request.getContextPath() + request.getServletPath();
        headersToSign.add(new HTTPHeader(MessagingConstants.HEADER_REQUEST_TARGET, requestTarget));
        jwsService.validateSignature(signature, headersToSign);

        return doc.openStream();
    }
}
