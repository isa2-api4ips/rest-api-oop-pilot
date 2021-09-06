package eu.europa.ec.isa2.restapi.reader.utils;

import eu.europa.ec.isa2.restapi.profile.annotation.*;
import eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterLocationType;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants.OPENAPI_REF_PATH_PARAMETERS;
import static eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants.OPENAPI_REF_PATH_SCHEMAS;

/**
 * Rest API pilot project: Purpose of the class is to build parameters for operation types:
 *  - MessageSubmissionOperation
 *  - ResponseMessageReferencePullOperation
 *  - ResponseMessageSubmissionOperation
 *  - SignalSubmissionOperation
 *  - GetMessageOperation
 *  - GetResponseMessageOperation
 *  - MessageReferenceListOperation
 *
 * @author Joze Rihtarsic
 */
public class MessagingAPIParameterGenerator {


    MessagingObjectPathUtils pathUtils = new MessagingObjectPathUtils();
    Components components;
    MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation;
    String messagingAPIURL;
    public MessagingAPIParameterGenerator(Components components) {
        this(components, MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS,null);
    }

    public MessagingAPIParameterGenerator(Components components, MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation, String messagingAPIURL) {
        this.components = components;
        this.messagingAPIDefinitionsLocation = messagingAPIDefinitionsLocation;
        this.messagingAPIURL = messagingAPIURL;
    }

    /**
     * Creates parameters according to MessageSubmissionOperation configuration
     * @param annotatedOperation
     * @return List of operational parameters
     */
    public List<Parameter> createOperationParameters(SubmitMessageOperation annotatedOperation) {
        // --------------------------
        // create parameter
        List<Parameter> methodParameters = createMessagingParameters(true, false);
        // creates message id parameter
        Parameter pathParameterMessageId = createMessagingParameterForTypeWithName(MessagingParameterType.MESSAGE_ID,annotatedOperation.messageIdParamName() );
        methodParameters.add(pathParameterMessageId);

        createServiceAndActionParameters(annotatedOperation.service(), annotatedOperation.action(), methodParameters);

        //  create webhook signal parameter
        if (annotatedOperation.useSignalWebhook()) {
            methodParameters.add(createMessagingParameterForType(MessagingParameterType.SIGNAL_WEBHOOK));
        }
        if (annotatedOperation.useMessageWebhook()) {
            methodParameters.add(createMessagingParameterForType(MessagingParameterType.RESPONSE_WEBHOOK));
        }
        return methodParameters;
    }

    /**
     * Creates parameters according to ResponseMessageReferencePullOperation configuration
     * @param annotatedOperation
     * @return List of operational parameters
     */
    public List<Parameter> createOperationParameters(GetResponseMessageReferenceListOperation annotatedOperation) {
        // --------------------------
        // create parameter
        List<Parameter> methodParameters = new ArrayList<>();
        // creates message id parameter
        Parameter pathParameterMessageId = createMessagingParameterForTypeWithName(MessagingParameterType.MESSAGE_ID,annotatedOperation.messageIdParamName() );
        methodParameters.add(pathParameterMessageId);

        createServiceAndActionParameters(annotatedOperation.service(), annotatedOperation.action(), methodParameters);

        switch (annotatedOperation.operationType()) {
            case RESPONSE_ACTION:
                if (StringUtils.isBlank(annotatedOperation.action())) {
                    Parameter pathParameterAction = createMessagingParameterForType(MessagingParameterType.RESPONSE_ACTION);
                    methodParameters.add(pathParameterAction);
                }
            case RESPONSE_SERVICE:
                if (StringUtils.isBlank(annotatedOperation.responseService())) {
                    Parameter pathParameterResponseService = createMessagingParameterForType(MessagingParameterType.RESPONSE_SERVICE);
                    methodParameters.add(pathParameterResponseService);
                }
        }
        return methodParameters;
    }

