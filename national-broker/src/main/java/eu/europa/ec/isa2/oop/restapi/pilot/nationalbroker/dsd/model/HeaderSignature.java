package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model;

import java.io.Serializable;

public class HeaderSignature  implements Serializable  {
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
