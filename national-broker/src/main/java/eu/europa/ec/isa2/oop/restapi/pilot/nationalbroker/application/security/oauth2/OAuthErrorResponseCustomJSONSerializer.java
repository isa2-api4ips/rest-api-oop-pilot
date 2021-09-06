package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class OAuthErrorResponseCustomJSONSerializer extends StdSerializer<OAuthErrorResponse> {

    public OAuthErrorResponseCustomJSONSerializer(){this(null);};

    public OAuthErrorResponseCustomJSONSerializer(Class<OAuthErrorResponse> t) {
        super(t);
    }

    @Override
    public void serialize(OAuthErrorResponse oAuthErrorResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("error", oAuthErrorResponse.getErrorCode().toString());
        if(StringUtils.isNotEmpty(oAuthErrorResponse.getErrorDescription())){
            jsonGenerator.writeStringField("error_description", oAuthErrorResponse.getErrorDescription());
        }
        if(StringUtils.isNotEmpty(oAuthErrorResponse.getErrorURI())){
            jsonGenerator.writeStringField("error_uri", oAuthErrorResponse.getErrorURI());
        }
        jsonGenerator.writeEndObject();
    }
}

