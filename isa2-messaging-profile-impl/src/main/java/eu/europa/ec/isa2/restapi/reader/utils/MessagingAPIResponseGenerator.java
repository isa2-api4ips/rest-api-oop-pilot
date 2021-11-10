package eu.europa.ec.isa2.restapi.reader.utils;

import com.fasterxml.jackson.core.JsonParser;
import eu.europa.ec.isa2.restapi.profile.annotation.MultipartPayload;
import eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants;
import eu.europa.ec.isa2.restapi.profile.enums.*;
import eu.europa.ec.isa2.restapi.profile.model.*;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static eu.europa.ec.isa2.restapi.profile.enums.APIProblemType.MESSAGE_ACCEPTED;
import static eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType.EDEL_MESSAGE_SIG;
import static eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType.EDEL_PAYLOAD_SIG;


/**
 * Rest API pilot project: Purpose of the class is to build response objects for messaging operations
 *
 * @author Joze Rihtarsic
 */
public class MessagingAPIResponseGenerator {


    MessagingObjectPathUtils pathUtils = new MessagingObjectPathUtils();
    Components components;
    MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation;
    String messagingAPIURL;

    // for future implementation. With the existing tools the responses were not parsed OK if they were
    // defined in #/components/responses
    boolean createReferencesForResponses = false;

    public MessagingAPIResponseGenerator(Components components) {
        this(components, MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS, null);
    }


    public MessagingAPIResponseGenerator(Components components, MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation, String messagingAPIURL) {
        this.components = components;
        this.messagingAPIDefinitionsLocation = messagingAPIDefinitionsLocation;
        this.messagingAPIURL = messagingAPIURL;
    }

    /**
     * Method returns multipart response
     *
     * @param title
     * @param description
     * @param payloads
     * @return
     */
    public ApiResponse createMultipartResponse(String title, String description, MultipartPayload[] payloads) {
        //
        Content multipartContent = createMultipartContent(title, description, payloads);
        ApiResponse p = new ApiResponse().content(multipartContent);
        p.setDescription(description);
        // always add headers for signal - that is Edel-Message-Sig
        // the Original-Sender and Final recipient are added manually for pull and webhook endpoint!
        addCommonHeaders(p, true);
        return p;
    }

    public RequestBody createMultipartRequest(String title, String description, MultipartPayload[] payloads) {
        //
        Content multipartContent = createMultipartContent(title, description, payloads);
        RequestBody p = new RequestBody().content(multipartContent);
        return p;
    }

    public RequestBody createSignalMessageRequest(APIProblemType messageType) {
        //MESSAGE_ACCEPTED
        Content multipartContent = createSignalMessageContent(messageType);
        RequestBody p = new RequestBody().content(multipartContent);
        return p;
    }


    public void addCommonHeaders(ApiResponse response, boolean headersForSignal) {

        List<MessagingParameterType> headerParameters = Arrays.asList(MessagingParameterType.values()).stream()
                .filter(parameterType -> !parameterType.isPayloadPart()
                        && (parameterType.getLocation() == MessagingParameterLocationType.HEADER)
                        && !parameterType.isPayloadPart()
                        && !parameterType.isWebhookParameters()
                        && headersForSignal != parameterType.isUserMessageParameter()
                        && parameterType.getMessagingParameterUsageType() != MessagingParameterUsageType.MESSAGE_REQUEST_ONLY
                ).collect(Collectors.toList());
        for (MessagingParameterType parameterType : headerParameters) {
            response.addHeaderObject(parameterType.getName(), createMessagingHeaderForType(parameterType));
        }
    }

