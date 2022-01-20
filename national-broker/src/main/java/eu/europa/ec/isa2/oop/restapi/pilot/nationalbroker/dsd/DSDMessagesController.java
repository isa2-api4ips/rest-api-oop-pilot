package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao.AuthRoleDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.DSDRequestLoggerDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDMessageRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDRequestLogRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters.DSDRequestLogFilterRO;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dsd/v1")
@Tag(name = "DSD messages", description = "Controller for inspecting DSD messages.")
@SecurityRequirements({
        @SecurityRequirement(name = "NationalBroker_Http_BearerTokenAuthorization")
})
public class DSDMessagesController implements OpenApiSecuritySchemes {

    private static final Logger LOG = LoggerFactory.getLogger(DSDMessagesController.class);
    NationalBrokerProperties nationalBrokerProperties;
    DSDRequestLoggerDao dsdRequestLoggerDao;
    AuthRoleDao authRoleDao;
    JwsService jwsService;

    @Autowired
    public DSDMessagesController(NationalBrokerProperties nationalBrokerProperties,
                                 AuthRoleDao authRoleDao,
                                 DSDRequestLoggerDao dsdRequestLoggerDao,
                                 JwsService jwsService) {
        this.nationalBrokerProperties = nationalBrokerProperties;
        this.authRoleDao = authRoleDao;
        this.jwsService = jwsService;
        this.dsdRequestLoggerDao = dsdRequestLoggerDao;
    }

    @Operation(summary = "Returns list of organization update requests!", description = "Example of A collection resource (4.1.3.2. Collection). " +
            "A collection resource is a server-managed list of resources.Returns list of authorized dsd requests",
            tags = { "authorized","requests" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DSDRequestLogRO.class)))) })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_PROBLEM_JSON_VALUE,})
    @RequestMapping(method = RequestMethod.GET, path = "users/{user-id}/dsd-request/messages")
    //@PreAuthorize("hasRole('ROLE_UPDATE_DSD')")
    public ServiceResult<DSDRequestLogRO> listUserAuthorizedDSDRequestLogs( @PathVariable("user-id") String userId,
                                                                                       @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                                       @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                                                       @RequestParam(value = "sort", required = false) String sort,
                                                                                       @RequestParam(value = "q",required = false) String query,
                                                                                       HttpServletResponse response) {


        LOG.info("Inside listUserAuthorizedDSDRequestLogs for DSD.");
        LOG.info("Sort [{}], query [{}] ", sort, query);
        DSDRequestLogFilterRO filterRO = DaoQueryUtils.generateFilterFromJson(query, DSDRequestLogFilterRO.class);
        List<String> sortOrder = DaoQueryUtils.getSortOrderList(sort);

        ServiceResult<DSDRequestLogRO> serviceResult = dsdRequestLoggerDao.getAllLogs(offset,limit, filterRO, sortOrder);
        jwsService.signJsonResponse(serviceResult, response);
        return serviceResult;
    }


    @Operation(summary = "Returns DSD Message requests!", description = "Message request",
            tags = { "authorized","requests" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DSDRequestLogRO.class)))) })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_PROBLEM_JSON_VALUE} )
    @RequestMapping(method = RequestMethod.GET, path = "users/{user-id}/dsd-request/message/{id}")
    //@PreAuthorize("hasRole('ROLE_UPDATE_DSD')")
    public DSDRequestLogRO retrieveDSDRequestMessage(@PathVariable("user-id") String string,
                                                 @PathVariable(value = "id") Long id,
                                                 HttpServletResponse response) throws IOException {


        LOG.info("Inside retrieveDSDRequestMessage for DSD.");
        DSDRequestLogRO result = dsdRequestLoggerDao.getRequestLogById(id, true);
        jwsService.signJsonResponse(result, response);
        return result;
    }

    @Operation(summary = "Returns DSD Message  DSS report for the signature!", description = "Message Signature report",
            tags = { "authorized","requests" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig")
                    ) })
    @GetMapping(produces = MediaType.TEXT_XML_VALUE)
    @RequestMapping(method = RequestMethod.GET, path = "users/{user-id}/dsd-request/message/{id}/download/{messageType}")
    //@PreAuthorize("hasRole('ROLE_UPDATE_DSD')")
    public String retrieveDSDRequestMessage(@PathVariable("user-id") String userId,
                                                                          @PathVariable(value = "id") Long id,
                                                                          @PathVariable(value = "messageType") String messageType,
                                                                          HttpServletResponse response
    ) throws IOException {


        LOG.info("Inside retrieveDSDRequestMessage for DSD.");
        DSDRequestLogRO result = dsdRequestLoggerDao.getRequestLogById(id, true);
        if (StringUtils.equalsIgnoreCase("requestSignatureReport",messageType)){
            response.setContentType(MediaType.TEXT_XML_VALUE);

            return jwsService.getValidationSignatureXMLReportFromHeaders(result.getRequestMessage().getHeaders());

        } else if (StringUtils.equalsIgnoreCase("responseSignatureReport",messageType)){
            response.setContentType(MediaType.TEXT_XML_VALUE);
            return jwsService.getValidationSignatureXMLReportFromHeaders(result.getResponseMessage().getHeaders());
        } else if (StringUtils.equalsIgnoreCase("request",messageType)){
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            return messageToString(result.getRequestMessage());
        } else {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            return messageToString(result.getResponseMessage());
        }
    }

    public String messageToString(DSDMessageRO messageRO){
        StringWriter sw = new StringWriter();
        // sort by keys
        Map<String, String> treeMap = new TreeMap<>(messageRO.getHeaders());
        treeMap.forEach((key, value)-> {
            sw.append(key);
            sw.append(": ");
            sw.append(value);
            sw.append("\r\n");
        });
        sw.append("\r\n");
        sw.write(messageRO.getBody());
        sw.flush();
        return sw.toString();

    }
}
