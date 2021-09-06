package eu.ec.digit.edelivery.restapi.profile.generator;

import eu.europa.ec.isa2.oop.restapi.docsapi.DSDOrganizationApi;
import eu.europa.ec.isa2.restapi.profile.generator.OpenApiWriter;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class OpenApiWriterTest {
    OpenApiWriter testInstance = new OpenApiWriter();

    @Test
    public void generateJSONForClass() throws IOException {
        File f = new File("./target/test.json");

        testInstance.writeOpenApiForClass(DSDOrganizationApi.class, f, null, MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS, null, "v1", null);


    }
}