    /**
     * Method generates multipart content. If payload is not given (null or empty array) then multipart content is generic list of bytearrays"
     *
     * @param title       - the title of the content
     * @param description - the description of the content
     * @param payloads    - array of payloads.
     * @return - OpenAPi Content
     */
    public Content createMultipartContent(String title, String description, MultipartPayload[] payloads) {
        MediaType mediaType = new MediaType();
        Schema requestBody;
        Map<String, Header> headerMap = new HashMap<>();
        Header payloadSignature = createMessagingHeaderForType(EDEL_PAYLOAD_SIG);
        headerMap.put(EDEL_PAYLOAD_SIG.getName(), payloadSignature);

        if (payloads == null || payloads.length == 0) {
            String simpleName = PayloadBody.class.getSimpleName();
            if (!components.getSchemas().containsKey(simpleName)) {
                ResolvedSchema payloadPart = ModelConverters.getInstance().readAllAsResolvedSchema(PayloadBody.class);
                components.addSchemas(simpleName, payloadPart.schema);
                payloadPart.referencedSchemas.forEach(components::addSchemas);
            }
            requestBody = new Schema().$ref(MessagingConstants.OPENAPI_REF_PATH_SCHEMAS + simpleName);
            mediaType.addEncoding(simpleName, new Encoding().headers(headerMap));

        } else {
            requestBody = new Schema()
                    .type("object").title(StringUtils.trimToNull(title)).description(StringUtils.trimToNull(description));

            Arrays.asList(payloads).forEach(
                    multipartPayload -> {
                        Schema schema = createRefSchemaForCass(multipartPayload.instance(), components);
                        try (JsonParser parser = Json.mapper().createParser(multipartPayload.example())) {
                            requestBody.addProperties(multipartPayload.name(), schema.example(parser.getCurrentToken()));
                        } catch (IOException e) {
                            throw new IllegalArgumentException("Can not parse multipart example: [" + multipartPayload.example() + "]]!");
                        }
                        mediaType.addEncoding(multipartPayload.name(),
                                new Encoding().contentType(multipartPayload.contentType())
                                        .headers(headerMap));
                    }
            );
        }
        return new Content()
                .addMediaType(org.springframework.http.MediaType.MULTIPART_MIXED_VALUE,
                        mediaType.schema(requestBody));
    }


    /**
     * create messaging header for given type. The header is generated according to messagingAPIDefinitionsLocation!
     *
     * @param parameterType - header type
     * @return
     */
    public Header createMessagingHeaderForType(MessagingParameterType parameterType) {
        Header header;
        String definitionURI = pathUtils.getDefinitionURI(messagingAPIDefinitionsLocation, parameterType.getMessagingReferenceType(),
                MessagingConstants.OPENAPI_SUBPATH_HEADERS, messagingAPIURL);
        switch (messagingAPIDefinitionsLocation) {
            case DOCUMENT_COMPONENTS: {
                if (!components.getHeaders().containsKey(parameterType.getMessagingReferenceType().getName())) {
                    Header parameterSchema = createMessagingHeaderForTypePrivate(parameterType);
                    components.getHeaders().put(parameterType.getMessagingReferenceType().getName(), parameterSchema);
                }
            }
            // this part is common to DOCUMENT_COMPONENTS, MESSAGING_API_OBJECT and MESSAGING_API_COMPONENTS!
            case MESSAGING_API_OBJECT:
            case MESSAGING_API_COMPONENTS: {
                header = new Header()
                        .$ref(definitionURI);
                break;
            }
            case INLINE:
            default:
                header = createMessagingHeaderForTypePrivate(parameterType);
        }
        return header;
    }


    public Header createMessagingHeaderForTypePrivate(MessagingParameterType parameterType) {

        Schema schema;
        if (parameterType.getSchema() != null) {
            if (!components.getSchemas().containsKey(parameterType.getSchema().getName())) {
                components.addSchemas(parameterType.getSchema().getName(), parameterType.getSchema());
            }
            schema = new Schema().$ref(MessagingConstants.OPENAPI_REF_PATH_SCHEMAS + parameterType.getSchema().getName());
        } else {
            schema = new StringSchema().format(parameterType.getFormat());
        }

        Header parameter = new Header()
                .description(parameterType.getDescription())
                .schema(schema)
                .example(parameterType.getExample());

        return parameter;
    }


    public void addAllProblemResponses(ApiResponses responses, boolean includePullResponses) {

        Arrays.asList(APIProblemType.values()).stream()
                .filter(problemType -> problemType.isError()
                        && (includePullResponses == problemType.isPullSpecific())
                )
                .forEach(problemType -> {
                    ApiResponse response = responses.get(problemType.getStatus().toString());
                    if (response == null) {
                        // create new response entry
                        responses.addApiResponse(problemType.getStatus().toString(), createProblemResponse(problemType));
                    } else {
                        // add new example
                        response.getContent().get(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE).addExamples(problemType.getName(), new Example().value(new APIProblem(problemType)));
                    }

                });
    }

