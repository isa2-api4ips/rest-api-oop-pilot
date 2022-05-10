package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.OriginalSenderTokenRetriever;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao.AuthRoleDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.OrganizationDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.DSDOrganizationMessagingService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dsd-lcm/v1")
@Tag(name = "DSD LCM Broker services", description = "DSD LCM Broker services API.")

@SecurityRequirements({
        @SecurityRequirement(name = "NationalBroker_Http_BearerTokenAuthorization")
})

public class OrganizationLCMController {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationLCMController.class);


    NationalBrokerProperties nationalBrokerProperties;
    OrganizationDao organizationDao;
    AuthRoleDao authRoleDao;
    DSDOrganizationMessagingService dsdOrganizationMessagingService;
    JwsService jwsService;
    OriginalSenderTokenRetriever originalSenderTokenRetriever;


    @Autowired
    public OrganizationLCMController(NationalBrokerProperties nationalBrokerProperties, OrganizationDao organizationDao,
                                     AuthRoleDao authRoleDao,
                                     DSDOrganizationMessagingService dsdOrganizationMessagingService,
                                     JwsService jwsService,
                                     OriginalSenderTokenRetriever originalSenderTokenRetriever) {
        this.nationalBrokerProperties = nationalBrokerProperties;
        this.organizationDao = organizationDao;
        this.authRoleDao = authRoleDao;
        this.dsdOrganizationMessagingService = dsdOrganizationMessagingService;

        this.jwsService = jwsService;
        this.originalSenderTokenRetriever = originalSenderTokenRetriever;
    }


    @Operation(summary = "Update organization", description = "update organization by id", tags = { "authorized","organization","details" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(schema = @Schema(implementation = OrganizationRO.class))) })
    @PutMapping("users/{user-id}/organization/{organization-id}")
    public OrganizationRO updateOrganization(
            @PathVariable("user-id") String userId,
            @PathVariable("organization-id") String organizationId,
            @RequestBody OrganizationRO updateEntity, HttpServletResponse response) {
        // implementation
        String messageIdentifier = UUID.randomUUID().toString();
        // set status to update
        OrganizationRO result = organizationDao.updateOrganizations(updateEntity, messageIdentifier);
        jwsService.signJsonResponse(result, response);
        // call DSD mock
        String token = originalSenderTokenRetriever.getRequesterIdentityToken();
        token = validatedAndGenerateOriginalSenderToken(token, userId);
        dsdOrganizationMessagingService.updateOrganization(result, messageIdentifier,userId,userId, token);
        return result;
    }
    /**
     * Method validates if token exists. If not it generates a simple JWT token - signed user:id
     * @param token
     * @param userId
     * @return
     */
    public String validatedAndGenerateOriginalSenderToken(String token, String userId){
        return  StringUtils.isBlank(token)||StringUtils.equalsIgnoreCase(token, userId) ?jwsService.createOriginalSenderToken(userId):token;
    }
}
