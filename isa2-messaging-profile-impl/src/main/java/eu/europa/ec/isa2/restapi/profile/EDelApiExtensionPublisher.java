package eu.europa.ec.isa2.restapi.profile;

import java.io.Serializable;
import java.net.URL;

public class EDelApiExtensionPublisher implements Serializable {
    String name;
    URL url;

    public EDelApiExtensionPublisher(String name, URL url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
