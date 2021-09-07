package eu.europa.ec.isa2.restapi.reader;

import java.lang.reflect.Method;

public class MessagingAPIOperation {

    Object object;
    Method method;

    String service;
    String action;

    String responseService;
    String responseAction;

    boolean useMessageWebhook=false;
    boolean useSignalWebhook=false;

    public MessagingAPIOperation(Object object, Method method, String service, String action) {
        this.object = object;
        this.method = method;
        this.service = service;
        this.action = action;
    }

    public MessagingAPIOperation(Object object, Method method, String service, String action, String responseService, String responseAction) {
        this.object = object;
        this.method = method;
        this.service = service;
        this.action = action;
        this.responseService = responseService;
        this.responseAction = responseAction;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResponseService() {
        return responseService;
    }

    public void setResponseService(String responseService) {
        this.responseService = responseService;
    }

    public String getResponseAction() {
        return responseAction;
    }

    public void setResponseAction(String responseAction) {
        this.responseAction = responseAction;
    }

    public boolean isUseMessageWebhook() {
        return useMessageWebhook;
    }

    public void setUseMessageWebhook(boolean useMessageWebhook) {
        this.useMessageWebhook = useMessageWebhook;
    }

    public boolean isUseSignalWebhook() {
        return useSignalWebhook;
    }

    public void setUseSignalWebhook(boolean useSignalWebhook) {
        this.useSignalWebhook = useSignalWebhook;
    }
}
