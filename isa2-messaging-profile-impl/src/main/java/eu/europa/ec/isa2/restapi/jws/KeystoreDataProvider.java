package eu.europa.ec.isa2.restapi.jws;

import java.security.KeyStore;

public interface KeystoreDataProvider {

    String getLocation();
    String getType();
    char[] getCredentials();
    KeyStore.ProtectionParameter getKeyCredentials(String alias);
}
