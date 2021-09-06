package eu.europa.ec.isa2.oop.restapi;

import eu.europa.ec.isa2.restapi.profile.MessagingOpenApi;
import eu.europa.ec.isa2.restapi.profile.annotation.*;
import eu.europa.ec.isa2.restapi.profile.docsapi.exceptions.MessagingAPIException;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingEndpointType;
import eu.europa.ec.isa2.restapi.reader.MessagingAPIOperation;
import io.swagger.v3.core.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.isa2.restapi.profile.enums.APIProblemType.VALIDATION_FAILED;

@Service
public class APIRegistration {


    private static final Logger LOG = LoggerFactory.getLogger(APIRegistration.class);

    List<MessagingAPIOperation> submissionMessageAsyncMethods = new ArrayList<>();
    List<MessagingAPIOperation> submissionMessageSyncMethods = new ArrayList<>();
    List<MessagingAPIOperation> submissionSignalMessageMethods = new ArrayList<>();
    List<MessagingAPIOperation> submissionSignalMessageMethodsWebhook = new ArrayList<>();
    List<MessagingAPIOperation> submissionResponseMessageMethods = new ArrayList<>();
    List<MessagingAPIOperation> submissionResponseMessageMethodsWebhook = new ArrayList<>();



    List<MessagingAPIOperation> messageReferenceListMethods = new ArrayList<>();
    List<MessagingAPIOperation> getMessageMethods = new ArrayList<>();
    List<MessagingAPIOperation> responseMessageReferenceMethods = new ArrayList<>();
    List<MessagingAPIOperation> getResponseMessageMethods = new ArrayList<>();


    List<MessagingOpenApi> messagingOpenApis;

    public APIRegistration(@Autowired List<MessagingOpenApi> messagingOpenApis) {
        this.messagingOpenApis = messagingOpenApis;
    }

