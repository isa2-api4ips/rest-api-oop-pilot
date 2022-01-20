package eu.europa.ec.isa2.oop.dsd.property;

import eu.europa.ec.isa2.restapi.jws.TruststoreDataProvider;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static eu.europa.ec.isa2.oop.dsd.property.DsdMockPropertyMetaDataManager.*;
import static eu.europa.ec.isa2.oop.dsd.property.DsdMockPropertyMetaDataManager.DSD_MOCK_HIBERNATE_CONNECTION_DRIVER_CLASS;
import static eu.europa.ec.isa2.oop.dsd.property.DsdMockPropertyMetaDataManager.DSD_OAUTH2_SECURITY_ENABLED;

@Component(DSD_MOCK_PROPERTIES)
@PropertySources({
        @PropertySource(value = "classpath:application.properties"),
        @PropertySource(value = "classpath:dsd.properties"),
        @PropertySource(value = "file:///${dsd.config.location}/dsd.properties", ignoreResourceNotFound = true)
})
public class DsdMockProperties implements TruststoreDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DsdMockProperties.class);

    public static String OOP_REST_API_PILOT_CONFIG_BASE_LOCATION;

    public static String DSD_MOCK_CONFIG_LOCATION = "";

    //########## H2 DATABASE #############
    @Value("${" + DSD_MOCK_HIBERNATE_CONNECTION_DRIVER_CLASS + "}")
    private String hibernateConnectionDriverClass;

    @Value("${" + DSD_MOCK_HIBERNATE_DIALECT + "}")
    private String hibernateDialect;

    @Value("${" + DSD_MOCK_HIBERNATE_NEW_ID_GENERATOR_MAPPING + "}")
    private String hibernateNewIdGeneratorMappings;

    @Value("${" + DSD_MOCK_HIBERNATE_TRANSACTION_FACTORY_CLASS + "}")
    private String hibernateTransactionFactoryClass;

    @Value("${" + DSD_MOCK_HIBERNATE_TRANSACTION_JTA_PLATFORM + "}")
    private String hibernateTransactionJtaPlatform;

    @Value("${" + DSD_MOCK_HIBERNATE_FORMAT_SQL + "}")
    private String hibernateFormatSql;

    @Value("${" + DSD_MOCK_HIBERNATE_SHOW_SQL + "}")
    private String hibernateShowSql;

    @Value("${" + DSD_MOCK_DB_CREATE + "}")
    private String createDatabase;

    @Value("${" + DSD_MOCK_DB_INIT_SCRIPT + "}")
    private String databaseInitScript;
    //########## H2 DATABASE #############


    //########## JMS #############
    @Value("${" + DSD_MOCK_JMS_CONNECTIONFACTORY_SESSION_CACHE_SIZE + "}")
    private String jms_connection_factory_connection_cache_size;
    //########## JMS #############


    @Value("${" + DSD_MOCK_STORAGE_LOCATION + "}")
    private String storageLocation;

    @Value("${" + DSD_MOCK_MESSAGING_API_TYPE + "}")
    private String messagingAPIDefinitionType;

    @Value("${" + DSD_MOCK_MESSAGING_API_DEFINITION_URL + "}")
    private String messagingApiDefinitionUrl;


    @Value("${" + DSD_MOCK_KEYSTORE_LOCATION + "}")
    private String keystoreLocation;

    @Value("${" + DSD_MOCK_KEYSTORE_TYPE + "}")
    private String keystoreType;

    @Value("${" + DSD_MOCK_KEYSTORE_CREDENTIALS + "}")
    private String keystoreCredentials;

    @Value("${" + DSD_MOCK_SIGNATURE_KEY_ALIAS + "}")
    private String signatureKeyAlias;

    @Value("${" + DSD_MOCK_SIGNATURE_KEY_CREDENTIALS + "}")
    private String signatureKeyCredentials;


    @Value("${" + DSD_MOCK_TRUSTSTORE_LOCATION + "}")
    private String truststoreLocation;

    @Value("${" + DSD_MOCK_TRUSTSTORE_TYPE + "}")
    private String truststoreType;

    @Value("${" + DSD_MOCK_TRUSTSTORE_CREDENTIALS + "}")
    private String truststoreCredentials;

    @Value("${" + DSD_PAYLOAD_DIGEST_ALGORITHM + "}")
    private String payloadDigestAlgorithm;

    @Value("${" + DSD_MESSAGING_SECURITY_ENABLED + "}")
    private String messagingSecurityEnabled;

    @Value("${" + DSD_OAUTH2_SECURITY_ENABLED + "}")
    private String oauthSecurity_EnabledFlag;

    @Value("${" + DSD_OAUTH2_SECURITY_TOKEN_URL + "}")
    private String oauthSecurityTokenUrl;

    @Value("${" + DSD_OAUTH2_SECURITY_JWK_KEYSET_URL + "}")
    private String oauthSecurityJwkKeysetUrl;

    @Value("${" + DSD_MOCK_ORIGINAL_SENDER + "}")
    private String demoDsdOriginalSender;

    @Value("${" + DSD_MOCK_FINAL_RECIPIENT + "}")
    private String demoDsdFinalRecipient;



    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public String getDemoDsdOriginalSender() {
        return demoDsdOriginalSender;
    }

    public String getDemoDsdFinalRecipient() {
        return demoDsdFinalRecipient;
    }

    public String getHibernateConnectionDriverClass() {
        return hibernateConnectionDriverClass;
    }

    public boolean createDatabase() {
        return BooleanUtils.toBoolean(createDatabase);
    }

    public String getDatabaseInitScript() {
        return databaseInitScript;
    }

    public String getHibernateDialect() {
        return hibernateDialect;
    }

    public String getHibernateNewIdGeneratorMappings() {
        return hibernateNewIdGeneratorMappings;
    }

    public String getHibernateTransactionFactoryClass() {
        return hibernateTransactionFactoryClass;
    }

    public String getHibernateTransactionJtaPlatform() {
        return hibernateTransactionJtaPlatform;
    }

    public String getHibernateFormatSql() {
        return hibernateFormatSql;
    }

    public String getHibernateShowSql() {
        return hibernateShowSql;
    }

    public String getJmsConnectionFactoryConnectionCacheSize() {
        return jms_connection_factory_connection_cache_size;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getMessagingAPIDefinitionType() {
        return messagingAPIDefinitionType;
    }

    public String getMessagingApiDefinitionUrl() {
        return messagingApiDefinitionUrl;
    }

    public String getKeystoreLocation() {
        return keystoreLocation;
    }

    public String getKeystoreType() {
        return keystoreType;
    }

    public String getKeystoreCredentials() {
        return keystoreCredentials;
    }

    public String getSignatureKeyAlias() {
        return signatureKeyAlias;
    }

    public String getSignatureKeyCredentials() {
        return signatureKeyCredentials;
    }

    public String getTruststoreLocation() {
        return truststoreLocation;
    }

    public String getTruststoreType() {
        return truststoreType;
    }

    public char[]  getTruststoreCredentials() {
        return StringUtils.isBlank(truststoreCredentials)?null:truststoreCredentials.toCharArray();
    }

    public String getPayloadDigestAlgorithm() {
        return payloadDigestAlgorithm;
    }

    public Boolean isMessagingSecurityEnabled() {
        return BooleanUtils.toBoolean(messagingSecurityEnabled);
    }

    public String getMessagingSecurityEnabled() {
        return messagingSecurityEnabled;
    }

    public boolean isOAuthSecurityEnabled() {
        return (StringUtils.equalsIgnoreCase(oauthSecurity_EnabledFlag, "true")) ? true : false;
    }

    public String getOauthSecurityTokenUrl() {
        return oauthSecurityTokenUrl;
    }

    public String getOauthSecurityJwkKeysetUrl() {
        return oauthSecurityJwkKeysetUrl;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty(DSD_MOCK_HIBERNATE_CONNECTION_DRIVER_CLASS, hibernateConnectionDriverClass);
        properties.setProperty(DSD_MOCK_HIBERNATE_DIALECT, hibernateDialect);
        properties.setProperty(DSD_MOCK_HIBERNATE_NEW_ID_GENERATOR_MAPPING, hibernateNewIdGeneratorMappings);
        properties.setProperty(DSD_MOCK_HIBERNATE_TRANSACTION_FACTORY_CLASS, hibernateTransactionFactoryClass);
        properties.setProperty(DSD_MOCK_HIBERNATE_TRANSACTION_JTA_PLATFORM, hibernateTransactionJtaPlatform);
        properties.setProperty(DSD_MOCK_HIBERNATE_FORMAT_SQL, hibernateFormatSql);
        properties.setProperty(DSD_MOCK_HIBERNATE_SHOW_SQL, hibernateShowSql);
        properties.setProperty(DSD_MOCK_DB_CREATE, createDatabase);
        properties.setProperty(DSD_MOCK_DB_INIT_SCRIPT, databaseInitScript);
        properties.setProperty(DSD_MOCK_JMS_CONNECTIONFACTORY_SESSION_CACHE_SIZE, jms_connection_factory_connection_cache_size);
        properties.setProperty(DSD_MOCK_STORAGE_LOCATION, storageLocation);
        properties.setProperty(DSD_MOCK_MESSAGING_API_TYPE, messagingAPIDefinitionType);
        properties.setProperty(DSD_MOCK_MESSAGING_API_DEFINITION_URL, messagingApiDefinitionUrl);
        properties.setProperty(DSD_MOCK_KEYSTORE_LOCATION, keystoreLocation);
        properties.setProperty(DSD_MOCK_KEYSTORE_TYPE, keystoreType);
        properties.setProperty(DSD_MOCK_KEYSTORE_CREDENTIALS, keystoreCredentials);
        properties.setProperty(DSD_MOCK_SIGNATURE_KEY_ALIAS, signatureKeyAlias);
        properties.setProperty(DSD_MOCK_SIGNATURE_KEY_CREDENTIALS, signatureKeyCredentials);
        properties.setProperty(DSD_MOCK_TRUSTSTORE_LOCATION, truststoreLocation);
        properties.setProperty(DSD_MOCK_TRUSTSTORE_TYPE, truststoreType);
        properties.setProperty(DSD_MOCK_TRUSTSTORE_CREDENTIALS, truststoreCredentials);
        properties.setProperty(DSD_PAYLOAD_DIGEST_ALGORITHM, payloadDigestAlgorithm);
        properties.setProperty(DSD_MESSAGING_SECURITY_ENABLED, messagingSecurityEnabled);
        properties.setProperty(DSD_OAUTH2_SECURITY_ENABLED, oauthSecurity_EnabledFlag);
        properties.setProperty(DSD_OAUTH2_SECURITY_TOKEN_URL, oauthSecurityTokenUrl);
        properties.setProperty(DSD_OAUTH2_SECURITY_JWK_KEYSET_URL, oauthSecurityJwkKeysetUrl);

        properties.setProperty(DSD_MOCK_ORIGINAL_SENDER, demoDsdOriginalSender);
        properties.setProperty(DSD_MOCK_FINAL_RECIPIENT, demoDsdFinalRecipient);
        return properties;
    }

}
