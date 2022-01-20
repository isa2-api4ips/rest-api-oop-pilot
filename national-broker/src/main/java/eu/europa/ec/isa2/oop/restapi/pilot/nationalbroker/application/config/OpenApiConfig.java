package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.config;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.docsapi.JwsCompactDetachedHeader;
import eu.europa.ec.isa2.restapi.profile.EDelApiExtensionLifecycle;
import eu.europa.ec.isa2.restapi.profile.EDelApiExtensionPublisher;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Configuration
public class OpenApiConfig {
    private static final Logger LOG = LoggerFactory.getLogger(OpenApiConfig.class);
    @Bean
    public OpenAPI customOpenAPI() {

        Server serverDemo = new Server();
        serverDemo.setUrl("https://localhost:8080/national-broker");
        serverDemo.setUrl("https://gateway-edelivery.westeurope.cloudapp.azure.com:883/national-broker");
        serverDemo.description("National broker demo services!");

        Server serverLocalhost = new Server();
        serverLocalhost.setUrl("http://localhost:8080/national-broker");
        serverLocalhost.description("National broker localhost services!");

        HashMap extensions = new java.util.LinkedHashMap<>();
        //put summary here because there is no summary annotation
        extensions.put("summary", "DSD messaging rest service");
        extensions.put("x-edel-lifecycle", new EDelApiExtensionLifecycle());
        try {
            extensions.put("x-edel-publisher",
                    new EDelApiExtensionPublisher("European Commission",
                            new URL("https://joinup.ec.europa.eu/collection/api4dt")));
        } catch (MalformedURLException e) {
            // for the demo just log
            LOG.error("Error occired while reading the URL",e);
        }

        return new OpenAPI()
                .components(new Components()
                        // deal with in header later
                        //.addParameters("In-header", new Parameter().in("header").schema(new JwsCompactDetachedHeader()).name("myHeader1"))
                        .addHeaders("edel-message-sig", new Header().description("Message signature header").schema(new JwsCompactDetachedHeader()))
                        .addHeaders("edel-payload-sig", new Header().description("Payload signature header").schema(new JwsCompactDetachedHeader()))
                )
                .info(new Info().title("National broker API").description(
                        "This is a pilot project for ISA2 REST API")
                        .version("v1.0")
                        .license(new License().name("EUPL 1.2").url("https://www.eupl.eu/")
                                .extensions(extensions)
                        ))

                .servers(Arrays.asList(serverDemo, serverLocalhost))
              .externalDocs(new ExternalDocumentation()
                .description("ISA2 REST Pilot")
                .url("https://ec.europa.eu/cefdigital/wiki/pages/viewpage.action?pageId=254313406"));
    }

}
