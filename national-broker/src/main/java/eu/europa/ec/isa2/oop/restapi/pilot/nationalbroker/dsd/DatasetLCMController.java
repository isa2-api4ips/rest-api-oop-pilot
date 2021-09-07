package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.JwsService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.OriginalSenderTokenRetriever;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao.AuthRoleDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.DSDDataUpdateDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.DSDDatasetMessagingService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDDataUpdateRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DatasetRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OrganizationRO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dsd-lcm/v1")
@Tag(name = "DSD LCM Dataset Broker services", description = "DSD LCM Dataset Broker services API")
@SecurityRequirements({
        @SecurityRequirement(name = "NationalBroker_Http_BearerTokenAuthorization")
})
public class DatasetLCMController implements OpenApiSecuritySchemes {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetLCMController.class);


    NationalBrokerProperties nationalBrokerProperties;
    AuthRoleDao authRoleDao;
    DSDDatasetMessagingService dsdDatasetMessagingService;
    DSDDataUpdateDao dataUpdateDao;
    OriginalSenderTokenRetriever originalSenderTokenRetriever;
    JwsService jwsService;

    @Autowired
    public DatasetLCMController(DSDDataUpdateDao dataUpdateDao, NationalBrokerProperties nationalBrokerProperties,
                                AuthRoleDao authRoleDao,
                                DSDDatasetMessagingService dsdDatasetMessagingService,
                                JwsService jwsService,
                                OriginalSenderTokenRetriever originalSenderTokenRetriever) {
        this.nationalBrokerProperties = nationalBrokerProperties;
        this.authRoleDao = authRoleDao;
        this.dsdDatasetMessagingService = dsdDatasetMessagingService;
        this.jwsService = jwsService;
        this.dataUpdateDao = dataUpdateDao;
        this.originalSenderTokenRetriever = originalSenderTokenRetriever;
    }


    @Operation(summary = "Returns list of datasets", description = "Example of a collection resource (4.1.3.2. Collection). " +
            "A collection resource is a server-managed list of resources.Returns list of authorized organizations",
            tags = {"dataset"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name = "edel-message-sig", required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DatasetRO.class))))})
    @GetMapping(produces = "application/json; charset=UTF-8")
    @RequestMapping(method = RequestMethod.GET, path = "users/{user-id}/datasets")
    //@PreAuthorize("hasRole('ROLE_UPDATE_DSD')")
    public ServiceResult<DatasetRO> listOrganizationDatasets(@PathVariable("user-id") String userId,
                                                             @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                             @RequestParam(value = "limit", defaultValue = "50") int limit,
                                                             @RequestParam(value = "sort", required = false) String sort,
                                                             @RequestParam(value = "q", required = false) String query,
                                                             HttpServletResponse response) {
        LOG.info("Inside listOrganizationDatasets for DSD.");
        String messageIdentifier = UUID.randomUUID().toString();
        String token = originalSenderTokenRetriever.getRequesterIdentityToken();
        token = StringUtils.isBlank(token)?userId:token;
        ServiceResult<DatasetRO> serviceResult = dsdDatasetMessagingService.searchDatasets(messageIdentifier,userId, token, offset, limit, sort, query);


        jwsService.signJsonResponse(serviceResult, response);
        return serviceResult;
    }

    @Operation(summary = "Update dataset", description = "update dataset by id", tags = {"dataset"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name = "edel-message-sig", required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(schema = @Schema(implementation = OrganizationRO.class)))})
    @PutMapping("users/{user-id}/dataset/{dataset-id}")
    public DatasetRO updateDataset(
            @PathVariable("user-id") String userId,
            @PathVariable("dataset-id") String datasetId,
            @RequestBody DatasetRO updateEntity, HttpServletResponse response) {
        // implementation
        String messageIdentifier = UUID.randomUUID().toString();
        // set status to update

        DSDDataUpdateRO result = dataUpdateDao.updateDataset(updateEntity, messageIdentifier, userId);

        jwsService.signJsonResponse(updateEntity, response);
        // call DSD mock
        // set status to update
        String token = originalSenderTokenRetriever.getRequesterIdentityToken();
        token = StringUtils.isBlank(token)?userId:token;
        dsdDatasetMessagingService.updateDataset(updateEntity, messageIdentifier,userId, token);
        return updateEntity;
    }

    @Operation(summary = "Create dataset", description = "Add dataset", tags = {"dataset"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name = "edel-message-sig", required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(schema = @Schema(implementation = OrganizationRO.class)))})
    @PostMapping("users/{user-id}/dataset")
    public DatasetRO createDataset(
            @PathVariable("user-id") String userId,
            @RequestBody DatasetRO updateEntity, HttpServletResponse response) {
        // implementation
        String messageIdentifier = UUID.randomUUID().toString();

        DSDDataUpdateRO result = dataUpdateDao.createDataset(updateEntity, messageIdentifier, userId);

        jwsService.signJsonResponse(updateEntity, response);
        // call DSD mock
        // set status to update
        String token = originalSenderTokenRetriever.getRequesterIdentityToken();
        token = StringUtils.isBlank(token)?userId:token;
        dsdDatasetMessagingService.createDataset(updateEntity, messageIdentifier,userId, token);
        return updateEntity;
    }

    @Operation(summary = "Delete dataset", description = "Delete dataset by id", tags = { "dataset" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    headers = @Header(name="edel-message-sig",required = true, ref = "#/components/headers/edel-message-sig"),
                    content = @Content(schema = @Schema(implementation = OrganizationRO.class))) })
    @DeleteMapping("users/{user-id}/dataset/{id}")
    public void deleteOrganization(@PathVariable("user-id") String userId,@PathVariable("id") String datasetId, HttpServletResponse response) {
        String messageIdentifier = UUID.randomUUID().toString();
        // set status to update
        String token = originalSenderTokenRetriever.getRequesterIdentityToken();
        token = StringUtils.isBlank(token)?userId:token;
        DSDDataUpdateRO result = dataUpdateDao.deleteDataset(messageIdentifier, userId);
        DatasetRO datasetRO = new DatasetRO();
        datasetRO.getIdentifiers().add(datasetId);
        jwsService.signJsonResponse(datasetRO, response);
        // call DSD mock
        dsdDatasetMessagingService.deleteDataset(datasetRO, messageIdentifier,userId, token);

    }
}
