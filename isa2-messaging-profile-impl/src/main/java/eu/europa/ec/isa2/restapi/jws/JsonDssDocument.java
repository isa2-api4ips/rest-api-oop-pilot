package eu.europa.ec.isa2.restapi.jws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectWriter;
import eu.europa.esig.dss.model.CommonDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.MimeType;
import io.swagger.v3.core.util.Json;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class JsonDssDocument extends CommonDocument implements DataSource{

    Object jsonObject;
    boolean prettyPrint;
    byte[] serializedObject;

    /**
     * Creates dss document with json object. Name is json object class simple name
     *
     * @param jsonObject
     */
    public JsonDssDocument(Object jsonObject) {
        this(jsonObject, jsonObject.getClass().getSimpleName());

    }

    /**
     * Creates dss document with json object
     *
     * @param jsonObject
     * @param name
     */
    public JsonDssDocument(Object jsonObject, String name) {
        this(jsonObject, jsonObject.getClass().getSimpleName(), true);
    }

    /**
     * Creates dss document with json object
     *
     * @param jsonObject
     * @param name
     */
    public JsonDssDocument(Object jsonObject, String name, boolean prettyPrint) {
        Objects.requireNonNull(jsonObject, "Json object must not be null!");
        this.name = name;
        this.mimeType = MimeType.JSON;
        this.jsonObject = jsonObject;
        this.prettyPrint = prettyPrint;
        initializeBuffer();
    }

    protected void initializeBuffer(){
        Objects.requireNonNull(jsonObject, "jsonObject is null");
        ObjectWriter writer = prettyPrint?Json.mapper().writer(new DefaultPrettyPrinter()): Json.mapper().writer();
        try {
            this.serializedObject  = writer.writeValueAsBytes(jsonObject);
        } catch (JsonProcessingException e) {
            throw new DSSException("Unable to serialize json object: ["+jsonObject.getClass()+"]", e);
        }
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    @Override
    public InputStream openStream() {
        return new ByteArrayInputStream(serializedObject);
    }
    public int getSize(){
        return  serializedObject!=null?serializedObject.length:0;
    }

    @Override
    public InputStream getInputStream()  {
        return openStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Read-only data");
    }

    @Override
    public String getContentType() {
        return getMimeType().getMimeTypeString();
    }
}