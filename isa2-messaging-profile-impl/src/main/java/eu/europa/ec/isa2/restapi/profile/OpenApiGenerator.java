package eu.europa.ec.isa2.restapi.profile;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

public class OpenApiGenerator {
    public OpenAPI generatedMessagingAPI() throws MalformedURLException {

        HashMap extensions = new java.util.LinkedHashMap<>();
        //put summary here because there is no summary annotation
        extensions.put("summary", "Generic Messaging API");
        extensions.put("x-edelivery", generateEDeliveryExtension());

        // not sure why is generating http://localhost:8080/dsd-mock/v3/a"
        Server server = new Server();
        server.setUrl("http://localhost:8080/dsd-mock");
        server.description("Test server!");

        OpenAPI openAPI = new OpenAPI()
//                .servers(Collections.singletonList(server))
//                .components(components)
                .info(new Info().title("Generic Messaging API")
                        .extensions(extensions)
                        .description("This is a pilot project for implementing ISA2 messaging REST API")
                        .termsOfService("https://www.eupl.eu/")
                        .version("v1.0")
                        .license(new License().name("EUPL 1.2").url("https://www.eupl.eu/")))
                .externalDocs(new ExternalDocumentation()
                        .description("ISA² IPS REST API Core Profile - OpenAPI Document Specification")
                        .url("https://joinup.ec.europa.eu/collection/api4dt/solution/..."));


        openAPI.setOpenapi("3.1.0");
        return openAPI;
    }

    public EDeliveryAPIExtension generateEDeliveryExtension() throws MalformedURLException {
        EDeliveryAPIExtension eDeliveryAPIExtension = new EDeliveryAPIExtension();
        eDeliveryAPIExtension.setLifecycle(new EDeliveryAPIExtension.EDelApiExtensionLifecycle());
        eDeliveryAPIExtension.setPublisher(
                new EDeliveryAPIExtension.EDelApiExtensionPublisher("European Commission",
                        new URL("https://joinup.ec.europa.eu/collection/api4dt")));
        return eDeliveryAPIExtension;
    }
}
