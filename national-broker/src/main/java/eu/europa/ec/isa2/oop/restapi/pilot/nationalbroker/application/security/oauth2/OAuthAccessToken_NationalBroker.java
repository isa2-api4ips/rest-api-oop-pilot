package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.AuthRoleEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class OAuthAccessToken_NationalBroker extends OAuth2AccessToken implements Serializable {
    private int expiresInSeconds = 86400;// = 24hour by default. Can be modifier inside the national-broker.properties
    private final String issuer = "http://national-broker-server:8080/national-broker/oauth/token";
    private String userName;
    private String subject_User_IDP_ID;
    private List<String> audience;
    private String originalIDToken;

    /**
     * Constructs an {@code OAuth2AccessToken} using the provided parameters.
     *
     * @param tokenType  the token type
     * @param tokenValue the token value
     * @param issuedAt   the time at which the token was issued
     * @param expiresAt  the expiration time on or after which the token MUST NOT be
     */
    public OAuthAccessToken_NationalBroker(TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt) {
        super(tokenType, tokenValue, issuedAt, expiresAt);
    }

    /**
     * Constructs an {@code OAuth2AccessToken} using the provided parameters.
     *
     * @param tokenType  the token type
     * @param tokenValue the token value
     * @param issuedAt   the time at which the token was issued
     * @param expiresAt  the expiration time on or after which the token MUST NOT be
     *                   accepted
     * @param scopes     the scope(s) associated to the token
     */
    public OAuthAccessToken_NationalBroker(TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
        super(tokenType, tokenValue, issuedAt, expiresAt, scopes);
    }

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubject_User_IDP_ID() {
        return subject_User_IDP_ID;
    }

    public void setSubject_User_IDP_ID(String subject_User_IDP_ID) {
        this.subject_User_IDP_ID = subject_User_IDP_ID;
    }

    public List<String> getAudience() {
        return audience;
    }

    public String getOriginalIDToken() {
        return originalIDToken;
    }

    public void setOriginalIDToken(String originalIDToken) {
        this.originalIDToken = originalIDToken;
    }

    public void setAudience(List<String> audience) {
        this.audience = audience;
    }

    public void addAudience(String audience) {
        if (this.audience == null) {
            this.audience = new ArrayList<>();
        }
        this.audience.add(audience);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("tokenType", this.getTokenType().getValue())
                .append("tokenValue", this.getTokenValue())
                .append("issuedAt", this.getIssuedAt())
                .append("expiresAt", this.getExpiresAt())
                .append("userName", userName)
                .append("subject_User_IDP_ID", subject_User_IDP_ID)
                .append("audience", audience)
                .append("userRoles", this.getScopes())
                .append("issuer", issuer)
                .toString();
    }

    public static class Builder {
        private OAuthAccessToken_NationalBroker accessTokenNationalBroker;

        private Builder() {
            //Do Nothing
        }

        public Builder(OAuthAccessToken_NationalBroker accessTokenNationalBroker) {
            this.accessTokenNationalBroker = accessTokenNationalBroker;
        }

        public Builder(String assertion, String tokenValue, String userName, int secondsTillTokenExpiry, Set<String> scopes) {
            Calendar cal = Calendar.getInstance();
            Instant issuedAt = cal.getTime().toInstant();
            cal.add(Calendar.SECOND, secondsTillTokenExpiry);
            Instant expiresAt = cal.getTime().toInstant();

            accessTokenNationalBroker = new OAuthAccessToken_NationalBroker(TokenType.BEARER, tokenValue, issuedAt, expiresAt, scopes);
            accessTokenNationalBroker.setUserName(userName);
            accessTokenNationalBroker.expiresInSeconds = secondsTillTokenExpiry;
            accessTokenNationalBroker.setOriginalIDToken(assertion);
        }

        public static Builder getInstance(String assertion, String tokenValue, String userName, int secondsTillTokenExpiry, String role){
            Set<String> scope = new HashSet<>();
            scope.add(role);
            Builder builder = new Builder(assertion, tokenValue, userName, secondsTillTokenExpiry, scope);
            return builder;
        }

        public static Builder getInstance(String assertion, String tokenValue, String userName, int secondsTillTokenExpiry, Set<AuthRoleEntity> userAuthRoles){
            Set<String> scopes = new HashSet<>();
            if(CollectionUtils.isNotEmpty(userAuthRoles)){
                for (AuthRoleEntity userAuthRole : userAuthRoles) {
                    scopes.add(userAuthRole.getRoleName().name());
                }
            }
            Builder builder = new Builder(assertion, tokenValue, userName, secondsTillTokenExpiry, scopes);
            return builder;
        }


        public Builder withUser_IDP_ID(String user_idp_id) {
            accessTokenNationalBroker.setSubject_User_IDP_ID(user_idp_id);
            return this;
        }

        public Builder withAudience(List<String> audienceList) {
            if (CollectionUtils.isNotEmpty(audienceList)) {
                if (accessTokenNationalBroker.getAudience() == null) {
                    accessTokenNationalBroker.setAudience(new ArrayList<>());
                }
                accessTokenNationalBroker.getAudience().addAll(audienceList);
            }
            return this;
        }

        public Builder withAudience(String audience) {
            accessTokenNationalBroker.addAudience(audience);
            return this;
        }

        public OAuthAccessToken_NationalBroker getAccessTokenNationalBroker() {
            return accessTokenNationalBroker;
        }

        public ResponseEntity<String> buildJSONServletResponse() throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule("OAuthNationalBrokerAccessTokenSerializer", new Version(1, 0, 0, null, null, null));
            module.addSerializer(OAuthAccessToken_NationalBroker.class, new OAuthAccessTokenCustomJSONSerializer());
            objectMapper.registerModule(module);
            String responseJSONString = objectMapper.writeValueAsString(accessTokenNationalBroker);
            return ResponseEntity.ok()
                    .body(responseJSONString);
        }
    }
}
