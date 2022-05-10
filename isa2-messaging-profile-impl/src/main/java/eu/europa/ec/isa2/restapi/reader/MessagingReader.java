package eu.europa.ec.isa2.restapi.reader;

import eu.europa.ec.isa2.restapi.profile.annotation.*;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import eu.europa.ec.isa2.restapi.reader.utils.MessagingAPIParameterGenerator;
import eu.europa.ec.isa2.restapi.reader.utils.MessagingAPIResponseGenerator;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.jaxrs2.SecurityParser;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;
import io.swagger.v3.jaxrs2.util.ReaderUtils;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants.GET_METHOD;
import static eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants.POST_METHOD;
import static eu.europa.ec.isa2.restapi.profile.enums.APIProblemType.MESSAGE_ACCEPTED;
import static eu.europa.ec.isa2.restapi.profile.enums.APIProblemType.PULL_MESSAGE_READY;
import static eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType.*;

public class MessagingReader {
    private static final String PROPERTIES = "properties:";

    private static final Logger LOG = LoggerFactory.getLogger(MessagingReader.class);
    OpenAPI openAPI;
    Components components;
    Paths paths;
    String version;

    MessagingAPIParameterGenerator messagingAPIParameterGenerator;
    MessagingAPIResponseGenerator messagingAPIResponseGenerator;

    Properties authorizationProperties = new Properties();

    public MessagingReader() {
        this(MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS, null, "v1", new Properties());
    }

    public MessagingReader(MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation, String parameterUrl, String version, Properties properties) {
        this.openAPI = new OpenAPI();
        this.paths = new Paths();
        this.components = new Components();
        this.components.setSchemas(new LinkedHashMap<>());
        this.components.setResponses(new LinkedHashMap<>());
        this.components.setParameters(new LinkedHashMap<>());
        this.components.setHeaders(new LinkedHashMap<>());
        this.openAPI.setComponents(components);
        this.messagingAPIParameterGenerator = new MessagingAPIParameterGenerator(components, messagingAPIDefinitionsLocation, parameterUrl);
        this.messagingAPIResponseGenerator = new MessagingAPIResponseGenerator(components, messagingAPIDefinitionsLocation, parameterUrl);
        this.version = version;
        if (properties != null) {
            this.authorizationProperties = properties;
        }

    }

