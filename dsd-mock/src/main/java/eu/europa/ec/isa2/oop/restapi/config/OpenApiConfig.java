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
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {
    private static final Logger LOG = LoggerFactory.getLogger(OpenApiConfig.class);
    private static final String VERSION = "v1";

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
                .group("organization")
                .addOpenApiCustomiser(openApi -> customizeMessagingAPI(openApi, DSDOrganizationApi.class))
                .build();
    }

    @Bean
    public GroupedOpenApi datasetMessagingApi() {
        return GroupedOpenApi.builder()
                .group("dataset")
                .addOpenApiCustomiser(openApi -> customizeMessagingAPI(openApi, DSDDatasetApi.class))
                .build();
    }

    @Bean
    public GroupedOpenApi generalMessagingApi() {
        // use controller class to parse annotation for all extended apis!
        return GroupedOpenApi.builder()
                .group("api")
                .addOpenApiCustomiser(openApi -> customizeMessagingAPI(openApi, MessageServiceHandlerController.class))
                .build();
    }


    public void customizeMessagingAPI(OpenAPI openApi, Class clazz) {
        String type = dsdMockProperties.getMessagingAPIDefinitionType();
        MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation;
        try {
            messagingAPIDefinitionsLocation = MessagingAPIDefinitionsLocation.valueOf(type);
        } catch (IllegalArgumentException ex) {
            messagingAPIDefinitionsLocation = MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS;
            LOG.warn("Invalid type: [{}]. Allowed values: [{}]", type,
                    String.join(",", Arrays.stream(MessagingAPIDefinitionsLocation.values()).map(String::valueOf).collect(Collectors.toList())));
        }

        MessagingReader reader = new MessagingReader(messagingAPIDefinitionsLocation, dsdMockProperties.getMessagingApiDefinitionUrl(), VERSION, dsdMockProperties.getProperties() );
        OpenAPI result = reader.read(clazz);
        openApi.paths(result.getPaths()).
                components(result.getComponents())
                .webhooks(result.getWebhooks())
                .tags(MessagingOpenApiUtils.getGlobalTags(clazz));
    }

}