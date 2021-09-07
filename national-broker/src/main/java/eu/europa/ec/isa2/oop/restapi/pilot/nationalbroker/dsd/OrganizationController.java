package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao.AuthRoleDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.DSDDataUpdateDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.OrganizationDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDDataUpdateRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters.OrganizationFilterRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters.OrganizationUpdateFilterRO;
import eu.europa.ec.isa2.oop.restapi.utils.DaoQueryUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dsd/v1")
@Tag(name = "User services", description = "National broker user services API")
@SecurityRequirements({
        @SecurityRequirement(name = "NationalBroker_Http_BearerTokenAuthorization")
})
public class OrganizationController implements OpenApiSecuritySchemes {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationController.class);
    NationalBrokerProperties nationalBrokerProperties;
    OrganizationDao organizationDao;
    DSDDataUpdateDao DSDDataUpdateDao;
    AuthRoleDao authRoleDao;
    JwsService jwsService;

    @Autowired
    public OrganizationController(NationalBrokerProperties nationalBrokerProperties, OrganizationDao organizationDao,
                                  AuthRoleDao authRoleDao,
                                  DSDDataUpdateDao DSDDataUpdateDao,
                                  JwsService jwsService) {
        this.nationalBrokerProperties = nationalBrokerProperties;
        this.organizationDao = organizationDao;
        this.authRoleDao = authRoleDao;
        this.jwsService = jwsService;
        this.DSDDataUpdateDao = DSDDataUpdateDao;
    }

    @Operation(summary = "Returns list of authorized organizations", description = "Example of a collection resource (4.1.3.2. Collection). " +
            "A collection resource is a server-managed list of resources.Returns list of authorized organizations",
            tags = { "authorized","organizations" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrganizationRO.class)))) })
    @GetMapping(produces = "application/json; charset=UTF-8")
    @RequestMapping(method = RequestMethod.GET, path = "users/{user-id}/organizations")
    //@PreAuthorize("hasRole('ROLE_UPDATE_DSD')")
    public ServiceResult<OrganizationRO> listUserAuthorizedOrganizations( @PathVariable("user-id") String string,
                                                                          @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                          @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                                          @RequestParam(value = "sort", required = false) String sort,
                                                                          @RequestParam(value = "q",required = false) String query,
                                                                          HttpServletResponse response) {



        OrganizationFilterRO filterRO= DaoQueryUtils.generateFilterFromJson(query, OrganizationFilterRO.class);
        List<String> sortOrder =DaoQueryUtils.getSortOrderList(sort);
        ServiceResult<OrganizationRO> serviceResult = organizationDao.getAllOrganizations(offset,limit, filterRO, sortOrder );
        jwsService.signJsonResponse(serviceResult, response);

        return serviceResult;
    }

    @Operation(summary = "Returns list of organization update requests!", description = "Example of A collection resource (4.1.3.2. Collection). " +
            "A collection resource is a server-managed list of resources.Returns list of authorized organizations",
            tags = { "authorized","organizations" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DSDDataUpdateRO.class)))) })
    @GetMapping(produces = "application/json; charset=UTF-8")
    @RequestMapping(method = RequestMethod.GET, path = "users/{user-id}/organization/updates")
    //@PreAuthorize("hasRole('ROLE_UPDATE_DSD')")
    public ServiceResult<DSDDataUpdateRO> listUserAuthorizedOrganizationsUpdates(@PathVariable("user-id") String string,
                                                                                 @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                                 @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                                                 @RequestParam(value = "sort", required = false) String sort,
                                                                                 @RequestParam(value = "q",required = false) String query,
                                                                                 HttpServletResponse response) {
        LOG.info("Inside listUserAuthorizedOrganizationsUpdates for DSD.");

        OrganizationUpdateFilterRO filterRO= DaoQueryUtils.generateFilterFromJson(query, OrganizationUpdateFilterRO.class);
        List<String> sortOrder = DaoQueryUtils.getSortOrderList(sort);

        ServiceResult<DSDDataUpdateRO> serviceResult = DSDDataUpdateDao.getAllUpdates(offset,limit, filterRO, sortOrder);
            jwsService.signJsonResponse(serviceResult, response);
        return serviceResult;
    }
}
