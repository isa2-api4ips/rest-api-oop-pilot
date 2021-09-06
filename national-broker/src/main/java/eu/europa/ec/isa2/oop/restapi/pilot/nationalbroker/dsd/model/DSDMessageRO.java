package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model;

import java.util.HashMap;
import java.util.Map;

public class DSDMessageRO {

    Map<String, String> headers;
    String body;

    public Map<String, String> getHeaders() {
        if(headers ==null) {
            headers = new HashMap<>();
        }
        return headers;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
