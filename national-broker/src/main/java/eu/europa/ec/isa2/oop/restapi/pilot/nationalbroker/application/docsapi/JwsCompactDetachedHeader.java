package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.docsapi;

import io.swagger.v3.oas.models.media.Schema;

public class JwsCompactDetachedHeader extends Schema<String> {

    public JwsCompactDetachedHeader() {
        super("string", "jws-compact-detached");
        setPattern("^[A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+){2}$");
    }
}
