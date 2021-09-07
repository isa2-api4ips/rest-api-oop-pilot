package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging;

import org.junit.Test;

import static org.junit.Assert.*;

public class DSDClientHttpRequestInterceptorTest {
    DSDClientHttpRequestInterceptor testInstance = new DSDClientHttpRequestInterceptor(null, null);
    @Test
    public void parseServiceAndAction() {
        //given
        String path ="/dsd-mock/v1/messaging/dataset/query/05318207-657e-41ba-8b97-5777bb9d5d97/sync";
        //when
        testInstance.parseServiceAndAction(path);
        // then
        assertEquals("dataset",testInstance.getCurrentService());
        assertEquals("query",testInstance.getCurrentAction());

    }
}