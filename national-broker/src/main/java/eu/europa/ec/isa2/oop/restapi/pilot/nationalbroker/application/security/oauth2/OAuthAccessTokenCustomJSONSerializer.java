package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class OAuthAccessTokenCustomJSONSerializer extends StdSerializer<OAuthAccessToken_NationalBroker> {

    public OAuthAccessTokenCustomJSONSerializer(){this(null);}

    public OAuthAccessTokenCustomJSONSerializer(Class<OAuthAccessToken_NationalBroker> t) {
        super(t);
    }

    @Override
    public void serialize(OAuthAccessToken_NationalBroker oAuthAccessTokenNationalBroker, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("access_token", oAuthAccessTokenNationalBroker.getTokenValue());
        jsonGenerator.writeStringField("expires_in", Long.toString(oAuthAccessTokenNationalBroker.getExpiresInSeconds()));
        if(oAuthAccessTokenNationalBroker.getIssuedAt() != null){
            jsonGenerator.writeStringField("iat", Long.toString(oAuthAccessTokenNationalBroker.getIssuedAt().getEpochSecond()));
        }
        jsonGenerator.writeStringField("token_type", oAuthAccessTokenNationalBroker.getTokenType().getValue());
        jsonGenerator.writeEndObject();
    }
}

