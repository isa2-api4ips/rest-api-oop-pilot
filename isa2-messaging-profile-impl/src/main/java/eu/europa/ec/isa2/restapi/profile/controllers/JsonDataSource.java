package eu.europa.ec.isa2.restapi.profile.controllers;

import io.swagger.v3.core.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.activation.DataSource;
import java.io.*;

public class JsonDataSource implements DataSource {
    private static final Logger LOG = LoggerFactory.getLogger(JsonDataSource.class);
    private final String name;
    private final Object value;

    public JsonDataSource(Object jsonValue, String name) {
        this.name = name;
        this.value = jsonValue;

    }

    @Override
    public String getContentType() {
        return  MediaType.APPLICATION_JSON_VALUE;
    }

    @Override
    public InputStream getInputStream() throws IOException {

        return new ByteArrayInputStream(Json.pretty(value).getBytes());
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