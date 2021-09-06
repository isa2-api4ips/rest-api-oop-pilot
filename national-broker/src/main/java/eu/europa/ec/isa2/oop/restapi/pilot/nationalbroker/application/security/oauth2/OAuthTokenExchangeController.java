package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2;

import com.nimbusds.jwt.JWTClaimsSet;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2.schema.OAuthAccessTokenRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2.schema.OAuthErrorRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2.schema.OAuthTokenExchange_JWTBearerRequest;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;

import static eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2.OAuthConstants.GRANT_TYPE_JWT_BEARER;

@RestController
public class OAuthTokenExchangeController {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthTokenExchangeController.class);

    @Autowired
    OAuthJWTBearer_TokenExchangeService oAuthJWTBearerTokenExchangeService;

    @Hidden
    @GetMapping("/test")
    public ResponseEntity<String> test() {

        return ResponseEntity.status(HttpStatus.OK)
                .body("Successful invocation to test endpoint of National Broker Token exchange service");
    }

    /**
     * The token endpoint of NationalBroker accepts JWT Bearer tokens of type ACCESS_TOKEN issued by the IDP, validates the input and
     * provides another ACCESS_TOKEN as authorization for use in National Broker.
     * The ID of the subject as identified within the IDP should also be known in National Broker database.
     *
     * @param httpServletRequest - The request body should have 2 parameters:  <br/>
     *                           1. <b>grant_type</b> - should be "urn:ietf:params:oauth:grant-type:jwt-bearer" <br/>
     *                           2. <b>assertion</b> - an access token in signed JWT format
     * @return aJSON containing an opaque access token that authorizes the holder to access the relevant APIs in the National Broker.
     */
    @Operation(summary = "Exchange IDP Access Token for National Broker Access Token"
            , description = "Exchange an IDP Access Token in signed JWT form for an National Broker Access Token. Follows IETF - RFC7523.   \n" +
            "The request body for token exchange must contain 2 parameters:   \n" +
            "**grant_type** - The OAuth2 Grant Type of the assertion. Currently only JWT-Bearer grant is supported.   \n" +
            "**assertion** - An Access Token issued by an Identity provider in Signed JWT form.   \n"
            , externalDocs = @ExternalDocumentation(url = "https://datatracker.ietf.org/doc/html/rfc7523")
            , tags = {"OAuth2"}
            , requestBody = @RequestBody(
            required = true
            , content = {@Content(
            mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            , schema = @Schema(implementation = OAuthTokenExchange_JWTBearerRequest.class))})
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful operation to get access token for National Broker"
                    , content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OAuthAccessTokenRO.class)))
            , @ApiResponse(responseCode = "400"
            , description = "- If the grant_type is incorrect   \n" +
            "- If no access token is provided in the assertion or if the access token provided is invalid or expired"
            , content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OAuthErrorRO.class))),
            @ApiResponse(responseCode = "500"
                    , description = "If an unexpected system error is encountered."
                    , content = @Content(mediaType = MediaType.ALL_VALUE))})
    @PostMapping(name = "/oauth/token", value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tokenEndpoint(HttpServletRequest httpServletRequest) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {

        String reqGrantType = StringUtils.trim(httpServletRequest.getParameter("grant_type"));
        if (StringUtils.isNotBlank(reqGrantType)) {
            reqGrantType = URLDecoder.decode(reqGrantType, StandardCharsets.UTF_8.toString());
        }
        String assertion = StringUtils.trim(httpServletRequest.getParameter("assertion"));
        if (StringUtils.isNotBlank(assertion)) {
            assertion = URLDecoder.decode(httpServletRequest.getParameter("assertion"), StandardCharsets.UTF_8.toString());
        }
        LOG.debug("reqGrantType:[{}]", reqGrantType);
        LOG.debug("assertion:[{}]", assertion);

        if (!StringUtils.equalsIgnoreCase(GRANT_TYPE_JWT_BEARER, reqGrantType)) {
            LOG.error("Invalid grant type: [{}] received for token exchange request. Expected: [{}]", reqGrantType, GRANT_TYPE_JWT_BEARER);
            return new OAuthErrorResponse.Builder(HttpStatus.BAD_REQUEST, OAuthErrorResponse.OAuthErrorCode.INVALID_GRANT)
                    .withErrorDescription("Token exchange endpoint of National Broker currently supports only grant type:" + GRANT_TYPE_JWT_BEARER)
                    .buildJSONServletResponse();
        }

        JWTClaimsSet validatedJWTClaimsSet;
        OAuthAccessToken_NationalBroker authAccessTokenNationalBroker = null;
        try {
            validatedJWTClaimsSet = oAuthJWTBearerTokenExchangeService.validateAssertionAsIDToken(assertion);
            authAccessTokenNationalBroker = oAuthJWTBearerTokenExchangeService.generateAccessTokenToAuthorizeNationalBrokerAccess(assertion, validatedJWTClaimsSet);
        } catch (OAuthException e) {
            LOG.error("Encountered OAuth validation exception:", e);
            return new OAuthErrorResponse.Builder(HttpStatus.BAD_REQUEST, e.getErrorCode())
                    .withErrorDescription(e.getErrorDescription())
                    .buildJSONServletResponse();
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
            LOG.error("Unexpected system error encountered in token exchange:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server error occurred:" + e.getMessage());
        }

        return (new OAuthAccessToken_NationalBroker.Builder(authAccessTokenNationalBroker)).buildJSONServletResponse();
    }

    @Hidden
    @GetMapping("/oauth/callback")
    public String oauthCallback(HttpServletRequest request,
                                @RequestParam(name = "tokenId", defaultValue = "NoValue") String tokenId) {
        String queryString = request.getQueryString();
        LOG.info("Request URL:[{}], query string:[{}], tokenId: [{}]", request.getRequestURL(), queryString, tokenId);

        Cache accessTokenCache = oAuthJWTBearerTokenExchangeService.getAccessTokenCache();
        Cache.ValueWrapper cachedObject = accessTokenCache.get(tokenId);
        if (cachedObject != null) {
            OAuthAccessToken_NationalBroker authAccessTokenNationalBroker = (OAuthAccessToken_NationalBroker) cachedObject.get();
            LOG.info("AccessToken retrieved:[{}] ", authAccessTokenNationalBroker);
        } else {
            LOG.info("No access token retrieved.");
        }

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        LOG.info("@@username :[{}] ", username);
        OAuth2IntrospectionAuthenticatedPrincipal principal = (OAuth2IntrospectionAuthenticatedPrincipal) authentication.getPrincipal();
        LOG.info("@@principal :[{}]",  principal);
        LOG.info("@@principal.getUserName :[{}]",  principal.getUsername());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        authorities.forEach(
                grantedAuthority -> LOG.info("GrantedAuthority :[{}]",  grantedAuthority.getAuthority())
        );

        return "String from callback";
    }

}
