package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging;


import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import eu.europa.ec.isa2.restapi.jws.SignedMimeMultipart;
import eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import eu.europa.eu.dsd.messaging.gen.dataset.model.*;
import eu.europa.eu.dsd.messaging.gen.organization.model.MessageStatusResponse;
import eu.europa.eu.dsd.messaging.gen.organization.model.OrganizationRO;
import eu.europa.eu.dsd.messaging.gen.organization.model.SearchParameters;
import eu.europa.eu.dsd.messaging.gen.organization.model.SearchResponseList;
import eu.europa.eu.dsd.messaging.gen.organization.model.SignalMessage;
import eu.europa.eu.dsd.messaging.gen.organization.model.*;
import io.swagger.v3.core.util.Json;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.InputStreamDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MultipartMimeDsDConverter
        extends AbstractHttpMessageConverter<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(MultipartMimeDsDConverter.class);

    JwsService jwsService;

    public MultipartMimeDsDConverter(JwsService jwsService) {
        super(MediaType.MULTIPART_MIXED);
        this.jwsService = jwsService;
    }

    public MultipartMimeDsDConverter() {
        super(MediaType.MULTIPART_MIXED);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        boolean supports = SearchParameters.class.isAssignableFrom(clazz)
                || SearchResponseList.class.isAssignableFrom(clazz)
                || UpdateOrganizationRequest.class.isAssignableFrom(clazz)
                || SignalMessage.class.isAssignableFrom(clazz)
                || eu.europa.eu.dsd.messaging.gen.dataset.model.SearchResponseList.class.isAssignableFrom(clazz)
                || eu.europa.eu.dsd.messaging.gen.dataset.model.SearchParameters.class.isAssignableFrom(clazz)
                || eu.europa.eu.dsd.messaging.gen.dataset.model.DatasetRequest.class.isAssignableFrom(clazz)
                || eu.europa.eu.dsd.messaging.gen.dataset.model.DatasetCreateRequest.class.isAssignableFrom(clazz)
                || eu.europa.eu.dsd.messaging.gen.dataset.model.DatasetDeleteRequest.class.isAssignableFrom(clazz)
                || MessageStatusResponse.class.isAssignableFrom(clazz)
                || eu.europa.eu.dsd.messaging.gen.dataset.model.MessageStatusResponse.class.isAssignableFrom(clazz);

        LOG.info("supports: [{}], result: [{}]", clazz.getName(), supports);
        return supports;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        LOG.info("readInternal: [{}], result: [{}]", clazz.getName());

        Multipart multipart = extractPostRequestBody(inputMessage);

        Object result = null;
        try {
            if (clazz.equals(SearchParameters.class)) {
                OrganizationQuery value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(OrganizationQuery.class);
                result = new SearchParameters().searchParameter(value);
            } else if (clazz.equals(SearchResponseList.class)) {
                OrganizationSearchResult value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(OrganizationSearchResult.class);
                result = new SearchResponseList().responseList(value);
            } else if (clazz.equals(UpdateOrganizationRequest.class)) {
                OrganizationRO value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(OrganizationRO.class);
                result = new UpdateOrganizationRequest().updateOrganization(value);
            } else if (clazz.equals(DatasetRequest.class)) {
                DatasetRO value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(DatasetRO.class);
                result = new DatasetRequest().datasetUpdate(value);
            } else if (clazz.equals(DatasetCreateRequest.class)) {
                DatasetRO value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(DatasetRO.class);
                result = new DatasetCreateRequest().datasetCreate(value);
            } else if (clazz.equals(DatasetDeleteRequest.class)) {
                DatasetRO value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(DatasetRO.class);
                result = new DatasetDeleteRequest().datasetDelete(value);
            } else if (clazz.equals(MessageStatusResponse.class)) {
                OrganizationStatusResult value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(OrganizationStatusResult.class);
                result = new MessageStatusResponse().messageStatusResponse(value);
            } else if (clazz.equals(eu.europa.eu.dsd.messaging.gen.dataset.model.MessageStatusResponse.class)) {
                DatasetStatusResult value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(DatasetStatusResult.class);
                result = new eu.europa.eu.dsd.messaging.gen.dataset.model.MessageStatusResponse().messageStatusResponse(value);
            } else if (clazz.equals(eu.europa.eu.dsd.messaging.gen.dataset.model.SearchResponseList.class)) {
                DatasetSearchResult value = Json.mapper().createParser(multipart.getBodyPart(0).getInputStream()).readValueAs(DatasetSearchResult.class);
                result = new eu.europa.eu.dsd.messaging.gen.dataset.model.SearchResponseList().responseList(value);
            }
        } catch (MessagingException e) {
            new IOException("Error occured while parsing payload", e);
        }
        return result;
    }

    MimeMultipart extractPostRequestBody(HttpInputMessage request) throws IOException {

        try {
            return new MimeMultipart(new InputStreamDataSource(request.getBody(), MediaType.MULTIPART_MIXED_VALUE));
        } catch (MessagingException e) {
            // for the demo just log!
            LOG.error("Error occurred while parsing the mime input the payload", e);
        }
        return null;
    }

    @Override
    protected void writeInternal(Object request, HttpOutputMessage outputMessage)
            throws HttpMessageNotWritableException {

        if (outputMessage.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE)) {
            LOG.info("remove Content-Type because it will be added with boundaries");
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
        // Find better approach for non pilot project!!
        List<String> requestTargetList = null;
        if (outputMessage.getHeaders().containsKey("Request-Target-PRIVATE")) {
            requestTargetList = outputMessage.getHeaders().remove("Request-Target-PRIVATE");
        }


        Map<String, String> headers = new HashMap<>();
        outputMessage.getHeaders().forEach((header, value) -> {
            LOG.info("Add existing header: [{}], [{}]", header, value);
            // sign only headers which occurred only once!
            if (value.size() == 1) {
                headers.put(header, value.get(0));
            }
        });
        if (requestTargetList != null && !requestTargetList.isEmpty()) {
            String requestTarget = requestTargetList.get(0);
            headers.put(MessagingConstants.HEADER_REQUEST_TARGET, requestTarget);
            LOG.warn("ADD  to sign (Request-Target) [{}]", requestTarget);
        }

        Object jsonObject = null;
        if (request instanceof SearchParameters) {
            jsonObject = ((SearchParameters) request).getSearchParameter();
        } else if (request instanceof UpdateOrganizationRequest) {
            jsonObject = ((UpdateOrganizationRequest) request).getUpdateOrganization();
        } else if (request instanceof eu.europa.eu.dsd.messaging.gen.dataset.model.SearchParameters) {
            jsonObject = ((eu.europa.eu.dsd.messaging.gen.dataset.model.SearchParameters) request).getSearchParameter();
        } else if (request instanceof DatasetRequest) {
            jsonObject = ((DatasetRequest) request).getDatasetUpdate();
        } else if (request instanceof DatasetCreateRequest) {
            jsonObject = ((DatasetCreateRequest) request).getDatasetCreate();
        }else if (request instanceof DatasetDeleteRequest) {
            jsonObject = ((DatasetDeleteRequest) request).getDatasetDelete();
        }

        try {
            SignedMimeMultipart multipartResponse = jwsService.createSignedMimeMultipartFromJson(jsonObject, headers);
            // set headers
            multipartResponse.getHttpHeaders().forEach((headerName, value) -> {
                if (!StringUtils.equalsIgnoreCase(MessagingConstants.HEADER_REQUEST_TARGET, headerName)) {
                    if (outputMessage.getHeaders().containsKey(headerName)) {
                        // remove old header and add signed one!
                        outputMessage.getHeaders().remove(headerName);
                    }
                    outputMessage.getHeaders().add(headerName, value);
                }
            });

            // write to
            OutputStream outputStream = outputMessage.getBody();
            multipartResponse.writeTo(outputStream);
            outputStream.close();
        } catch (MessagingException | IOException e) {
            throw new HttpMessageNotWritableException("Error occurred while generating signed MimeMultipart!", e);
        }
    }

}
