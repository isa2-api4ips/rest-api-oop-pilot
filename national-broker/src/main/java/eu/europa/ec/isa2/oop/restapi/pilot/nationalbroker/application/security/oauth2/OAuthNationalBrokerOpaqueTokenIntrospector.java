package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.*;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OAuthNationalBrokerOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private static final Logger LOG = LoggerFactory.getLogger(OAuthNationalBrokerOpaqueTokenIntrospector.class);

    @Autowired
    private CacheManager cacheManager;


    /**
     * Introspect and verify the given token, returning its attributes.
     * <p>
     * Returning a {@link Map} is indicative that the token is valid.
     *
     * @param token the token to introspect
     * @return the token's attributes
     */
    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        LOG.info("At OAuthNationalBrokerOpaqueTokenIntrospector, Bearer token provided:[{}]", token);

        assert cacheManager != null;
        //Retrieve Access token from cache
        Cache nationalBrokerAccessTokenCache = cacheManager.getCache("nationalBrokerAccessTokenCache");
        if (nationalBrokerAccessTokenCache == null) {
            LOG.error("Internal error. Unable to access NationalBroker AccessToken Cache");
            throw new OAuth2IntrospectionException("Internal error. Unable to access NationalBroker AccessToken Cache");
        }
        OAuthAccessToken_NationalBroker authAccessTokenNationalBroker = nationalBrokerAccessTokenCache.get(token, OAuthAccessToken_NationalBroker.class);
        if (authAccessTokenNationalBroker == null) {
            LOG.error("Invalid or expired access token supplied:[{}] ", token);
            throw new BadOpaqueTokenException("Invalid or expired access token supplied: " + token);
        }
        LOG.info("At OAuthNationalBrokerOpaqueTokenIntrospector, Found OAuthAccessToken_NationalBroker from cache: [{}]", authAccessTokenNationalBroker);
        Date currentDateTime = new Date();

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Object> claims = new HashMap<>();
        // store token to principal to retrieve JWT
        claims.put("national-broker-token", token);
        claims.put("identity-token", authAccessTokenNationalBroker.getOriginalIDToken());
        if (StringUtils.isNotBlank(authAccessTokenNationalBroker.getUserName())) {
            claims.put(OAuth2IntrospectionClaimNames.USERNAME, authAccessTokenNationalBroker.getUserName());
        }
        if (StringUtils.isNotBlank(authAccessTokenNationalBroker.getSubject_User_IDP_ID())) {
            claims.put(OAuth2IntrospectionClaimNames.SUBJECT, authAccessTokenNationalBroker.getSubject_User_IDP_ID());
        }
        if (authAccessTokenNationalBroker.getAudience() != null) {
            claims.put(OAuth2IntrospectionClaimNames.AUDIENCE, Collections.unmodifiableList(authAccessTokenNationalBroker.getAudience()));
        }

        if (authAccessTokenNationalBroker.getExpiresAt() != null) {
            Instant exp = authAccessTokenNationalBroker.getExpiresAt();
            claims.put(OAuth2IntrospectionClaimNames.EXPIRES_AT, exp);
            if (currentDateTime.toInstant().isAfter(authAccessTokenNationalBroker.getExpiresAt())) {
                LOG.error("Access token is expired.");
                throw new BadOpaqueTokenException("Access token is expired.");
            }
        }
        if (authAccessTokenNationalBroker.getIssuedAt() != null) {
            Instant iat = authAccessTokenNationalBroker.getIssuedAt();
            claims.put(OAuth2IntrospectionClaimNames.ISSUED_AT, iat);
        }
        if (authAccessTokenNationalBroker.getIssuer() != null) {
            try {
                claims.put(OAuth2IntrospectionClaimNames.ISSUER, new URL(authAccessTokenNationalBroker.getIssuer()));
            } catch (MalformedURLException e) {
                LOG.error("Invalid issuer value in Bearer Token:", e);
                throw new BadOpaqueTokenException("Invalid " + OAuth2IntrospectionClaimNames.ISSUER + " value: " + authAccessTokenNationalBroker.getIssuer());
            }
        }
        if (CollectionUtils.isNotEmpty(authAccessTokenNationalBroker.getScopes())) {
            claims.put(OAuth2IntrospectionClaimNames.SCOPE, authAccessTokenNationalBroker.getScopes());
            for (String scope : authAccessTokenNationalBroker.getScopes()) {
                authorities.add(new SimpleGrantedAuthority(/*"SCOPE_"+*/ scope));
            }
        }
        LOG.info("Grant [{}] to token: [{}] ",
                String.join(",",authorities.stream().map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toList())),
                token);
        return new OAuth2IntrospectionAuthenticatedPrincipal(claims, authorities);
    }
}