    public OpenAPI read(Class<?> cls) {
        final List<Method> methods = Arrays.stream(cls.getMethods())
                .sorted(new MessagingReader.MethodComparator())
                .collect(Collectors.toList());

        List<io.swagger.v3.oas.annotations.security.SecurityScheme> apiSecuritySchemeAnnotations = ReflectionUtils.getRepeatableAnnotations(cls, io.swagger.v3.oas.annotations.security.SecurityScheme.class);
        List<io.swagger.v3.oas.annotations.security.SecurityRequirement> apiSecurityRequirementsAnnotations = ReflectionUtils.getRepeatableAnnotations(cls, io.swagger.v3.oas.annotations.security.SecurityRequirement.class);


        // class security schemes
        if (apiSecuritySchemeAnnotations != null) {
            for (io.swagger.v3.oas.annotations.security.SecurityScheme securitySchemeAnnotation : apiSecuritySchemeAnnotations) {
                Optional<SecurityParser.SecuritySchemePair> securitySchemePairOptional = SecurityParser.getSecurityScheme(securitySchemeAnnotation);
                if (securitySchemePairOptional.isPresent()) {
                    SecurityScheme securityScheme = securitySchemePairOptional.get().securityScheme;
                    String schemeKey = securitySchemePairOptional.get().key;
                    Map<String, SecurityScheme> securitySchemeMap = new HashMap<>();
                    updateOAuthFlowsProperties(securityScheme.getFlows());
                    if (StringUtils.isNotBlank(schemeKey)) {
                        securitySchemeMap.put(schemeKey, securityScheme);
                        if (components.getSecuritySchemes() != null && components.getSecuritySchemes().size() != 0) {
                            components.getSecuritySchemes().putAll(securitySchemeMap);

                        } else {
                            components.setSecuritySchemes(securitySchemeMap);
                        }
                    }
                }
            }
        }

        // class security requirements
        List<SecurityRequirement> apiSecurityRequirements = new ArrayList<>();
        if (apiSecurityRequirementsAnnotations != null) {
            Optional<List<SecurityRequirement>> apiRequirementsOpt = SecurityParser.getSecurityRequirements(
                    apiSecurityRequirementsAnnotations.toArray(new io.swagger.v3.oas.annotations.security.SecurityRequirement[apiSecurityRequirementsAnnotations.size()])
            );
            if (apiRequirementsOpt.isPresent()) {
                apiSecurityRequirements = apiRequirementsOpt.get();
            }
        }


        // iterate class methods
        for (Method method : methods) {

            SubmitResponseMessageOperation submitResponseMessageOperation = ReflectionUtils.getAnnotation(method, SubmitResponseMessageOperation.class);
            if (submitResponseMessageOperation != null) {
                LOG.info("Read method ResponseMessageSubmissionOperation [{}]", method);
                readResponseMessageSubmissionOperation(submitResponseMessageOperation, method, apiSecurityRequirements);
                continue;
            }

            SubmitMessageOperation submitMessageOperation = ReflectionUtils.getAnnotation(method, SubmitMessageOperation.class);
            if (submitMessageOperation != null) {
                LOG.info("Read method MessageSubmissionOperation [{}]", method);
                readMessageSubmissionOperation(submitMessageOperation, method, apiSecurityRequirements);
                continue;
            }

            SubmitSignalOperation submitSignalOperation = ReflectionUtils.getAnnotation(method, SubmitSignalOperation.class);
            if (submitSignalOperation != null) {
                LOG.info("Read method webhookSignalSubmissionOperation [{}]", method);
                readResponseMessageSubmissionOperation(submitSignalOperation, method, apiSecurityRequirements);
                continue;
            }

            GetMessageReferenceListOperation getMessageReferenceListOperation = ReflectionUtils.getAnnotation(method, GetMessageReferenceListOperation.class);
            if (getMessageReferenceListOperation != null) {
                LOG.info("Read method messageReferenceList [{}]", method);
                readMessageReferenceListOperation(getMessageReferenceListOperation, method, apiSecurityRequirements);
                continue;
            }

            GetMessageOperation getMessageOperation = ReflectionUtils.getAnnotation(method, GetMessageOperation.class);
            if (getMessageOperation != null) {
                LOG.info("Read method getMessageOperation [{}]", method);
                readGetMessageOperation(getMessageOperation, method, apiSecurityRequirements);
                continue;
            }

            GetResponseMessageReferenceListOperation getResponseMessageReferenceListOperation = ReflectionUtils.getAnnotation(method, GetResponseMessageReferenceListOperation.class);
            if (getResponseMessageReferenceListOperation != null) {
                LOG.info("Read method ResponseMessagePullOperation [{}]", method);
                readResponseMessagePullOperation(getResponseMessageReferenceListOperation, method, apiSecurityRequirements);
                continue;
            }
            GetResponseMessageOperation getResponseMessageOperation = ReflectionUtils.getAnnotation(method, GetResponseMessageOperation.class);
            if (getResponseMessageOperation != null) {
                LOG.info("Read method GetResponseMessageOperation [{}]", method);
                readGetResponseMessageOperation(getResponseMessageOperation, method, apiSecurityRequirements);
                continue;
            }
        }

        if (openAPI.getComponents().getResponses().isEmpty()) {
            openAPI.getComponents().setResponses(null);
        }

        if (openAPI.getComponents().getParameters().isEmpty()) {
            openAPI.getComponents().setParameters(null);
        }
        if (openAPI.getComponents().getHeaders().isEmpty()) {
            openAPI.getComponents().setHeaders(null);
        }

        return openAPI;
    }


    public void readGetResponseMessageOperation(GetResponseMessageOperation messageOperation, Method method, List<SecurityRequirement> classSecurityRequirements) {

        String operationPath = "/" + version + "/messaging/"
                + (StringUtils.isBlank(messageOperation.service()) ? "{" + MessagingParameterType.SERVICE.getName() + "}" : messageOperation.service())
                + "/" + (StringUtils.isBlank(messageOperation.action()) ? "{" + MessagingParameterType.ACTION.getName() + "}" : messageOperation.action())
                + "/{" + messageOperation.messageIdParamName() + "}"
                + "/response"
                + "/" + (StringUtils.isBlank(messageOperation.responseService()) ? "{" + MessagingParameterType.RESPONSE_SERVICE.getName() + "}" : messageOperation.responseService())
                + "/" + (StringUtils.isBlank(messageOperation.responseAction()) ? "{" + MessagingParameterType.RESPONSE_ACTION.getName() + "}" : messageOperation.responseAction())
                + "/{" + messageOperation.responseMessageIdParamName() + "}";

        String httpMethod = ReaderUtils.extractOperationMethod(method, OpenAPIExtensions.chain());

        PathItem pathItemObject;
        if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
            pathItemObject = openAPI.getPaths().get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }

