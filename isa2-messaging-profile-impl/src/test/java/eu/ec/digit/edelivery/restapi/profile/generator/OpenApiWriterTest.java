package eu.ec.digit.edelivery.restapi.profile.generator;


import eu.europa.ec.isa2.restapi.profile.docsapi.MessageServiceHandlerResponseAPI;
import eu.europa.ec.isa2.restapi.profile.docsapi.MessagingAPI;
import eu.europa.ec.isa2.restapi.profile.generator.OpenApiWriter;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class OpenApiWriterTest {
    OpenApiWriter testInstance = new OpenApiWriter();

    @Test
    public void generateJSONForClass() throws IOException {
        File f = new File("./target/test.json");

        testInstance.writeOpenApiForClass(MessageServiceHandlerResponseAPI.class, f, null, MessagingAPIDefinitionsLocation.DOCUMENT_COMPONENTS, null, "v1", null);

        Assert.assertTrue(f.exists());
    }

    @Test
    public void writeProfileForClass() throws IOException {
        testInstance.writeProfileForClass(MessagingAPI.class, new File("target/profile"), "https://raw.githubusercontent.com/isa2-api4ips/rest-api-profile/main/","v1", null);
    }
}