package eu.europa.ec.isa2.oop.restapi.config;

import eu.europa.ec.isa2.restapi.profile.model.APIProblem;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class OpenApiConfigTest {

    @Test
    public void convertApiSchema(){

        final Map<String, Schema> schemas = ModelConverters.getInstance().readAll(APIProblem.class);

        assertFalse(schemas.isEmpty());
    }


}