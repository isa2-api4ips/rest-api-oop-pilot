package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging;


import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import io.swagger.v3.core.util.Json;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class JsonMimeDsDConverter
        extends AbstractHttpMessageConverter<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMimeDsDConverter.class);

    JwsService jwsService;

    public JsonMimeDsDConverter(JwsService jwsService) {
        super(MediaType.APPLICATION_PROBLEM_JSON, MediaType.APPLICATION_JSON);
        this.jwsService = jwsService;
    }

    public JsonMimeDsDConverter() {
        this(null);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // support any class and handle it as json object
        return true;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        LOG.info("readInternal: [{}], result: [{}]", clazz.getName());
        return Json.mapper().createParser(inputMessage.getBody()).readValueAs(clazz);
    }

    @Override
    protected void writeInternal(Object request, HttpOutputMessage outputMessage)
            throws HttpMessageNotWritableException {


        if (outputMessage.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE)) {
            LOG.info("remove Content-Type because it will be added after signature");
            outputMessage.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        }
        // remove Digest it will be recalculated!
        if (outputMessage.getHeaders().containsKey(MessagingConstants.HEADER_DIGEST)) {
            outputMessage.getHeaders().remove(MessagingConstants.HEADER_DIGEST);
        }

        // remove signature because it will be recalculated!
        if (outputMessage.getHeaders().containsKey(MessagingParameterType.EDEL_MESSAGE_SIG.getName())) {
            outputMessage.getHeaders().remove(MessagingParameterType.EDEL_MESSAGE_SIG.getName());
        }
        // remove Request-Target because it is just internal header for signing!
        // pilot projects supports header only for mimepart
        List<String> requestTargetList = null;
        if (outputMessage.getHeaders().containsKey("Request-Target-PRIVATE")) {
            requestTargetList = outputMessage.getHeaders().remove("Request-Target-PRIVATE");
        }

        // parse datetime make sure to write it without ms
        OffsetDateTime dateTime = OffsetDateTime.now();
        if (outputMessage.getHeaders().containsKey(MessagingParameterType.TIMESTAMP.getName())) {
            // just for demo  -:)
            String strTimestamp = outputMessage.getHeaders().get(MessagingParameterType.TIMESTAMP.getName()).get(0);
            dateTime = OffsetDateTime.parse(strTimestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            outputMessage.getHeaders().remove(MessagingParameterType.TIMESTAMP.getName());
        }
        outputMessage.getHeaders().add(MessagingParameterType.TIMESTAMP.getName(),
                dateTime.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME));

        Map<String, String> headers = new HashMap<>();
        outputMessage.getHeaders().forEach((header, value) -> {
            LOG.info("Add existing header: [{}], [{}]", header, value);
            // sign only headers which occurred only once!
            if (value.size() == 1) {
                headers.put(header, value.get(0));
            }
        });

        if (!headers.containsKey(MessagingParameterType.MESSAGE_ID_HEADER.getName())) {
            // al signals and message reference jsons must have messageID
            headers.put(MessagingParameterType.MESSAGE_ID_HEADER.getName(), UUID.randomUUID().toString());
        }

        // set headers
        Map<String, String> httpHeaders;
        try (OutputStream outputStream = outputMessage.getBody()) {
            httpHeaders = jwsService.signAndWriteJsonResponse(request, outputStream, outputMessage.getHeaders().getContentType(), headers);
        } catch (IOException e) {
            throw new HttpMessageNotWritableException("Error occurred while signing json response!", e);
        }

        httpHeaders.forEach((headerName, value) -> {
            if (!StringUtils.equalsIgnoreCase(MessagingConstants.HEADER_REQUEST_TARGET, headerName)) {
                if (outputMessage.getHeaders().containsKey(headerName)) {
                    // remove old header and add signed one!
                    outputMessage.getHeaders().remove(headerName);
                }
                outputMessage.getHeaders().add(headerName, value);
            }
        });
    }

}
