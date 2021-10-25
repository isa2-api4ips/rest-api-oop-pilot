package eu.europa.ec.isa2.restapi.profile.generator;

import eu.europa.ec.isa2.restapi.profile.EDelApiExtensionLifecycle;
import eu.europa.ec.isa2.restapi.profile.EDelApiExtensionPublisher;
import eu.europa.ec.isa2.restapi.profile.OpenApiGenerator;
import eu.europa.ec.isa2.restapi.profile.enums.*;
import eu.europa.ec.isa2.restapi.reader.MessagingReader;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import eu.europa.ec.isa2.restapi.reader.utils.MessagingOpenApiUtils;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static eu.europa.ec.isa2.restapi.profile.enums.SpecificationPartType.*;
import static eu.europa.ec.isa2.restapi.profile.enums.SpecificationReferencePartType.*;

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

        if (result.getWebhooks() != null && !result.getWebhooks().isEmpty()) {
            Map<String, PathItem> webhooks = result.getWebhooks();

            if (openApi.getPaths() != null) {
                openApi.getPaths().clear();
            } else {
                openApi.setPaths(new Paths());
            }
            // set webhooks as paths for server
            webhooks.keySet().stream().forEach((pathName) -> {
                openApi.path(pathName, webhooks.get(pathName));
            });
            if (openApi.getWebhooks() != null) {
                openApi.getWebhooks().clear();
            }
            String apiName = filePath.getName();
            File fileServerPath = new File(filePath.getParent(), apiName.substring(0, apiName.lastIndexOf(".")) + "-server.json");
            Json.pretty().writeValue(new FileOutputStream(fileServerPath), openApi);
        }
    }

    public static void writeProfileForClass(Class clazz, File folderPath,
                                            String messagingAPITypeDefinitionURL, String apiVersion, Properties properties) throws IOException {

        MessagingReader reader = new MessagingReader(MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS, messagingAPITypeDefinitionURL, apiVersion, properties);
        OpenAPI result = reader.read(clazz);
        result.setTags(MessagingOpenApiUtils.getGlobalTags(clazz));
        // write generic messaging api to messaging profile
        writeGenericMessagingApi(MESSAGING_PROFILE, result, folderPath);
        writeSignalDefinitionsForMessagingApi(MESSAGING_PROFILE, folderPath);
        writeOpenApiXObjects(DOCUMENT_PROFILE, folderPath);

        // sort and write objects by specification
        Components coreComponents = new Components();
        coreComponents.setHeaders(new HashMap<>());
        coreComponents.setParameters(new HashMap<>());
        coreComponents.setSchemas(new HashMap<>());
        Components messagingComponents = new Components();
        messagingComponents.setHeaders(new HashMap<>());
        messagingComponents.setParameters(new HashMap<>());
        messagingComponents.setSchemas(new HashMap<>());

        sortParameterObjects(result.getComponents().getHeaders(), coreComponents.getHeaders(), messagingComponents.getHeaders());
        sortParameterObjects(result.getComponents().getParameters(), coreComponents.getParameters(), messagingComponents.getParameters());
        sortSchemaObjects(result.getComponents().getSchemas(), coreComponents.getSchemas(), messagingComponents.getSchemas());

        writeComponent(CORE_PROFILE, coreComponents, folderPath, messagingAPITypeDefinitionURL);
        writeComponent(MESSAGING_PROFILE, messagingComponents, folderPath, messagingAPITypeDefinitionURL);
    }

    public static void writeGenericMessagingApi(SpecificationPartType specificationPartType, OpenAPI result, File profileFolder) throws IOException {
        OpenApiGenerator generator = new OpenApiGenerator();
        OpenAPI openApi = generator.generatedMessagingAPI();
        openApi.paths(result.getPaths())
                .webhooks(result.getWebhooks())
                .components(result.getComponents())
                .tags(result.getTags());

        File messagingFolder = createFolder(profileFolder,
                specificationPartType.getName());
        File componentFilePath = new File(messagingFolder, "generic-messaging-api.json");
        writeObjectToFile(openApi, componentFilePath);
    }

    public static void writeOpenApiXObjects(SpecificationPartType specificationPartType, File profileFolder) throws IOException {
        File documentFolder = createFolder(profileFolder,
                specificationPartType.getName());

        File lifecycleFilePath = new File(documentFolder, "x-edel-lifecycle.json");
        ResolvedSchema resolvedSchema = ModelConverters.getInstance().readAllAsResolvedSchema(EDelApiExtensionLifecycle.class);
        writeObjectToFile(resolvedSchema.schema, lifecycleFilePath);

        File publisherFilePath = new File(documentFolder, "x-edel-publisher.json");
        ResolvedSchema publisherSchema = ModelConverters.getInstance().readAllAsResolvedSchema(EDelApiExtensionPublisher.class);
        writeObjectToFile(publisherSchema.schema, publisherFilePath);
    }

    public static void writeSignalDefinitionsForMessagingApi(SpecificationPartType specificationPartType, File profileFolder) throws IOException {

        File messagingFolder = createFolder(profileFolder,
                specificationPartType.getName());
        File signalDefFilePath = new File(messagingFolder, "signal.md");
        String lineSeparator = System.lineSeparator();
        try (FileWriter writer = new FileWriter(signalDefFilePath)) {
            writer.append("<h1>Predefined Signals</h1>");
            writer.append(lineSeparator);
            writer.append("The following pre-defined signals MUST be supported by both the client and the server implementing the Messaging API Specification:");
            writer.append(lineSeparator);

            for (APIProblemType type : APIProblemType.values()) {
                // Requirement from spec writers: skip pull specific types since they are not mandatory
                if (type.isPullSpecific()) {
                    continue;
                }

                writer.append(lineSeparator);
                writer.append("<h2 id=\"");
                writer.append(type.getFileName());
                writer.append("\">");
                writer.append(type.getTitle());
                writer.append("</h2>");
                writer.append(lineSeparator);

                writer.append("<b>Status:</b> ");
                writer.append(type.getStatus().toString());
                writer.append("<BR />");
                writer.append(lineSeparator);

                writer.append("<b>Type:</b> ");
                writer.append(type.getType());
                writer.append("<BR />");
                writer.append(lineSeparator);

                writer.append("<b>Detail:</b> ");
                writer.append(type.getDetail());
                writer.append("<BR />");
                writer.append(lineSeparator);
            }
        }
    }

    public static void writeComponent(SpecificationPartType specificationPartType, Components components, File profileFolder, String urlPrefix) throws IOException {
        // write just component.json
        File specificationFolder = createFolder(profileFolder,
                specificationPartType.getName());
        File componentFilePath = new File(specificationFolder, COMPONENTS.getName() + ".json");
        OpenAPI openAPI = new OpenAPI();
        openAPI.setOpenapi(null);
        openAPI.setComponents(components);
        writeObjectToFile(openAPI, componentFilePath);


        // write objects
        for (Map.Entry<String, Schema> entry : components.getSchemas().entrySet()) {
            String s = entry.getKey();
            Schema schema = entry.getValue();
            Optional<MessagingSchemaType> optionalMessagingParameterType = MessagingSchemaType.getByName(s);
            if (!optionalMessagingParameterType.isPresent()) {
                LOG.warn("Could not find schema by name [{}]", s);
                continue;
            }
            updateReferences(schema, urlPrefix);
            writeSchemaObject(schema, optionalMessagingParameterType.get(), profileFolder);
        }

        // write headers
        for (Map.Entry<String, Header> entry : components.getHeaders().entrySet()) {
            String s = entry.getKey();
            Header header = entry.getValue();
            updateReferences(header.getSchema(), urlPrefix);
            Optional<MessagingParameterType> optionalMessagingParameterType = MessagingParameterType.getByName(s);
            writeParameterObject(header, optionalMessagingParameterType.get(), profileFolder, Header.class);
        }
        // write parameters
        for (Map.Entry<String, Parameter> entry : components.getParameters().entrySet()) {
            String s = entry.getKey();
            Parameter parameter = entry.getValue();
            updateReferences(parameter.getSchema(), urlPrefix);
            Optional<MessagingParameterType> optionalMessagingParameterType = MessagingParameterType.getByName(s);
            writeParameterObject(parameter, optionalMessagingParameterType.get(), profileFolder, Parameter.class);
        }

    }

    static <T> void sortParameterObjects(Map<String, T> input, Map<String, T> core, Map<String, T> messaging) {

        input.entrySet().forEach(entry -> {
            String s = entry.getKey();
            T parameter = entry.getValue();
            Optional<MessagingParameterType> optionalMessagingParameterType = MessagingParameterType.getByName(s);
            if (optionalMessagingParameterType.isPresent()) {
                MessagingParameterType parameterType = optionalMessagingParameterType.get();
                switch (parameterType.getMessagingReferenceType().getSpecificationPart()) {
                    case CORE_PROFILE:
                        core.put(s, parameter);
                        break;
                    case MESSAGING_PROFILE:
                        messaging.put(s, parameter);
                        break;
                    case DOCUMENT_PROFILE:
                        break;
                }
                LOG.info("parameterType:" + parameterType);
            } else {
                throw new IllegalArgumentException("Could not find parameter by name [" + s + "]");
            }
        });
    }

    static <T> void sortSchemaObjects(Map<String, Schema> input, Map<String, Schema> core, Map<String, Schema> messaging) {

        input.entrySet().forEach(entry -> {
            String s = entry.getKey();
            Schema parameter = entry.getValue();
            Optional<MessagingSchemaType> optionalMessagingType = MessagingSchemaType.getByName(s);
            if (optionalMessagingType.isPresent()) {
                MessagingSchemaType type = optionalMessagingType.get();
                switch (type.getMessagingReferenceType().getSpecificationPart()) {
                    case CORE_PROFILE:
                        core.put(s, parameter);
                        break;
                    case MESSAGING_PROFILE:
                        messaging.put(s, parameter);
                        break;
                    case DOCUMENT_PROFILE:
                        break;
                }
                LOG.info("parameterType:" + s);
            } else {
                LOG.warn("Could not find schema by name [" + s + "]");
            }
        });
    }


    public static void updateReferences(Schema schema, String urlPrefix) {
        if (StringUtils.isNotBlank(schema.get$ref())) {
            schema.set$ref(updateReference(schema.get$ref(), urlPrefix));
        }
        if (schema instanceof ArraySchema) {
            ArraySchema arraySchema = (ArraySchema) schema;
            Schema itemSchema = arraySchema.getItems();
            if (StringUtils.isNotBlank(itemSchema.get$ref())) {
                itemSchema.set$ref(updateReference(itemSchema.get$ref(), urlPrefix));
            }
        }

        Map<String, Schema> propertyEntries = schema.getProperties();
        if (propertyEntries != null) {
            for (Map.Entry<String, Schema> propertyEntry : propertyEntries.entrySet()) {
                Schema propSchema = propertyEntry.getValue();
                updateReferences(propSchema, urlPrefix);
            }
        }
    }

    public static String updateReference(String reference, String urlPrefix) {
        String schemaName = reference.substring(reference.lastIndexOf('/') + 1);
        System.out.println("Get for schema:" + schemaName);
        Optional<MessagingSchemaType> optionalType = MessagingSchemaType.getByName(schemaName);
        if (optionalType.isPresent()) {
            MessagingSchemaType schemaType = optionalType.get();
            MessagingReferenceType referenceType = schemaType.getMessagingReferenceType();
            return StringUtils.appendIfMissing(urlPrefix, "/")
                    + referenceType.getSpecificationPart().getName()
                    + "/" + COMPONENTS.getName()
                    + "/" + SCHEMAS.getName()
                    + "/" + referenceType.getObjectURIDefinition();
        } else
            return reference;
    }

    public static File createFolder(File profileFolder, String... subfolders) throws IOException {
        File specificationFolder = new File(profileFolder, StringUtils.join(subfolders, File.separatorChar));

        if (!specificationFolder.exists() && !specificationFolder.mkdirs()) {
            LOG.error("Could not create folders [{}]!", specificationFolder.getAbsolutePath());
            throw new IOException("Error occurred while creating profile folder: " + specificationFolder.getAbsolutePath() + "");
        }
        return specificationFolder;
    }

    static <T> void writeParameterObject(T parameter, MessagingParameterType type, File profileFolder, Class<T> clazz) throws IOException {

        MessagingReferenceType referenceType = type.getMessagingReferenceType();
        // create SpecificationFolder
        File paramFolder = createFolder(profileFolder,
                referenceType.getSpecificationPart().getName(),
                COMPONENTS.getName(),
                Objects.equals(clazz, Header.class) ? HEADERS.getName() : PARAMETERS.getName());
        File objectFilePath = new File(paramFolder, referenceType.getObjectURIDefinition());
        LOG.info("Store file: " + objectFilePath.getAbsolutePath());

        writeObjectToFile(parameter, objectFilePath);
    }


    static void writeSchemaObject(Schema value, MessagingSchemaType type, File profileFolder) throws IOException {

        MessagingReferenceType referenceType = type.getMessagingReferenceType();
        File schemaFolder = createFolder(profileFolder,
                referenceType.getSpecificationPart().getName(),
                COMPONENTS.getName(),
                SCHEMAS.getName());

        File objectFilePath = new File(schemaFolder, referenceType.getObjectURIDefinition());
        LOG.info("Store file: " + objectFilePath.getAbsolutePath());

        writeObjectToFile(value, objectFilePath);
    }

    public static void writeObjectToFile(Object value, File objectFilePath) throws IOException {
        try (FileOutputStream fis = new FileOutputStream(objectFilePath)) {
            Json.pretty().writeValue(fis, value);
        }
    }
}


