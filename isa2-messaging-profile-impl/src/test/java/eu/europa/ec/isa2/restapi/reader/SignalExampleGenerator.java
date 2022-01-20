package eu.europa.ec.isa2.restapi.reader;

import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.ec.isa2.restapi.profile.model.SignalMessage;
import io.swagger.v3.core.util.Json;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class SignalExampleGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(SignalExampleGenerator.class);
    @Test
    public void generateAllSignals(){

        File parent = new File("target/signal");
        parent.mkdirs();
        Arrays.stream(APIProblemType.values()).forEach(response ->{
            SignalMessage message = new SignalMessage(
                    response.getTitle(),
                    response.getStatus(),
                    response.getType(),
                    response.getDetail(),
                    "[A URI reference that identifies the specific occurrence of the problem. It may or may not yield further information if dereferenced as example: /my-service/my-action/dde12f67-c391-4851-8fa2-c07dd8532efd]",
                    "[The digest of the received message using the notation proposed in 'Digest Header' (https://datatracker.ietf.org/doc/html/draft-ietf-httpbis-digest-headers) as example: sha-256=eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ]");
            try {
                Json.pretty().writeValue(new FileOutputStream(new File(parent, response.getFileName())), message);
            } catch (IOException e) {
                LOG.error("Error occured while writting the signals", e);
                e.printStackTrace();
            }

        });

    }
    @Test
    public void generateAllSignalsVersion2() throws IOException {
        File parent = new File("target");
        parent.mkdirs();
        FileWriter fileWriter = new FileWriter(new File(parent, "predefined-signal.html"));

        Arrays.stream(APIProblemType.values()).forEach(response ->{
            try {
                fileWriter.append("<h1 id=\"");
                fileWriter.append(response.getFileName());
                fileWriter.append("\">");
                fileWriter.append(response.getTitle());
                fileWriter.append("</h1>\n");

                fileWriter.append("Status: ");
                fileWriter.append(  response.getStatus()+"");
                fileWriter.append("<BR />\n");

                fileWriter.append("Type: ");
                fileWriter.append(  response.getType());
                fileWriter.append("<BR />\n");

                fileWriter.append("Detail: ");
                fileWriter.append(  response.getDetail());
                fileWriter.append("<BR />\n");



            } catch (IOException e) {
                // for the demo just log
                LOG.error("Error occired while reading the URL",e);
            }

        });
        fileWriter.flush();
        fileWriter.close();

    }

}