    /**
     * Creates parameters according to ResponseMessageSubmissionOperation configuration
     * @param annotatedOperation
     * @return List of operational parameters
     */
    public List<Parameter> createOperationParameters(SubmitResponseMessageOperation annotatedOperation) {
        // --------------------------
        // create parameter
        List<Parameter> methodParameters = createMessagingParameters(true, false);
        // creates message id parameter. The object must be created inline if named does not match the definition
        Parameter pathParameterMessageId = createMessagingParameterForTypeWithName(MessagingParameterType.MESSAGE_ID,annotatedOperation.messageIdParamName() );
        methodParameters.add(pathParameterMessageId);

        Parameter pathParameterResponseMessageId = createMessagingParameterForTypeWithName(MessagingParameterType.RESPONSE_MESSAGE_ID,annotatedOperation.responseMessageIdParamName() );
        methodParameters.add(pathParameterResponseMessageId);

        if (!annotatedOperation.isWebhook()) {
            createServiceAndActionParameters(annotatedOperation.service(), annotatedOperation.action(),  methodParameters);
        }

        createResponseServiceAndActionParameters(annotatedOperation.responseService(), annotatedOperation.responseAction(), methodParameters);

        return methodParameters;
    }

    /**
     * Creates parameters according to SignalSubmissionOperation configuration
     * @param annotatedOperation
     * @return List of operational parameters
     */
    public List<Parameter> createOperationParameters(SubmitSignalOperation annotatedOperation) {
        // --------------------------
        // create parameter
        List<Parameter> methodParameters = createMessagingParameters(true, false);
        // creates message id parameter
        Parameter pathParameterMessageId = createMessagingParameterForTypeWithName(MessagingParameterType.MESSAGE_ID,annotatedOperation.messageIdParamName() );
        methodParameters.add(pathParameterMessageId);

        return methodParameters;
    }

    /**
     * Creates parameters according to GetMessageOperation configuration
     * @param annotatedOperation
     * @return List of operational parameters
     */
    public List<Parameter> createOperationParameters(GetMessageOperation annotatedOperation) {
        // --------------------------
        // create parameter
        List<Parameter> methodParameters = new ArrayList<>();
        // creates message id parameter
        Parameter pathParameterMessageId = createMessagingParameterForTypeWithName(MessagingParameterType.MESSAGE_ID,annotatedOperation.messageIdParamName() );
        methodParameters.add(pathParameterMessageId);

        createServiceAndActionParameters(annotatedOperation.service(), annotatedOperation.action(), methodParameters);

        return methodParameters;
    }

    /**
     * Creates parameters according to GetResponseMessageOperation configuration
     * @param annotatedOperation
     * @return List of operational parameters
     */
    public List<Parameter> createOperationParameters(GetResponseMessageOperation annotatedOperation) {
        // --------------------------
        // create parameter
        List<Parameter> methodParameters = new ArrayList<>();
        // creates message id parameter
        Parameter pathParameterMessageId = createMessagingParameterForTypeWithName(MessagingParameterType.MESSAGE_ID,annotatedOperation.messageIdParamName() );
        methodParameters.add(pathParameterMessageId);

        Parameter pathParameterResponseMessageId = createMessagingParameterForTypeWithName(MessagingParameterType.RESPONSE_MESSAGE_ID,annotatedOperation.responseMessageIdParamName() );
        methodParameters.add(pathParameterResponseMessageId);

        createServiceAndActionParameters(annotatedOperation.service(), annotatedOperation.action(), methodParameters);
        createResponseServiceAndActionParameters(annotatedOperation.responseService(), annotatedOperation.responseAction(), methodParameters);

        return methodParameters;
    }

    /**
     * Creates parameters according to MessageReferenceListOperation configuration
     * @param annotatedOperation
     * @return List of operational parameters
     */
    public List<Parameter> createOperationParameters(GetMessageReferenceListOperation annotatedOperation) {
        List<Parameter> methodParameters = new ArrayList<>();
        switch (annotatedOperation.operationType()) {
            case SERVICE_AND_ACTION:
                if (StringUtils.isBlank(annotatedOperation.action())) {
                    methodParameters.add(createMessagingParameterForType(MessagingParameterType.ACTION));
                }
            case SERVICE:
                if (StringUtils.isBlank(annotatedOperation.service())) {
                    methodParameters.add(createMessagingParameterForType(MessagingParameterType.SERVICE));
                }
        }
        return methodParameters;
    }


    protected void createServiceAndActionParameters(String service, String action, List<Parameter> methodParameters) {
        if (StringUtils.isBlank(service)) {
            Parameter pathParameterService = createMessagingParameterForType(MessagingParameterType.SERVICE);
            methodParameters.add(pathParameterService);
        }

        if (StringUtils.isBlank(action)) {
            Parameter pathParameterAction = createMessagingParameterForType(MessagingParameterType.ACTION);
            methodParameters.add(pathParameterAction);
        }
    }

