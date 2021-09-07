package eu.europa.ec.isa2.restapi.profile.generator;

import eu.europa.ec.isa2.restapi.profile.OpenApiGenerator;
import eu.europa.ec.isa2.restapi.reader.MessagingReader;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import eu.europa.ec.isa2.restapi.reader.utils.MessagingOpenApiUtils;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class OpenApiWriter {

    private static final Logger LOG = LoggerFactory.getLogger(OpenApiWriter.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException {


        String apiClasses = args[0]; // comma api classes
        String outputFolder = args.length > 1 ? args[1] : ""; // comma separated entity packages
        String targetOpenApiVersion = args.length > 2 ? args[2] : ""; // OpenApi version
        String referenceTypeParam = args.length > 3 ? args[3] : MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS.name(); // OpenApi version
        String messagingAPITypeDefinitionURL = args.length > 4 ? args[4] : ""; // OpenApi version
        String apiVersion = args.length > 5 ? args[5] : "v1"; // api version
        String propertiesFilePath = args.length > 6 ? args[6] : null; // api version


        if (StringUtils.isBlank(apiClasses)) {
            LOG.error("First parameter [comma separated API classes] must not be null!");
            System.exit(1);
        }

        if (StringUtils.isBlank(outputFolder)) {
            LOG.error("Second parameter [output folder] must not be null!");
            System.exit(2);
        }

        Properties properties = new Properties();
        if (!StringUtils.isBlank(propertiesFilePath)) {
            LOG.info("Property file [{}]!", propertiesFilePath);
            File fProperties = new File(propertiesFilePath);
            if (fProperties.exists()) {
                properties.load(new FileInputStream(fProperties));
            } else {
                LOG.error("Property file [{}] does not exists!", propertiesFilePath);
                System.exit(3);
            }
        }


        MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation;
        try {
            messagingAPIDefinitionsLocation = MessagingAPIDefinitionsLocation.valueOf(referenceTypeParam);
        } catch (IllegalArgumentException ex) {
            messagingAPIDefinitionsLocation = MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS;
            LOG.warn("Invalid type: [{}]. Allowed values: [{}]", referenceTypeParam,
                    String.join(",", Arrays.stream(MessagingAPIDefinitionsLocation.values()).map(String::valueOf).collect(Collectors.toList())));
        }


        File file = new File(outputFolder);
        String[] classNames = apiClasses.split("\\s*,\\s*");
        for (String className : classNames) {
            Class<?> clazz = Class.forName(className);
            if (clazz != null) {
                writeOpenApiForClass(clazz, new File(file, clazz.getSimpleName() + ".json"),
                        targetOpenApiVersion,
                        messagingAPIDefinitionsLocation,
                        messagingAPITypeDefinitionURL,
                        apiVersion, properties);

            } else {
                LOG.error("Class with name [{}] not exist!", className);
            }

        }


        System.exit(0);
    }

    public static void writeOpenApiForClass(Class clazz, File filePath,
                                            String targetOpenApiVersion,
                                            MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation,
                                            String messagingAPITypeDefinitionURL, String apiVersion, Properties properties) throws IOException {
        OpenApiGenerator generator = new OpenApiGenerator();

        OpenAPI openApi = generator.generatedMessagingAPI();

        if (StringUtils.isNotBlank(targetOpenApiVersion)) {
            openApi.setOpenapi(targetOpenApiVersion);
        }

        MessagingReader reader = new MessagingReader(messagingAPIDefinitionsLocation, messagingAPITypeDefinitionURL, apiVersion, properties);

        OpenAPI result = reader.read(clazz);
        openApi.paths(result.getPaths())
            .webhooks(result.getWebhooks())
            .components(result.getComponents());
        openApi.setTags(MessagingOpenApiUtils.getGlobalTags(clazz));
        Json.pretty().writeValue(new FileOutputStream(filePath), openApi);

        if (result.getWebhooks()!=null && !result.getWebhooks().isEmpty()) {
            Map<String, PathItem> webhooks =  result.getWebhooks();

            if (openApi.getPaths()!=null) {
                openApi.getPaths().clear();
            } else {
                openApi.setPaths(new Paths());
            }
            // set webhooks as paths for server
            webhooks.keySet().stream().forEach( (pathName)->{
                openApi.path(pathName, webhooks.get(pathName));
            });
            if (openApi.getWebhooks()!=null) {
                openApi.getWebhooks().clear();
            }
            String apiName = filePath.getName();
            File fileServerPath = new File(filePath.getParent(), apiName.substring(0, apiName.lastIndexOf(".")) +"-server.json" );
            Json.pretty().writeValue(new FileOutputStream(fileServerPath), openApi);
        }

    }
}


