package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.stereotype.Component;

@Component
public class OriginalSenderTokenRetriever {
    private static final Logger LOG = LoggerFactory.getLogger(OriginalSenderTokenRetriever.class);

    public String getRequesterIdentityToken() {

        if (SecurityContextHolder.getContext() == null) {
            LOG.error("No security context!");
            return null;
        }

        BearerTokenAuthentication authentication = (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOG.error("No security authentication!");
            return null;
        }
        LOG.info("Got authentication: " + authentication);
        OAuth2IntrospectionAuthenticatedPrincipal principal = (OAuth2IntrospectionAuthenticatedPrincipal) authentication.getPrincipal();
        return principal.getAttribute("identity-token");
    }
}
