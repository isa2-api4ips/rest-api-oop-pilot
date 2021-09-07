package eu.europa.ec.isa2.restapi.profile.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;

import java.io.*;
import java.nio.charset.Charset;

public class InputStreamDataSource implements DataSource {
    private static final Logger LOG = LoggerFactory.getLogger(InputStreamDataSource.class);

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final String name;

    public InputStreamDataSource(InputStream inputStream, String name) {
        this.name = name;
        try {
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            inputStream.close();
            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOG.info("Body\n[{}]", new String(buffer.toByteArray(), Charset.defaultCharset()));

    }

    @Override
    public String getContentType() {
        return new MimetypesFileTypeMap().getContentType(name);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(buffer.toByteArray());
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