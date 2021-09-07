package eu.europa.ec.isa2.restapi.jws;

import eu.europa.esig.dss.model.InMemoryDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InMemoryDataSource implements DataSource {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryDataSource.class);
    private final String name;
    private final InMemoryDocument document;

    public InMemoryDataSource(InMemoryDocument document, String name) {
        this.name = name;
        this.document = document;

    }

    @Override
    public String getContentType() {
        return  MediaType.APPLICATION_JSON_VALUE;
    }

    @Override
    public InputStream getInputStream() throws IOException {

        return new ByteArrayInputStream(document.getBytes());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Read-only data");
    }

}