        if (StringUtils.isBlank(httpMethod)) {
            return;
        }
        ApiResponses responses = new ApiResponses();

        ApiResponse defaultResponse = messagingAPIResponseGenerator.createMultipartResponse(messageOperation.responseTitle(), messageOperation.responseDescription(), messageOperation.responsePayloads());
        defaultResponse.addHeaderObject(ORIGINAL_SENDER.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(ORIGINAL_SENDER));
        defaultResponse.addHeaderObject(ORIGINAL_SENDER_TOKEN.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(ORIGINAL_SENDER_TOKEN));
        defaultResponse.addHeaderObject(FINAL_RECIPIENT.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(FINAL_RECIPIENT));
        defaultResponse.addHeaderObject(TIMESTAMP.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(TIMESTAMP));
        defaultResponse.addHeaderObject(MESSAGE_ID_HEADER.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(MESSAGE_ID_HEADER));
        defaultResponse.addHeaderObject(EDEL_MESSAGE_SIG.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(EDEL_MESSAGE_SIG));

        responses.addApiResponse(MESSAGE_ACCEPTED.getStatus().toString(),defaultResponse);

        // do not include pull specific errors
        messagingAPIResponseGenerator.addAllProblemResponses(responses, true);

        List<Parameter> methodParameters = messagingAPIParameterGenerator.createOperationParameters(messageOperation);
        Operation operation = new Operation()
                .operationId(messageOperation.operationId())
                .tags(messageOperation.tags().length != 0 ? Arrays.asList(messageOperation.tags()) : null)
                .description(messageOperation.description())
                .summary(messageOperation.summary())
                .parameters(methodParameters)
                .responses(responses);

        // parse security operations
        readSecurityRequirements(messageOperation.securityRequirements(), operation, classSecurityRequirements);

        setPathItemOperation(pathItemObject, httpMethod, operation);

