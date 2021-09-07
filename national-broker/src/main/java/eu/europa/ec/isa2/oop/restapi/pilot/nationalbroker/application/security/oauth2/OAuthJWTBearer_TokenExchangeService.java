package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao.UserDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.UserEntity;
import eu.europa.ec.isa2.restapi.jws.TruststoreFileConnection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OAuthJWTBearer_TokenExchangeService {
    private static final Logger LOG = LoggerFactory.getLogger(OAuthJWTBearer_TokenExchangeService.class);

    private CacheManager cacheManager;

    private NationalBrokerProperties nationalBrokerProperties;

    private UserDao userDao;
    TruststoreFileConnection truststoreFileConnection;
    @Autowired
    public OAuthJWTBearer_TokenExchangeService(CacheManager cacheManager, NationalBrokerProperties nationalBrokerProperties, UserDao userDao) {
        this.cacheManager = cacheManager;
        this.nationalBrokerProperties = nationalBrokerProperties;
        this.userDao = userDao;

        truststoreFileConnection = new TruststoreFileConnection( this.nationalBrokerProperties);
    }

    private JWSVerifier identifyJWSVerifier(JWK auth0Key) throws JOSEException {
        KeyType keyType = auth0Key.getKeyType();
        if (StringUtils.equalsIgnoreCase(keyType.getValue(), KeyType.RSA.getValue())) {
            return new RSASSAVerifier(auth0Key.toRSAKey());
        }
        if (StringUtils.equalsIgnoreCase(keyType.getValue(), KeyType.EC.getValue())) {
            return new ECDSAVerifier(auth0Key.toECKey());
        }
        throw new RuntimeException("Unsupported signature algorithm.");
    }

    /**
     * Validate an assertion received is a valid ID Token in a Signed JWT format.
     * If all validation succeeds then return the extracted JWT claims set.
     *
     * @param assertion - an ID Token as a Signed JWT
     * @return - the set of Claims from the ID Token
     * @throws OAuthException
     */
    public JWTClaimsSet validateAssertionAsIDToken(String assertion) throws OAuthException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {

        SignedJWT signedAssertionJWT;
        JWTClaimsSet assertionJWTClaimsSet;

        try {
            signedAssertionJWT = SignedJWT.parse(assertion);
            assertionJWTClaimsSet = signedAssertionJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Assertion to Token exchange endpoint of National Broker should be a signed JWT with required assertions as JSON.");
        }

        if (!validateIDPSignature(signedAssertionJWT)) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Signature verification of assertion failed.");
        }

        validateIssuer(assertionJWTClaimsSet);
        validateSubject(assertionJWTClaimsSet);
        //validateAudience(assertionJWTClaimsSet); Audience should be validated. But currently Auth0 does not support it.
        validateExpirationTime(assertionJWTClaimsSet);
        validateNotBeforeTime(assertionJWTClaimsSet);

        return assertionJWTClaimsSet;
    }

    private void validateNotBeforeTime(JWTClaimsSet assertionJWTClaimsSet) throws OAuthException {
        Date notBeforeTime = assertionJWTClaimsSet.getNotBeforeTime();
        Date currentDateTime = new Date();
        if (notBeforeTime != null && currentDateTime.before(notBeforeTime)) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Assertion specifies a Not Before Time later than current time. Assertion should not be used yet.");
        }
    }

    private void validateExpirationTime(JWTClaimsSet assertionJWTClaimsSet) throws OAuthException {
        Date expirationTime = assertionJWTClaimsSet.getExpirationTime();
        if (expirationTime == null) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Expiration time of assertion is not specified.");
        }
        Date currentDateTime = new Date();
        if (currentDateTime.after(expirationTime)) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Assertion has expired.");
        }
    }

    private void validateAudience(JWTClaimsSet assertionJWTClaimsSet) throws OAuthException {
        List<String> audience = assertionJWTClaimsSet.getAudience();
        if (!audience.contains(nationalBrokerProperties.getOauth_IdpAccessToken_ExpectedAudience())) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "National Broker token endpoint is not in intended audiences of assertion.");
        }
    }

    private void validateSubject(JWTClaimsSet assertionJWTClaimsSet) throws OAuthException {
        String subject = assertionJWTClaimsSet.getSubject();
        if (StringUtils.isEmpty(subject)) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Subject not present in assertion.");
        }
    }

    private void validateIssuer(JWTClaimsSet assertionJWTClaimsSet) throws OAuthException {
        String issuer = assertionJWTClaimsSet.getIssuer();
        if (StringUtils.isEmpty(issuer)) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Issuer not present in assertion.");
        }
        if (!StringUtils.equalsIgnoreCase(issuer, nationalBrokerProperties.getOauth_uiclient_idp_issuer())) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Issuer of Assertion is not Auth0.");
        }
    }

    private boolean validateIDPSignature(SignedJWT signedAssertionJWT) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, OAuthException, UnrecoverableKeyException {
        JWSHeader assertionHeader = signedAssertionJWT.getHeader();
        String keyIDFromAssertionHeader = assertionHeader.getKeyID();
        LOG.debug("KeyID From Assertion Header:[{}]", keyIDFromAssertionHeader);

        //loadTrustStore
        LOG.debug("Loading Truststore type:[{}], from path:[{}]", nationalBrokerProperties.getTruststoreType(), nationalBrokerProperties.getTruststoreLocation());

        JWKSet jwkSetFromTrustStore = null;
        LOG.debug("Looking for certificate with IDP alias:[{}]", nationalBrokerProperties.getOauth_uiclient_idp_certificate_alias());
        jwkSetFromTrustStore = JWKSet.load(truststoreFileConnection.getKeyStore(), null); //Important: it is not checked if certificates are still valid!
        JWK idpKey = jwkSetFromTrustStore.getKeyByKeyId(nationalBrokerProperties.getOauth_uiclient_idp_certificate_alias());
        if (idpKey == null) {
            throw new UnrecoverableKeyException("Public certificate of IDP with alias: " + nationalBrokerProperties.getOauth_uiclient_idp_certificate_alias() + " cannot be retrieved from truststore of National Broker. Please contact admin.");
            //throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "KeyID of signature used to sign the JWK is unknown.");
        }

        JWSVerifier jwsVerifier;
        try {
            jwsVerifier = identifyJWSVerifier(idpKey);
        } catch (JOSEException e) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Signature Key extraction failed.", e);
        }

        try {
            return signedAssertionJWT.verify(jwsVerifier);
        } catch (JOSEException e) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT, "Signature verification of assertion failed.", e);
        }
    }

    public OAuthAccessToken_NationalBroker generateAccessTokenToAuthorizeNationalBrokerAccess(String assertion, JWTClaimsSet jwtClaimsSet) throws OAuthException {
        String subject = jwtClaimsSet.getSubject();
        LOG.debug("Subject requesting token exchange:" + subject);

        //Find user and role from database (This is a Pilot for production do not use SEMI random UUID!!)
        String accessTokenUUID = UUID.randomUUID().toString();

        UserEntity userEntity = userDao.findUserByIDP_ID(subject);
        if (userEntity == null) {
            throw new OAuthException(OAuthErrorResponse.OAuthErrorCode.INVALID_CLIENT, "User identifier:" + subject + " is not known within National Broker.");
        }
        LOG.debug("UserEntity retrieved:[{}]]", userEntity);

        OAuthAccessToken_NationalBroker.Builder oAuthAccessTokenBuilder = OAuthAccessToken_NationalBroker.Builder.getInstance(assertion, accessTokenUUID, userEntity.getUsername(), nationalBrokerProperties.getOauthAccessTokenExpiryPeriodInSeconds(), userEntity.getUserAuthRoles())
                .withUser_IDP_ID(userEntity.getUser_IDP_ID());

        OAuthAccessToken_NationalBroker accessTokenNationalBroker = oAuthAccessTokenBuilder.getAccessTokenNationalBroker();

        Cache nationalBrokerAccessTokenCache = getAccessTokenCache();
        nationalBrokerAccessTokenCache.put(accessTokenNationalBroker.getTokenValue(), accessTokenNationalBroker);

        return accessTokenNationalBroker;
    }

    public Cache getAccessTokenCache() {
        return cacheManager.getCache("nationalBrokerAccessTokenCache");
    }

    @PostConstruct
    public void populateDummyAccessToken() {
        LOG.info("Inside populateDummyAccessToken at PostConstruct.");
        Cache accessTokenCache = getAccessTokenCache();
        assert accessTokenCache != null;

        String accessTokenUUID = "DUMMY_ACCESS_TOKEN_1";
        OAuthAccessToken_NationalBroker.Builder oAuthAccessTokenBuilder = OAuthAccessToken_NationalBroker.Builder.getInstance(null, accessTokenUUID, "USER_DSD", nationalBrokerProperties.getOauthAccessTokenExpiryPeriodInSeconds(),"ROLE_DSD")
                .withUser_IDP_ID("auth0|60aaec1b802b8800686a5c94");
        accessTokenCache.put(accessTokenUUID, oAuthAccessTokenBuilder.getAccessTokenNationalBroker());
        LOG.info("Put DUMMY_ACCESS_TOKEN_1 in Cache:" + oAuthAccessTokenBuilder.getAccessTokenNationalBroker());
    }


}