    @PostConstruct
    protected void initializeServices() {
        LOG.info("Initialize beans:" + messagingOpenApis.size());
        for (MessagingOpenApi messagingOpenApi : messagingOpenApis) {
            registerApi(messagingOpenApi);
        }

    }
    public MessagingAPIOperation getMethodByType(MessagingEndpointType type, String service, String action ){
        return getMethodByType(type, service, action, null, null);


    }
    public MessagingAPIOperation getMethodByType(MessagingEndpointType type, String service, String action,  String rService, String rAction){
        List<MessagingAPIOperation> listOfMethods = null;
        switch (type){
            case GET_MESSAGE:
                listOfMethods = getMessageMethods;
                break;
            case GET_MESSAGE_REFERENCE_LIST:
                listOfMethods = messageReferenceListMethods;
                break;
            case GET_RESPONSE_MESSAGE:
                listOfMethods = getResponseMessageMethods;
                break;
            case GET_RESPONSE_MESSAGE_REFERENCE_LIST:
                listOfMethods = responseMessageReferenceMethods;
                break;
            case SUBMIT_MESSAGE_SYNC:
                listOfMethods = submissionMessageSyncMethods;
                break;
            case SUBMIT_MESSAGE_ASYNC:
                listOfMethods = submissionMessageAsyncMethods;
                break;
            case SUBMIT_RESPONSE_MESSAGE:
                listOfMethods = submissionResponseMessageMethods;
                break;
            case SUBMIT_RESPONSE_MESSAGE_WEBHOOK:
                listOfMethods = submissionResponseMessageMethodsWebhook;
                break;
            case SUBMIT_SIGNAL:
                listOfMethods = submissionSignalMessageMethods;
                break;
            case SUBMIT_SIGNAL_WEBHOOK:
                listOfMethods = submissionSignalMessageMethodsWebhook;
                break;
        }
        if (listOfMethods ==null) {
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Messaging endpoint type: ["+type+"] is not supported!", null);
        }
        List<MessagingAPIOperation> messagingAPIOperations = getMethodsByServiceActionAndResponseServiceAction(service, action, rService, rAction, listOfMethods);

        if (messagingAPIOperations.isEmpty()) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    String.format("No method found for service [%s] and action: [%s]", service, action),
                   null);
        }

        if (messagingAPIOperations.size()> 1) {
            throw new MessagingAPIException(VALIDATION_FAILED,
                    String.format("More then one implementation of the [%s] is registered for the  service:[%s], action :[%s]!",
                            type, service, action), null);
        }
        return messagingAPIOperations.get(0);
    }

    public List<MessagingAPIOperation> getSyncSubmissionMethods(String service, String action) {
        return getMethodsByServiceAndAction(service, action, submissionMessageSyncMethods);
    }

    public List<MessagingAPIOperation> getAsyncSubmissionMethods(String service, String action) {
        return getMethodsByServiceAndAction(service, action, submissionMessageAsyncMethods);
    }

    public List<MessagingAPIOperation> getMessageReferenceListMethods(String service, String action) {
        return getMethodsByServiceAndAction(service, action, messageReferenceListMethods);
    }

    public List<MessagingAPIOperation> getGetMessageMethods(String service, String action) {
        LOG.info("getGetMessageMethods for service [{}] and action [{}] ", service, action);
        return getMethodsByServiceAndAction(service, action, getMessageMethods);
    }

    public List<MessagingAPIOperation> getResponseMessageReferenceMethods(String service, String action, String responseService, String responseAction) {
        LOG.info("getResponseMessageReferenceMethods for service [{}] and action [{}], responseService [{}] and responseAction [{}] ", service, action, responseService, responseAction);
        return getMethodsByServiceActionAndResponseServiceAction(service, action,responseService, responseAction, responseMessageReferenceMethods);
    }

    public List<MessagingAPIOperation> getGetResponseMessageMethods(String service, String action, String responseService, String responseAction) {
        LOG.info("getGetResponseMessageMethods for service [{}] and action [{}], responseService [{}] and responseAction [{}] ", service, action, responseService, responseAction);
        return getMethodsByServiceActionAndResponseServiceAction(service, action,responseService,responseAction, getResponseMessageMethods);
    }

    public List<MessagingAPIOperation> getSignalMethods() {
        return submissionSignalMessageMethods;
    }

    public List<MessagingAPIOperation> getMethodsByServiceAndAction(String service, String action, List<MessagingAPIOperation> methodList) {
        LOG.info("getMethodsByServiceAndAction for service [{}] and action [{}], list size [{}]", service, action, methodList.size());
        return getMethodsByServiceActionAndResponseServiceAction(service, action, null, null, methodList);
    }

    public List<MessagingAPIOperation> getMethodsByServiceActionAndResponseServiceAction(String service, String action, String responseService, String responseAction, List<MessagingAPIOperation> methodList) {
        LOG.info("getMethodsByServiceAndAction for service [{}] and action [{}], responseService [{}] and responseAction [{}], list size [{}]", service, action,responseService, responseAction,  methodList.size());
        return methodList.stream().filter(messagingApiOperation ->
                   (StringUtils.isAllBlank(service, messagingApiOperation.getService())                 || StringUtils.equals(service, messagingApiOperation.getService()))
                && (StringUtils.isAllBlank(action, messagingApiOperation.getAction())                   || StringUtils.equals(action, messagingApiOperation.getAction()))
                && (StringUtils.isAllBlank(responseService, messagingApiOperation.getResponseService()) || StringUtils.equals(responseService, messagingApiOperation.getResponseService()))
                && (StringUtils.isAllBlank(responseAction, messagingApiOperation.getResponseAction())   || StringUtils.equals(responseAction, messagingApiOperation.getResponseAction()))
        ).collect(Collectors.toList());
    }

    public void registerApi(Object messagingService) {
        LOG.info("registerApi target: [{}].", messagingService);
        final List<Method> methods = Arrays.stream(messagingService.getClass().getMethods())
                .collect(Collectors.toList());

        // iterate class methods
        for (Method method : methods) {

            SubmitMessageOperation submitMessageOperation = ReflectionUtils.getAnnotation(method, SubmitMessageOperation.class);
            if (submitMessageOperation != null) {
                parseMessageSubmissionOperation(messagingService, method, submitMessageOperation);
                continue;
            }

            SubmitSignalOperation submitSignalOperation = ReflectionUtils.getAnnotation(method, SubmitSignalOperation.class);
            if (submitSignalOperation != null) {
                parseSignalSubmissionOperation(messagingService, method, submitSignalOperation);
                continue;
            }

            SubmitResponseMessageOperation submitResponseMessageOperation = ReflectionUtils.getAnnotation(method, SubmitResponseMessageOperation.class);
            if (submitResponseMessageOperation != null) {
                parseResponseMessageSubmissionOperation(messagingService, method, submitResponseMessageOperation);
                continue;
            }

            GetMessageReferenceListOperation getMessageReferenceListOperation = ReflectionUtils.getAnnotation(method, GetMessageReferenceListOperation.class);
            if (getMessageReferenceListOperation != null) {
                parseMessageReferenceListOperation(messagingService, method, getMessageReferenceListOperation);
                continue;
            }

            GetMessageOperation getMessageOperation = ReflectionUtils.getAnnotation(method, GetMessageOperation.class);
            if (getMessageOperation != null) {
                parseGetMessageOperation(messagingService, method, getMessageOperation);
                continue;
            }

            GetResponseMessageReferenceListOperation getResponseMessageReferenceListOperation = ReflectionUtils.getAnnotation(method, GetResponseMessageReferenceListOperation.class);
            if (getResponseMessageReferenceListOperation != null) {
                parseResponseMessageReferencePullOperation(messagingService, method, getResponseMessageReferenceListOperation);
                continue;
            }

            GetResponseMessageOperation getResponseMessageOperation = ReflectionUtils.getAnnotation(method, GetResponseMessageOperation.class);
            if (getResponseMessageOperation != null) {
                parseGetResponseMessageOperation(messagingService, method, getResponseMessageOperation);
                continue;
            }
        }
    }

    public void parseSignalSubmissionOperation(Object target, Method operation, SubmitSignalOperation submitSignalOperation) {
        LOG.info("parseGetResponseMessageOperation target: [{}], operation: [{}].", target, operation);
        MessagingAPIOperation messagingApiOperation = new MessagingAPIOperation(target, operation, null, null);
        if (submitSignalOperation.isWebhook()) {
            submissionSignalMessageMethodsWebhook.add(messagingApiOperation);
        }else {
            submissionSignalMessageMethods.add(messagingApiOperation);
        }
    }

    public void parseGetResponseMessageOperation(Object target, Method operation, GetResponseMessageOperation responseMessageReferencePullOperation) {
        LOG.info("parseGetResponseMessageOperation target: [{}], operation: [{}].", target, operation);
        MessagingAPIOperation messagingApiOperation = new MessagingAPIOperation(target, operation,
                responseMessageReferencePullOperation.service(), responseMessageReferencePullOperation.action(),
                responseMessageReferencePullOperation.responseService(), responseMessageReferencePullOperation.responseAction());
        getResponseMessageMethods.add(messagingApiOperation);
    }

    public void parseResponseMessageReferencePullOperation(Object target, Method operation, GetResponseMessageReferenceListOperation getResponseMessageReferenceListOperation) {
        LOG.info("parseResponseMessageReferencePullOperation target: [{}], operation: [{}].", target, operation);
        MessagingAPIOperation messagingApiOperation = new MessagingAPIOperation(target, operation,
                getResponseMessageReferenceListOperation.service(), getResponseMessageReferenceListOperation.action(),
                getResponseMessageReferenceListOperation.responseService(), getResponseMessageReferenceListOperation.responseAction());
        responseMessageReferenceMethods.add(messagingApiOperation);
    }

    public void parseGetMessageOperation(Object target, Method operation, GetMessageOperation getMessageOperation) {
        LOG.info("parseGetMessageOperation target: [{}], operation: [{}].", target, operation);
        LOG.info("parseGetMessageOperation set service: [{}], action: [{}].", getMessageOperation.service(), getMessageOperation.action());
        MessagingAPIOperation messagingApiOperation = new MessagingAPIOperation(target, operation, getMessageOperation.service(), getMessageOperation.action());
        getMessageMethods.add(messagingApiOperation);
    }

    public void parseMessageSubmissionOperation(Object target, Method operation, SubmitMessageOperation submitMessageOperation) {
        LOG.info("parseMessageSubmissionOperation target: [{}], operation: [{}].", target, operation);
        MessagingAPIOperation messagingApiOperation = new MessagingAPIOperation(target, operation, submitMessageOperation.service(), submitMessageOperation.action());

        if (submitMessageOperation.sync()) {
            submissionMessageSyncMethods.add(messagingApiOperation);
        } else {
            // webhook is only for async
            messagingApiOperation.setUseMessageWebhook(submitMessageOperation.useMessageWebhook());
            messagingApiOperation.setUseSignalWebhook(submitMessageOperation.useSignalWebhook());
            submissionMessageAsyncMethods.add(messagingApiOperation);
        }
    }

    public void parseResponseMessageSubmissionOperation(Object target, Method operation, SubmitResponseMessageOperation annotatedOperation) {
        LOG.info("parseResponseMessageSubmissionOperation target: [{}], operation: [{}].", target, operation);
        MessagingAPIOperation messagingApiOperation = new MessagingAPIOperation(target, operation, annotatedOperation.service(), annotatedOperation.action(),
                annotatedOperation.responseService(), annotatedOperation.responseAction());
        if (annotatedOperation.isWebhook()) {
            submissionResponseMessageMethodsWebhook.add(messagingApiOperation);
        } else {
            submissionResponseMessageMethods.add(messagingApiOperation);
        }
    }

    public void parseMessageReferenceListOperation(Object target, Method operation, GetMessageReferenceListOperation getMessageReferenceListOperation) {
        LOG.info("parseMessageReferenceListOperation target: [{}], operation: [{}].", target, operation);
        MessagingAPIOperation messagingApiOperation = new MessagingAPIOperation(target, operation, getMessageReferenceListOperation.service(), getMessageReferenceListOperation.action());
        messageReferenceListMethods.add(messagingApiOperation);
    }
}
