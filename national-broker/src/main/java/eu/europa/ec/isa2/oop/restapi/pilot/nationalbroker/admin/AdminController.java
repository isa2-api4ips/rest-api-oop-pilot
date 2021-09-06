package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.admin;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.admin.model.UserRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao.AuthRoleDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao.UserDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Out of the scope of the pilot project. Leave the class for possible further development
 */
//@RestController
//@CrossOrigin(origins = "http://localhost:4200")
//@RequestMapping("/admin/v1")
//@Tag(name = "Administration services", description = "Administration for users and national broker properties")
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
/*
    @Autowired
    NationalBrokerProperties nationalBrokerProperties;

    @Autowired
    UserDao userDao;

    @Autowired
    AuthRoleDao authRoleDao;

    @Operation(summary = "Returns list of users", description = "Returns list of users", tags = { "administration","users" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrganizationRO.class)))) })
    @GetMapping(produces = "application/json; charset=UTF-8")
    @RequestMapping(method = RequestMethod.GET, path = "/users")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ServiceResult<UserRO> listUserAuthorizedOrganizations(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                 @RequestParam(value = "orderBy", required = false) String orderBy,
                                                                 @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType) {
        LOG.info("Inside listUserAuthorizedOrganizations for DSD.");

        return userDao.getAllUsers(page,pageSize);
    }
/*
    @Operation(summary = "Returns user details", description = "Returns user details by id ", tags = { "authorized","user","details" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(schema = @Schema(implementation = UserRO.class))) })
    @GetMapping(value = "user/{id}")
    public ResponseEntity<UserRO> getOrganizationDetailsById(@PathVariable("id") Long id) {
        // implementation
        return null;
    }

    @Operation(summary = "Add new user", description = "Add new user", tags = { "authorized","user","details" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(schema = @Schema(implementation = UserRO.class))) })
    @PostMapping(value = "user")
    public ResponseEntity<UserRO> addOrganization(@RequestBody @Validated UserRO foo) {
        // implementation
        return null;
    }

    @Operation(summary = "Delete user", description = "Delete user by id", tags = { "authorized","user","details" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(schema = @Schema(implementation = UserRO.class))) })
    @DeleteMapping("user/{id}")
    public ResponseEntity<UserRO> deleteOrganization(@PathVariable("id") long id) {
        // implementation
        return null;
    }

    @Operation(summary = "Update user", description = "update user by id", tags = { "authorized","user","details" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(schema = @Schema(implementation = OrganizationRO.class))) })
    @PutMapping("user/{id}")
    public ResponseEntity<OrganizationRO> updateOrganization(@PathVariable("id") long id, @RequestBody OrganizationRO foo) {
        // implementation
        return null;
    }
*/
}