    public ApiResponse createProblemResponse(APIProblemType type) {
        if (createReferencesForResponses) {
            if (!components.getResponses().containsKey(type.getName())) {
                ApiResponse response = createProblemResponsePrivate(type);
                components.getResponses().put(type.getName(), response);
            }
            return new ApiResponse().$ref(MessagingConstants.OPENAPI_REF_PATH_REQUESTS + type.getName());
        }
        return createProblemResponsePrivate(type);
    }

    public ApiResponse createProblemResponsePrivate(APIProblemType type) {
        MediaType mediaType;

        Schema problemSchema = createMessagingApiSchemaDefinition(APIProblem.class, MessagingReferenceType.PROBLEM);
        mediaType = new MediaType().schema(problemSchema)
                .addExamples(type.getName(), new Example().value(new APIProblem(type)));


        Content apiTypeContent = new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE, mediaType);
        ApiResponse response = new ApiResponse().content(apiTypeContent);
        response.setDescription(type.getDetail());
        // add only signature header
        response.addHeaderObject(EDEL_MESSAGE_SIG.getName(), createMessagingHeaderForType(EDEL_MESSAGE_SIG));
        return response;
    }

    public ApiResponse createSignalMessageResponse() {

        if (createReferencesForResponses) {
            if (!components.getResponses().containsKey(MessagingConstants.OPENAPI_SCHEMA_NAME_SIGNAL_MESSAGE)) {
                ApiResponse response = createSignalResponsePrivate(MESSAGE_ACCEPTED);
                components.getResponses().put(MessagingConstants.OPENAPI_SCHEMA_NAME_SIGNAL_MESSAGE, response);
            }
            return new ApiResponse().$ref(MessagingConstants.OPENAPI_REF_PATH_REQUESTS + MessagingConstants.OPENAPI_SCHEMA_NAME_SIGNAL_MESSAGE);
        }
        return createSignalResponsePrivate(MESSAGE_ACCEPTED);
    }

    public ApiResponse createMessageReferenceListResponse() {

        if (createReferencesForResponses) {
            if (!components.getResponses().containsKey(MessagingConstants.OPENAPI_SCHEMA_NAME_REFERENCE_LIST_MESSAGE)) {
                ApiResponse response = createMessageResponseListPrivate();
                components.getResponses().put(MessagingConstants.OPENAPI_SCHEMA_NAME_REFERENCE_LIST_MESSAGE, response);
            }
            return new ApiResponse().$ref(MessagingConstants.OPENAPI_REF_PATH_REQUESTS + MessagingConstants.OPENAPI_SCHEMA_NAME_REFERENCE_LIST_MESSAGE);
        }
        return createMessageResponseListPrivate();
    }

    public ApiResponse createSignalResponsePrivate(APIProblemType signalType) {
        Content apiTypeContent = createSignalMessageContent(signalType == null ? MESSAGE_ACCEPTED : signalType);
        ApiResponse response = new ApiResponse().content(apiTypeContent);
        response.setDescription("Sent when the message is properly validated and received.");
        addCommonHeaders(response, true);
        return response;
    }

    public ApiResponse createMessageResponseListPrivate() {

        MessageReferenceListRO referenceListRO = new MessageReferenceListRO();
        referenceListRO.getMessageReferenceList().add(new MessageReferenceRO(UUID.randomUUID().toString(), "my-service", "my-action"));
        referenceListRO.getMessageReferenceList().add(new MessageReferenceRO(UUID.randomUUID().toString(), "my-service", "my-action"));

        Schema schema = createMessagingApiSchemaDefinition(MessageReferenceListRO.class, MessagingReferenceType.MESSAGE_REFERENCE_LIST);
        MediaType mediaType = new MediaType().schema(schema)
                .example(referenceListRO);
        Content apiTypeContent = new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType);
        ;

        ApiResponse response = new ApiResponse().content(apiTypeContent);
        response.setDescription("Message reference list");
        addCommonHeaders(response, true);
        return response;
    }


    public Content createSignalMessageContent(APIProblemType problemType) {
        Schema problemSchema = createMessagingApiSchemaDefinition(SignalMessage.class, MessagingReferenceType.SIGNAL_MESSAGE);
        MediaType mediaType = new MediaType().schema(problemSchema)
                .example(new SignalMessage(
                        problemType.getTitle(),
                        problemType.getStatus(),
                        problemType.getType(),
                        problemType.getDetail(),
                        "/my-service/my-action/dde12f67-c391-4851-8fa2-c07dd8532efd",
                        "sha-256=eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"

                ));
        return new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE, mediaType);
    }


    private Schema createMessagingApiSchemaDefinition(Class messagingApiClass, MessagingReferenceType referenceType) {
        Schema messagingApiSchema;
        String definitionURI = pathUtils.getDefinitionURI(messagingAPIDefinitionsLocation, referenceType,
                MessagingConstants.OPENAPI_SUBPATH_SCHEMAS, messagingAPIURL);
        switch (messagingAPIDefinitionsLocation) {
            case DOCUMENT_COMPONENTS: {
                if (!components.getSchemas().containsKey(referenceType.getName())) {
                    ResolvedSchema resolvedSchema = ModelConverters.getInstance().readAllAsResolvedSchema(messagingApiClass);
                    components.addSchemas(referenceType.getName(), resolvedSchema.schema);
                    // add referenced schemas
                    resolvedSchema.referencedSchemas.forEach((name, schema) -> {
                        if (!components.getSchemas().containsKey(name)) {
                            components.addSchemas(name, schema);
                        }
                    });
                }
            }
            case MESSAGING_API_OBJECT:
            case MESSAGING_API_COMPONENTS: {
                messagingApiSchema = new Schema().name(referenceType.getName())
                        .$ref(definitionURI);
                break;
            }
            case INLINE:
            default:
                ResolvedSchema resolvedSchema = ModelConverters.getInstance().readAllAsResolvedSchema(messagingApiClass);
                messagingApiSchema = resolvedSchema.schema;
                // title is used to generate class by the swagger-codegen
                messagingApiSchema.setTitle(referenceType.getName());
                // add referenced schemas
                resolvedSchema.referencedSchemas.forEach((name, schema) -> {
                    if (!components.getSchemas().containsKey(name)) {
                        components.addSchemas(name, schema);
                    }
                });

        }
        return messagingApiSchema;
    }

    public ApiResponse createObjectResponse(Object object) {
        String objectName = object.getClass().getSimpleName();
        if (createReferencesForResponses) {
            if (!components.getResponses().containsKey(objectName)) {
                ApiResponse response = createObjectResponsePrivate(object);
                components.getResponses().put(objectName, response);
            }
            return new ApiResponse().$ref(MessagingConstants.OPENAPI_REF_PATH_REQUESTS + objectName);
        }
        return createObjectResponsePrivate(object);
    }


    public ApiResponse createObjectResponsePrivate(Object object) {
        Content apiTypeContent = createObjectContent(object, components);
        ApiResponse response = new ApiResponse().content(apiTypeContent);
        response.setDescription("Sent when the message is properly validated and received.");
        // add only signature header
        response.addHeaderObject(EDEL_MESSAGE_SIG.getName(), createMessagingHeaderForType(EDEL_MESSAGE_SIG));
        return response;
    }

    public Content createObjectContent(Object object, Components components) {
        Schema schema = createRefSchemaForCass(object.getClass(), components);
        MediaType mediaType = new MediaType().schema(schema)
                .example(object);
        return new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType);
    }

    public Schema createRefSchemaForCass(Class clazz, Components components) {
        ResolvedSchema resolvedSchema = ModelConverters.getInstance().readAllAsResolvedSchema(clazz);
        String objectName = StringUtils.isBlank(resolvedSchema.schema.getName()) ? clazz.getSimpleName() :
                resolvedSchema.schema.getName();
        if (!components.getSchemas().containsKey(objectName)) {
            components.addSchemas(objectName, resolvedSchema.schema);
            resolvedSchema.referencedSchemas.forEach(components::addSchemas);
        }
        return new Schema().$ref(MessagingConstants.OPENAPI_REF_PATH_SCHEMAS + objectName);
    }
}