        addPathItem(operationPath, pathItemObject, false);
    }

    public void readResponseMessagePullOperation(GetResponseMessageReferenceListOperation annotatedOperation, Method method, List<SecurityRequirement> classSecurityRequirements) {

        String operationPath = "";
        switch (annotatedOperation.operationType()) {
            case RESPONSE_ACTION:
                operationPath = "/" + (StringUtils.isBlank(annotatedOperation.responseAction()) ? "{" + MessagingParameterType.RESPONSE_ACTION.getName() + "}"
                        : annotatedOperation.responseAction()) + operationPath;
            case RESPONSE_SERVICE:
                operationPath = "/" + (StringUtils.isBlank(annotatedOperation.responseService()) ? "{" + MessagingParameterType.RESPONSE_SERVICE.getName() + "}"
                        : annotatedOperation.responseService()) + operationPath;
            case RESPONSE_ALL: {
                operationPath = "/" + version + "/messaging"
                        + "/" + (StringUtils.isBlank(annotatedOperation.service()) ? "{" + MessagingParameterType.SERVICE.getName() + "}" : annotatedOperation.service())
                        + "/" + (StringUtils.isBlank(annotatedOperation.action()) ? "{" + MessagingParameterType.ACTION.getName() + "}" : annotatedOperation.action())
                        + "/{" + annotatedOperation.messageIdParamName() + "}"
                        + "/response" + operationPath;
            }
        }

        String httpMethod = ReaderUtils.extractOperationMethod(method, OpenAPIExtensions.chain());

        PathItem pathItemObject;
        if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
            pathItemObject = openAPI.getPaths().get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }

        if (StringUtils.isBlank(httpMethod)) {
            return;
        }

        List<Parameter> methodParameters = messagingAPIParameterGenerator.createOperationParameters(annotatedOperation);

        ApiResponses responses = new ApiResponses();
        responses.addApiResponse(MESSAGE_ACCEPTED.getStatus().toString(),
                messagingAPIResponseGenerator.createMessageReferenceListResponse());

        // do not include pull specific errors
        messagingAPIResponseGenerator.addAllProblemResponses(responses, true);

        Operation operation = new Operation()
                .operationId(annotatedOperation.operationId())
                .tags(annotatedOperation.tags().length != 0 ? Arrays.asList(annotatedOperation.tags()) : null)
                .description(annotatedOperation.description())
                .summary(annotatedOperation.summary())
                .parameters(methodParameters)
                .responses(responses);
        // parse security operations
        readSecurityRequirements(annotatedOperation.securityRequirements(), operation, classSecurityRequirements);

        setPathItemOperation(pathItemObject, httpMethod, operation);

        addPathItem(operationPath, pathItemObject, false);
    }


    public void readGetMessageOperation(GetMessageOperation messageOperation, Method method, List<SecurityRequirement> classSecurityRequirements) {

        String operationPath = "/" + version + "/messaging/"
                + (StringUtils.isBlank(messageOperation.service()) ? "{" + MessagingParameterType.SERVICE.getName() + "}" : messageOperation.service())
                + "/" + (StringUtils.isBlank(messageOperation.action()) ? "{" + MessagingParameterType.ACTION.getName() + "}" : messageOperation.action())
                + "/{" + messageOperation.messageIdParamName() + "}";

        String httpMethod = ReaderUtils.extractOperationMethod(method, OpenAPIExtensions.chain());

        PathItem pathItemObject;
        if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
            pathItemObject = openAPI.getPaths().get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }

        if (StringUtils.isBlank(httpMethod)) {
            return;
        }
        ApiResponses responses = new ApiResponses();

        ApiResponse defaultResponse = messagingAPIResponseGenerator.createMultipartResponse(
                messageOperation.responseTitle(),
                messageOperation.responseDescription(),
                messageOperation.responsePayloads());

        defaultResponse.addHeaderObject(ORIGINAL_SENDER.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(ORIGINAL_SENDER));
        defaultResponse.addHeaderObject(ORIGINAL_SENDER_TOKEN.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(ORIGINAL_SENDER_TOKEN));
        defaultResponse.addHeaderObject(FINAL_RECIPIENT.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(FINAL_RECIPIENT));
        defaultResponse.addHeaderObject(TIMESTAMP.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(TIMESTAMP));
        defaultResponse.addHeaderObject(MESSAGE_ID_HEADER.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(MESSAGE_ID_HEADER));
        defaultResponse.addHeaderObject(EDEL_MESSAGE_SIG.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(EDEL_MESSAGE_SIG));

        responses.addApiResponse(MESSAGE_ACCEPTED.getStatus().toString(),
                defaultResponse);


        // do not include pull specific errors
        messagingAPIResponseGenerator.addAllProblemResponses(responses, true);

        List<Parameter> methodParameters = messagingAPIParameterGenerator.createOperationParameters(messageOperation);


        Operation operation = new Operation()
                .operationId(messageOperation.operationId())
                .tags(messageOperation.tags().length != 0 ? Arrays.asList(messageOperation.tags()) : null)
                .description(messageOperation.description())
                .summary(messageOperation.summary())
                .parameters(methodParameters)
                .responses(responses);
        // parse security operations
        readSecurityRequirements(messageOperation.securityRequirements(), operation, classSecurityRequirements);

        setPathItemOperation(pathItemObject, httpMethod, operation);

        addPathItem(operationPath, pathItemObject, false);
    }

    public void readMessageReferenceListOperation(GetMessageReferenceListOperation annotatedOperation, Method method, List<SecurityRequirement> classSecurityRequirements) {


        String operationPath = "";
        switch (annotatedOperation.operationType()) {
            case SERVICE_AND_ACTION:
                operationPath = "/" + (StringUtils.isBlank(annotatedOperation.action()) ? "{" + MessagingParameterType.ACTION.getName() + "}" : annotatedOperation.action()) + operationPath;
            case SERVICE:
                operationPath = "/" + (StringUtils.isBlank(annotatedOperation.service()) ? "{" + MessagingParameterType.SERVICE.getName() + "}" : annotatedOperation.service()) + operationPath;
            case ALL:
                operationPath = "/" + version + "/messaging" + operationPath;
        }


        List<Parameter> methodParameters = messagingAPIParameterGenerator.createOperationParameters(annotatedOperation);
        String httpMethod = ReaderUtils.extractOperationMethod(method, OpenAPIExtensions.chain());

        PathItem pathItemObject;
        if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
            pathItemObject = openAPI.getPaths().get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }

        if (StringUtils.isBlank(httpMethod)) {
            return;
        }

        ApiResponses responses = new ApiResponses();
        responses.addApiResponse(MESSAGE_ACCEPTED.getStatus().toString(),
                messagingAPIResponseGenerator.createMessageReferenceListResponse());
        // do not include pull specific errors
        messagingAPIResponseGenerator.addAllProblemResponses(responses, true);


        Operation operation = new Operation()
                .operationId(annotatedOperation.operationId())
                .tags(annotatedOperation.tags().length != 0 ? Arrays.asList(annotatedOperation.tags()) : null)
                .description(annotatedOperation.description())
                .summary(annotatedOperation.summary())
                .parameters(methodParameters.isEmpty() ? null : methodParameters)
                .responses(responses);
        // parse security operations
        readSecurityRequirements(annotatedOperation.securityRequirements(), operation, classSecurityRequirements);

        setPathItemOperation(pathItemObject, httpMethod, operation);

        addPathItem(operationPath, pathItemObject, false);
    }


    public void readResponseMessageSubmissionOperation(SubmitSignalOperation annotatedOperation, Method method, List<SecurityRequirement> classSecurityRequirements) {

        String operationPath = "/" + (annotatedOperation.isWebhook() ? "messaging-webhook" : version + "/messaging")
                + "/{" + annotatedOperation.messageIdParamName() + "}"
                + "/response/signal";


        String httpMethod = ReaderUtils.extractOperationMethod(method, OpenAPIExtensions.chain());

        PathItem pathItemObject;
        if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
            pathItemObject = openAPI.getPaths().get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }

        if (StringUtils.isBlank(httpMethod)) {
            return;
        }

        RequestBody requestBody = messagingAPIResponseGenerator.createSignalMessageRequest(PULL_MESSAGE_READY);
        ApiResponses responses = new ApiResponses();
        responses.addApiResponse(MESSAGE_ACCEPTED.getStatus().toString(),
                messagingAPIResponseGenerator.createSignalMessageResponse());

        // do not include pull specific errors
        messagingAPIResponseGenerator.addAllProblemResponses(responses, true);

        List<Parameter> methodParameters = messagingAPIParameterGenerator.createOperationParameters(annotatedOperation);


        Operation operation = new Operation()
                .operationId(annotatedOperation.operationId())
                .tags(annotatedOperation.tags().length != 0 ? Arrays.asList(annotatedOperation.tags()) : null)
                .description(annotatedOperation.description())
                .summary(annotatedOperation.summary())
                .parameters(methodParameters)
                .requestBody(requestBody)
                .responses(responses);
        // parse security operations
        readSecurityRequirements(annotatedOperation.securityRequirements(), operation, classSecurityRequirements);

        setPathItemOperation(pathItemObject, httpMethod, operation);

        addPathItem(operationPath, pathItemObject, annotatedOperation.isWebhook());
    }


    public void readResponseMessageSubmissionOperation(SubmitResponseMessageOperation annotatedOperation, Method method, List<SecurityRequirement> classSecurityRequirements) {

        String operationPath = "/" + (annotatedOperation.isWebhook() ? "messaging-webhook" : version + "/messaging")
                + (annotatedOperation.isWebhook() ? "" : "/" + (StringUtils.isBlank(annotatedOperation.service()) ? "{" + MessagingParameterType.SERVICE.getName() + "}" : annotatedOperation.service()))
                + (annotatedOperation.isWebhook() ? "" : "/" + (StringUtils.isBlank(annotatedOperation.action()) ? "{" + MessagingParameterType.ACTION.getName() + "}" : annotatedOperation.action()))
                + "/{" + annotatedOperation.messageIdParamName() + "}"
                + "/response/"
                + (StringUtils.isBlank(annotatedOperation.responseService()) ? "{" + MessagingParameterType.RESPONSE_SERVICE.getName() + "}" : annotatedOperation.responseService())
                + "/" + (StringUtils.isBlank(annotatedOperation.responseAction()) ? "{" + MessagingParameterType.RESPONSE_ACTION.getName() + "}" : annotatedOperation.responseAction())
                + "/{" + annotatedOperation.responseMessageIdParamName() + "}";

        String httpMethod = ReaderUtils.extractOperationMethod(method, OpenAPIExtensions.chain());

        PathItem pathItemObject;
        if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
            pathItemObject = openAPI.getPaths().get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }

        if (StringUtils.isBlank(httpMethod)) {
            return;
        }


        RequestBody requestBody = messagingAPIResponseGenerator.createMultipartRequest(
                annotatedOperation.requestTitle(),
                annotatedOperation.requestDescription(),
                annotatedOperation.requestPayloads());
        ApiResponses responses = new ApiResponses();
        responses.addApiResponse(MESSAGE_ACCEPTED.getStatus().toString(),
                messagingAPIResponseGenerator.createSignalMessageResponse());


        // do not include pull specific errors
        messagingAPIResponseGenerator.addAllProblemResponses(responses, false);

        List<Parameter> methodParameters = messagingAPIParameterGenerator.createOperationParameters(annotatedOperation);
        Operation operation = new Operation()
                .operationId(annotatedOperation.operationId())
                .tags(annotatedOperation.tags().length != 0 ? Arrays.asList(annotatedOperation.tags()) : null)
                .description(annotatedOperation.description())
                .summary(annotatedOperation.summary())
                .parameters(methodParameters)
                .requestBody(requestBody)
                .responses(responses);
        // parse security operations
        readSecurityRequirements(annotatedOperation.securityRequirements(), operation, classSecurityRequirements);

        setPathItemOperation(pathItemObject, httpMethod, operation);

        addPathItem(operationPath, pathItemObject, annotatedOperation.isWebhook());
    }

    public void readMessageSubmissionOperation(SubmitMessageOperation annotatedOperation, Method method, List<SecurityRequirement> classSecurityRequirements) {

        String operationPath = "/" + version + "/messaging/"
                + (StringUtils.isBlank(annotatedOperation.service()) ? "{" + MessagingParameterType.SERVICE.getName() + "}" : annotatedOperation.service())
                + "/" + (StringUtils.isBlank(annotatedOperation.action()) ? "{" + MessagingParameterType.ACTION.getName() + "}" : annotatedOperation.action())
                + "/{" + annotatedOperation.messageIdParamName() + "}"
                + (annotatedOperation.sync() ? "/sync" : "");

        String httpMethod = ReaderUtils.extractOperationMethod(method, OpenAPIExtensions.chain());

        PathItem pathItemObject;
        if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
            pathItemObject = openAPI.getPaths().get(operationPath);
        } else {
            pathItemObject = new PathItem();
        }

        if (StringUtils.isBlank(httpMethod)) {
            return;
        }
        RequestBody requestBody = messagingAPIResponseGenerator.createMultipartRequest(annotatedOperation.requestTitle(), annotatedOperation.requestDescription(),
                annotatedOperation.requestPayloads());
        ApiResponses responses = new ApiResponses();
        if (annotatedOperation.sync()) {
            ApiResponse multipartResponse =  messagingAPIResponseGenerator.createMultipartResponse(
                    annotatedOperation.responseTitle(),
                    annotatedOperation.responseDescription(),
                    annotatedOperation.responsePayloads());

            multipartResponse.addHeaderObject(TIMESTAMP.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(TIMESTAMP));
            multipartResponse.addHeaderObject(MESSAGE_ID_HEADER.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(MESSAGE_ID_HEADER));
            multipartResponse.addHeaderObject(EDEL_MESSAGE_SIG.getName(), messagingAPIResponseGenerator.createMessagingHeaderForType(EDEL_MESSAGE_SIG));

           responses.addApiResponse(MESSAGE_ACCEPTED.getStatus().toString(),multipartResponse );

        } else {
            responses.addApiResponse(MESSAGE_ACCEPTED.getStatus().toString(),
                    messagingAPIResponseGenerator.createSignalMessageResponse());
        }

        // do not include pull specific errors
        messagingAPIResponseGenerator.addAllProblemResponses(responses, false);

        List<Parameter> methodParameters = messagingAPIParameterGenerator.createOperationParameters(annotatedOperation);


        Operation operation = new Operation()
                .operationId(annotatedOperation.operationId())
                .tags(annotatedOperation.tags().length != 0 ? Arrays.asList(annotatedOperation.tags()) : null)
                .description(annotatedOperation.description())
                .summary(annotatedOperation.summary())
                .parameters(methodParameters)
                .requestBody(requestBody)
                .responses(responses);
        // parse security operations
        readSecurityRequirements(annotatedOperation.securityRequirements(), operation, classSecurityRequirements);
        setPathItemOperation(pathItemObject, httpMethod, operation);

        addPathItem(operationPath, pathItemObject, false);
    }


    private void setPathItemOperation(PathItem pathItemObject, String method, Operation operation) {
        switch (method) {
            case POST_METHOD:
                pathItemObject.post(operation);
                break;
            case GET_METHOD:
                pathItemObject.get(operation);
                break;
            default:
                // Do nothing here
                break;
        }
    }

    /**
     * Comparator for uniquely sorting a collection of Method objects.
     * Supports overloaded methods (with the same name).
     *
     * @see Method
     */
    private static class MethodComparator implements Comparator<Method> {

        @Override
        public int compare(Method m1, Method m2) {
            // First compare the names of the method
            int val = m1.getName().compareTo(m2.getName());

            // If the names are equal, compare each argument type
            if (val == 0) {
                val = m1.getParameterTypes().length - m2.getParameterTypes().length;
                if (val == 0) {
                    Class<?>[] types1 = m1.getParameterTypes();
                    Class<?>[] types2 = m2.getParameterTypes();
                    for (int i = 0; i < types1.length; i++) {
                        val = types1[i].getName().compareTo(types2[i].getName());

                        if (val != 0) {
                            break;
                        }
                    }
                }
            }
            return val;
        }
    }

    private void readSecurityRequirements(io.swagger.v3.oas.annotations.security.SecurityRequirement[] securityRequirements, Operation operation, List<SecurityRequirement> classSecurityRequirements) {
        classSecurityRequirements.forEach(operation::addSecurityItem);

        if (securityRequirements != null && securityRequirements.length > 0) {
            Optional<List<SecurityRequirement>> optRequirements = SecurityParser.getSecurityRequirements(securityRequirements);
            if (!optRequirements.isPresent()) {
                return;
            }
            List<SecurityRequirement> requirements = optRequirements.get();
            requirements.stream()
                    .filter(r -> operation.getSecurity() == null || !operation.getSecurity().contains(r))
                    .forEach(operation::addSecurityItem);
        }
    }

    private void updateOAuthFlowsProperties(OAuthFlows flows) {
        if (flows == null) {
            return;
        }
        updateOAuthFlowProperties(flows.getAuthorizationCode());
        updateOAuthFlowProperties(flows.getClientCredentials());
        updateOAuthFlowProperties(flows.getImplicit());
        updateOAuthFlowProperties(flows.getPassword());
    }

    private void updateOAuthFlowProperties(OAuthFlow oAuthFlow) {
        if (oAuthFlow == null) {
            return;
        }
        String tokenUrl = resolvePropertyValue(oAuthFlow.getTokenUrl());
        oAuthFlow.setTokenUrl(tokenUrl);
        String authUrl = resolvePropertyValue(oAuthFlow.getAuthorizationUrl());
        oAuthFlow.setAuthorizationUrl(authUrl);
        String refreshUrl = resolvePropertyValue(oAuthFlow.getRefreshUrl());
        oAuthFlow.setRefreshUrl(refreshUrl);
    }

    private String resolvePropertyValue(String value) {
        if (!StringUtils.isBlank(value)
                && StringUtils.startsWithIgnoreCase(value, PROPERTIES)) {
            String propertyName = value.substring(PROPERTIES.length());

            if (authorizationProperties.containsKey(propertyName)) {
                return authorizationProperties.getProperty(propertyName);
            } else {
                LOG.error("Configuration error! Property [{}] is not set to MessagingReader authorization properties!", propertyName);
            }
        }
        return value;
    }

    private void addPathItem(String operationPath, PathItem pathItemObject, boolean isWebhook) {
        if (isWebhook) {
            openAPI.addWebhooks(operationPath, pathItemObject);
        } else {
            paths.addPathItem(operationPath, pathItemObject);
            if (openAPI.getPaths() != null && openAPI.getPaths() != this.paths) {
                this.paths.putAll(openAPI.getPaths());
            }
            openAPI.setPaths(this.paths);
        }
    }
}
