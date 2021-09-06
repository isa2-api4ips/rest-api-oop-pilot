package eu.europa.ec.isa2.restapi.jws;

import javax.mail.internet.MimeMultipart;
import java.util.HashMap;
import java.util.Map;

public class SignedMimeMultipart extends MimeMultipart {
    Map<String, String> httpHeaders = new HashMap<>();

    public void addHttpHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }
}
