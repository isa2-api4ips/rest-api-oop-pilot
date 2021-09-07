package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class DSDRestTemplate extends RestTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(DSDRestTemplate.class);

    public DSDRestTemplate(JwsService jwsService,  DSDClientHttpRequestInterceptor interceptor) {

        setRequestFactory(new BufferingClientHttpRequestFactory(getRequestFactory()));
        MultipartMimeDsDConverter multipartMimeDsDConverter = new MultipartMimeDsDConverter(jwsService);
        JsonMimeDsDConverter jsonMimeDsDConverter = new JsonMimeDsDConverter(jwsService);
        getMessageConverters().add(0, multipartMimeDsDConverter);
        getMessageConverters().add(0, jsonMimeDsDConverter);

        getInterceptors().add(interceptor);
    }

    @Override
    public <T> ResponseEntity<T> exchange(RequestEntity<?> entity, ParameterizedTypeReference<T> responseType)
            throws RestClientException {

        // create entity with added headers
        HttpHeaders newHeaders = HttpHeaders.writableHttpHeaders(entity.getHeaders());
        URI uri = entity.getUrl();
        newHeaders.add(HttpHeaders.HOST, uri.getHost() + ":" + uri.getPort());
        //add request target - it will be removed by MultipartMimeDsDConverter
        newHeaders.add("Request-Target-PRIVATE", entity.getMethod().name().toLowerCase() + " " + uri.getPath());
        RequestEntity requestEntity = new RequestEntity(entity.getBody(), newHeaders, entity.getMethod(), entity.getUrl(), entity.getType());
        return super.exchange(requestEntity, responseType);
    }


}
