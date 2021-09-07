package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.services.StoragesService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.DSDRequestLoggerDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.DSDRequestLogEntity;
import eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DSDClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(DSDClientHttpRequestInterceptor.class);
    private static final byte[] NEW_LINE_SEQUENCE = {'\r', '\n'};
    private static final byte[] HEADER_SEPARATOR = {':', ' '};

    DSDRequestLoggerDao dsdRequestLoggerDao;
    StoragesService storagesService;

    String messageId;
    String service;
    String action;
    String username;

    final Pattern serviceAndAction;

    @Autowired
    public DSDClientHttpRequestInterceptor(DSDRequestLoggerDao dsdRequestLoggerDao, StoragesService storagesService) {
        this.dsdRequestLoggerDao = dsdRequestLoggerDao;
        this.storagesService = storagesService;

        serviceAndAction = Pattern.compile(".*\\/v\\d*\\/messaging\\/(?<service>[^\\/]+)\\/(?<action>[^\\/]+)\\/?.*");
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        parseServiceAndAction(request.getURI().getPath());
        DSDRequestLogEntity requestLogEntity = logRequest(request, body, request.getMethodValue().toLowerCase() +" "+ request.getURI().getPath());
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response, requestLogEntity);
        return response;
    }

    private DSDRequestLogEntity logRequest(HttpRequest request, byte[] body, String targetRequest) throws IOException {
        // get output request file
        File requestFile = storagesService.getNewStorageFile("bin", "dsd-request");
        // log to output stream
        try (FileOutputStream fos = new FileOutputStream(requestFile); InputStream inputStream = new ByteArrayInputStream(body)) {
            logHttpHeadersAndBodyToFile(fos, request.getHeaders(), inputStream, targetRequest);
        }
        // log to file
        return dsdRequestLoggerDao.logRequest(storagesService.getRelativePath(requestFile),
                messageId, service,
                action, username,
                request.getURI().getPath(), request.getMethodValue());
    }

    private void logResponse(ClientHttpResponse response, DSDRequestLogEntity requestLogEntity) throws IOException {
        File responseFile = storagesService.getNewStorageFile("bin", "dsd-response");

        try (FileOutputStream fos = new FileOutputStream(responseFile)) {
            logHttpHeadersAndBodyToFile(fos, response.getHeaders(), response.getBody(), null);
        }

        // prepare for re-reading the input
        requestLogEntity.setResponseOn(Calendar.getInstance().getTime());
        requestLogEntity.setResponseStoragePath(storagesService.getRelativePath(responseFile));
        requestLogEntity.setDsdStatus(response.getStatusText());
        dsdRequestLoggerDao.logResponseForRequest(requestLogEntity);
    }

    private void logHttpHeadersAndBodyToFile(OutputStream logStream, HttpHeaders headers, InputStream inputStreamBody, String targetRequest) throws IOException {

        if (!StringUtils.isBlank(targetRequest)){
            logStream.write(MessagingConstants.HEADER_REQUEST_TARGET.getBytes());
            logStream.write(HEADER_SEPARATOR);
            logStream.write(targetRequest.getBytes());
            logStream.write(NEW_LINE_SEQUENCE);
        }
        // log header
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                logStream.write(entry.getKey().getBytes());
                logStream.write(HEADER_SEPARATOR);
                logStream.write(value.getBytes());
                logStream.write(NEW_LINE_SEQUENCE);
            }
        }
        logStream.write(NEW_LINE_SEQUENCE);
        // log body
        byte[] buf = new byte[8192];
        int length;
        while ((length = inputStreamBody.read(buf)) > 0) {
            logStream.write(buf, 0, length);
        }
    }

    public void clearContextData() {

        messageId = null;
        service = null;
        action = null;
        username = null;
    }

    public void setContextMessageId(String messageId, String username) {
        this.messageId = messageId;
        this.username = username;
    }

    public void parseServiceAndAction(String path){
        LOG.info("Parse path [{}]", path);
        Matcher serviceActionMatcher = serviceAndAction.matcher(path);
        if (serviceActionMatcher.find() || serviceActionMatcher.groupCount() == 2){
            service = serviceActionMatcher.group("service");
            action = serviceActionMatcher.group("action");
        } else {
            LOG.error("Could not match service and action in path: [{}]. Group count [{}]", path, serviceActionMatcher.groupCount());
            service = null;
            action = null;
        }
    }

    public String getCurrentMessageId() {
        return messageId;
    }

    public String getCurrentService() {
        return service;
    }

    public String getCurrentAction() {
        return action;
    }

    public String getCurrentUsername() {
        return username;
    }
}