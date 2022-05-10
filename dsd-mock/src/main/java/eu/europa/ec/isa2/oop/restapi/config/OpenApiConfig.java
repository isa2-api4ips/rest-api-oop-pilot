package eu.europa.ec.isa2.oop.restapi.config;

import eu.europa.ec.isa2.oop.dsd.property.DsdMockProperties;
import eu.europa.ec.isa2.oop.restapi.controller.profile.controllers.MessageServiceHandlerController;
import eu.europa.ec.isa2.oop.restapi.docsapi.DSDDatasetApi;
import eu.europa.ec.isa2.oop.restapi.docsapi.DSDOrganizationApi;
import eu.europa.ec.isa2.restapi.profile.OpenApiGenerator;
import eu.europa.ec.isa2.restapi.reader.MessagingReader;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import eu.europa.ec.isa2.restapi.reader.utils.MessagingOpenApiUtils;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {
    private static final Logger LOG = LoggerFactory.getLogger(OpenApiConfig.class);
    private static final String VERSION = "v1";
    public static final String MESSAGING_API_GROUP = "openapi.json";
    public static final String ORGANIZATION_API_GROUP = "organization-openapi.json";
    public static final String DATASET_API_GROUP = "dataset-openapi.json";

    public static final String DOC_PREFIX = "/v3/api-docs/";
    //public static final String OPENAPI_DOCUMENT_SCHEMA = "https://raw.githubusercontent.com/OAI/OpenAPI-Specification/main/schemas/v3.1/schema-base.json";
    public static final String OPENAPI_DOCUMENT_SCHEMA = "https://spec.openapis.org/oas/3.1/schema/2022-02-27";



    OpenApiGenerator openApiGenerator = new OpenApiGenerator();

    DsdMockProperties dsdMockProperties;

    @Autowired
    public OpenApiConfig(DsdMockProperties dsdMockProperties) {
        this.dsdMockProperties = dsdMockProperties;
    }

    @Bean
    public OpenAPI dsdMockOpenAPI() throws MalformedURLException {
        Server server = new Server();
        server.setUrl("https://gateway-edelivery.westeurope.cloudapp.azure.com:883/dsd-mock");
        server.description("DSD Mock demo services!");

        OpenAPI openAPI = openApiGenerator.generatedMessagingAPI();
        openAPI.setServers(Arrays.asList(server));
        return openAPI;
    }

    @Bean
    public GroupedOpenApi organizationMessagingApi() {
        return GroupedOpenApi.builder()
                .group(ORGANIZATION_API_GROUP)
                .addOpenApiCustomiser(openApi -> customizeMessagingAPI(openApi, ORGANIZATION_API_GROUP, DSDOrganizationApi.class))
                .build();
    }

    @Bean
    public GroupedOpenApi datasetMessagingApi() {
        return GroupedOpenApi.builder()
                .group(DATASET_API_GROUP)
                .addOpenApiCustomiser(openApi -> customizeMessagingAPI(openApi, DATASET_API_GROUP, DSDDatasetApi.class))
                .build();
    }

    @Bean
    public GroupedOpenApi generalMessagingApi() {
        // use controller class to parse annotation for all extended apis!
        return GroupedOpenApi.builder()
                .group(MESSAGING_API_GROUP)
                .addOpenApiCustomiser(openApi -> customizeMessagingAPI(openApi, MESSAGING_API_GROUP, MessageServiceHandlerController.class))
                .build();
    }


    public void customizeMessagingAPI(OpenAPI openApi, String group, Class clazz) {
        String type = dsdMockProperties.getMessagingAPIDefinitionType();
        MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation;
        try {
            messagingAPIDefinitionsLocation = MessagingAPIDefinitionsLocation.valueOf(type);
        } catch (IllegalArgumentException ex) {
            messagingAPIDefinitionsLocation = MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS;
            LOG.warn("Invalid type: [{}]. Allowed values: [{}]", type,
                    String.join(",", Arrays.stream(MessagingAPIDefinitionsLocation.values()).map(String::valueOf).collect(Collectors.toList())));
        }

        MessagingReader reader = new MessagingReader(messagingAPIDefinitionsLocation, dsdMockProperties.getMessagingApiDefinitionUrl(), VERSION, dsdMockProperties.getProperties());
        OpenAPI result = reader.read(clazz);

        io.swagger.v3.oas.models.media.MediaType mediaType = new io.swagger.v3.oas.models.media.MediaType();
                //.schema(new Schema().$ref(OPENAPI_DOCUMENT_SCHEMA));
        //It MUST contain either one Path Item object "/openapi.json" and/or one Path Item object "/openapi.yaml"
        PathItem pathItem = new PathItem()
                .summary("The API's current OpenAPI Document.")
                .get(
                new Operation().tags(Collections.singletonList("OpenApi document"))
                        .summary("The GET operation that responds with the API's current OpenAPI JSON Document.")
                        .responses(new ApiResponses()
                        .addApiResponse("200",
                                new ApiResponse().content(
                                        new Content()
                                                .addMediaType(MediaType.APPLICATION_JSON_VALUE, mediaType)))));

        Paths paths = result.getPaths();
        paths.addPathItem(DOC_PREFIX + group, pathItem);

        openApi.paths(paths).
                components(result.getComponents())
                .webhooks(result.getWebhooks())
                .tags(MessagingOpenApiUtils.getGlobalTags(clazz));
    }

}