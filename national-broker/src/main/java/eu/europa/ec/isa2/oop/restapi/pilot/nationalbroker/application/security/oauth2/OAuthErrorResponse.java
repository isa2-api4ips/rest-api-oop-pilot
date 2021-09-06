package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

public class OAuthErrorResponse implements Serializable {
    private HttpStatus httpStatusCode;
    private OAuthErrorCode errorCode;
    private String errorDescription;
    private String errorURI;
    private static final String JSON_CONTENT_TYPE = "application/json";

    public OAuthErrorResponse(HttpStatus httpStatusCode, OAuthErrorCode oAuthErrorCode){
        this.httpStatusCode  = httpStatusCode;
        this.errorCode = oAuthErrorCode;
    }

    public enum OAuthErrorCode{
        INVALID_REQUEST,
        INVALID_CLIENT,
        INVALID_GRANT,
        UNAUTHORIZED_CLIENT,
        UNSUPPORTED_GRANT_TYPE,
        INVALID_SCOPE
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

    public OAuthErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorURI() {
        return errorURI;
    }

    public static class Builder{
        private OAuthErrorResponse oAuthErrorResponse;

        /**
         * Forbid creating empty builder
         */
        private Builder() {
            // Nothing to do
        }

        public Builder(final HttpStatus httpStatus, final OAuthErrorCode oAuthErrorCode) {
            oAuthErrorResponse = new OAuthErrorResponse(httpStatus, oAuthErrorCode);
        }

        public Builder withErrorDescription(String errorDescription) {
            if(StringUtils.isNotBlank(errorDescription)){
                oAuthErrorResponse.errorDescription = errorDescription;
            }
            return this;
        }

        public Builder withErrorURI(String errorURI) {
            if(StringUtils.isNotBlank(errorURI)){
                oAuthErrorResponse.errorURI = errorURI;
            }
            return this;
        }

        public ResponseEntity<String> buildJSONServletResponse() throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule("OAuthErrorResponseSerializer", new Version(1, 0, 0, null, null, null));
            module.addSerializer(OAuthErrorResponse.class, new OAuthErrorResponseCustomJSONSerializer());
            objectMapper.registerModule(module);
            String responseJSONString = objectMapper.writeValueAsString(oAuthErrorResponse);
            return ResponseEntity.status(oAuthErrorResponse.httpStatusCode)
                    .body(responseJSONString);
        }
    }
}
