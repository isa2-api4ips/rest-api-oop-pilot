package eu.europa.ec.isa2.restapi.jws;

public interface TruststoreDataProvider {

    String getTruststoreLocation();
    String getTruststoreType();
    char[] getTruststoreCredentials();
}