    protected void createResponseServiceAndActionParameters(String responseService, String responseAction, List<Parameter> methodParameters) {
        if (StringUtils.isBlank(responseService)) {
            Parameter pathParameterResponseService = createMessagingParameterForType(MessagingParameterType.RESPONSE_SERVICE);
            methodParameters.add(pathParameterResponseService);
        }

        if (StringUtils.isBlank(responseAction)) {
            Parameter pathParameterResponseAction = createMessagingParameterForType(MessagingParameterType.RESPONSE_ACTION);
            methodParameters.add(pathParameterResponseAction);
        }
    }


    protected List<Parameter> createMessagingParameters(boolean onlyHeader, boolean addWebhookParameters) {
        return Arrays.asList(MessagingParameterType.values()).stream()
                .filter(parameterType -> !parameterType.isPayloadPart()
                        && (!onlyHeader || parameterType.getLocation() == MessagingParameterLocationType.HEADER)
                        && !parameterType.isPayloadPart()
                        && (addWebhookParameters || !parameterType.isWebhookParameters()))
                .map(parameterType -> createMessagingParameterForType(parameterType)).collect(Collectors.toList());
    }


    /**
     * Method validates of parameter name matches the difinition. if not then parameter is generated INLINE
     * @param parameterType
     * @param parameterName
     * @return
     */
    private Parameter createMessagingParameterForTypeWithName(MessagingParameterType parameterType, String parameterName) {
        // creates message id parameter. The object must be created inline if named does not match the definition
        Parameter parameter
                = StringUtils.equals(parameterName,parameterType.getName() )?
                createMessagingParameterForType(parameterType): createMessagingParameterForTypePrivate(parameterType);
        parameter.setName(parameterName);
        return parameter;
    }
    /**
     * Method returns messaging api parameter according to parameterDefinitionLocation. If parameterDefinitionLocation is set to document
     * then Parameter is added to #/components/parameters/ in case MESSAGING_API then parameter reference is set to
     * ${messagingAPIURL}#/components/parameters/, in case MESSAGING_API_OBJECT: ${messagingAPIURL}/parameterType.getName() + ".json"
     *
     * @param parameterType MessagingParameterType
     * @return OpenApi Parameter object.
     */
    private Parameter createMessagingParameterForType(MessagingParameterType parameterType) {

        String definitionURI = pathUtils.getDefinitionURI(messagingAPIDefinitionsLocation, parameterType.getMessagingSchemaType(), MessagingConstants.OPENAPI_SUBPATH_PARAMETERS, messagingAPIURL );
        Parameter parameter;
        switch (messagingAPIDefinitionsLocation){
            case DOCUMENT_COMPONENTS:{
                if (!components.getParameters().containsKey(parameterType.getName())) {
                    Parameter parameterSchema = createMessagingParameterForTypePrivate(parameterType);
                    components.getParameters().put(parameterType.getName(), parameterSchema);
                }
            }
            case MESSAGING_API_OBJECT:
            case MESSAGING_API_COMPONENTS:{
                // this part is common to DOCUMENT_COMPONENTS, MESSAGING_API_OBJECT and MESSAGING_API_COMPONENTS!
                parameter = new Parameter()
                        .$ref(definitionURI);
                parameter.setOnlyRef(true);
                break;
            }
            case INLINE:
            default:
                parameter = createMessagingParameterForTypePrivate(parameterType);

        }
        return parameter;
    }


    protected Parameter createMessagingParameterForTypePrivate(MessagingParameterType parameterType) {

        Schema schema;
        if (parameterType.getSchema() != null) {
            if (!components.getSchemas().containsKey(parameterType.getSchema().getName())) {
                components.addSchemas(parameterType.getSchema().getName(), parameterType.getSchema());
            }
            schema = new Schema().$ref(OPENAPI_REF_PATH_SCHEMAS + parameterType.getSchema().getName());
        } else {
            schema = new StringSchema().format(parameterType.getFormat());
        }

        Parameter parameter = new Parameter()
                .name(parameterType.getName())
                .description(parameterType.getDescription())
                .in(parameterType.getLocation().getName())
                .schema(schema).example(parameterType.getExample());

        return parameter;
    }
}
