package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

@Schema(type = "object", name = "OAuth Access Token National Broker", description = "Service response object containing OAuth Access Token for National Broker")
public class OAuthAccessTokenRO implements Serializable {
    private String access_token;
    private long expires_in;
    private long iat;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("access_token", access_token)
                .append("expires_in", expires_in)
                .append("iat", iat)
                .append("token_type", token_type)
                .toString();
    }
}
