package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerPropertyMetaDataManager.*;

@Component(NATIONAL_BROKER_PROPERTIES)
@PropertySources({
        @PropertySource(value = "classpath:application.properties"),
        @PropertySource(value = "classpath:national_broker.properties"),
        @PropertySource(value = "file:///${nationalbroker.config.location}/national_broker.properties", ignoreResourceNotFound = true)
})
public class NationalBrokerProperties  implements TruststoreDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(NationalBrokerProperties.class);

    public static String OOP_REST_API_PILOT_CONFIG_BASE_LOCATION;
    public static String NATIONAL_BROKER_CONFIG_LOCATION;


    @Value("${" + NATIONAL_BROKER_STORAGE_LOCATION + "}")
    public String storageLocation;

    //########## H2 DATABASE #############
    @Value("${" + NATIONAL_BROKER_HIBERNATE_CONNECTION_DRIVER_CLASS + "}")
    private String hibernateConnectionDriverClass;

    @Value("${" + NATIONAL_BROKER_HIBERNATE_DIALECT + "}")
    private String hibernateDialect;

    @Value("${" + NATIONAL_BROKER_HIBERNATE_NEW_ID_GENERATOR_MAPPING + "}")
    private String hibernateNewIdGeneratorMappings;

    @Value("${" + NATIONAL_BROKER_HIBERNATE_TRANSACTION_FACTORY_CLASS + "}")
    private String hibernateTransactionFactoryClass;

    @Value("${" + NATIONAL_BROKER_HIBERNATE_TRANSACTION_JTA_PLATFORM + "}")
    private String hibernateTransactionJtaPlatform;

    @Value("${" + NATIONAL_BROKER_HIBERNATE_FORMAT_SQL + "}")
    private String hibernateFormatSql;

    @Value("${" + NATIONAL_BROKER_HIBERNATE_SHOW_SQL + "}")
    private String hibernateShowSql;

    @Value("${" + NATIONAL_BROKER_DB_CREATE + "}")
    private String createDatabase;

    @Value("${" + NATIONAL_BROKER_DB_INIT_SCRIPT + "}")
    private String databaseInitScript;
    //########## H2 DATABASE #############


    //########## JMS #############
    @Value("${" + NATIONAL_BROKER_JMS_CONNECTIONFACTORY_SESSION_CACHE_SIZE + "}")
    private String jms_connection_factory_connection_cache_size;
    //########## JMS #############

    @Value("${" + NATIONAL_BROKER_CROSS_ORIGINS + "}")
    private String crossOriginUrls;

    @Value("${" + NATIONAL_BROKER_KEYSTORE_LOCATION + "}")
    private String keystoreLocation;

    @Value("${" + NATIONAL_BROKER_KEYSTORE_TYPE + "}")
    private String keystoreType;

    @Value("${" + NATIONAL_BROKER_KEYSTORE_CREDENTIALS + "}")
    private String keystoreCredentials;

    @Value("${" + NATIONAL_BROKER_SIGNATURE_KEY_ALIAS + "}")
    private String signatureKeyAlias;

    @Value("${" + NATIONAL_BROKER_SIGNATURE_KEY_CREDENTIALS + "}")
    private String signatureKeyCredentials;


    @Value("${" + NATIONAL_BROKER_TRUSTSTORE_LOCATION + "}")
    private String truststoreLocation;

    @Value("${" + NATIONAL_BROKER_TRUSTSTORE_TYPE + "}")
    private String truststoreType;

    @Value("${" + NATIONAL_BROKER_TRUSTSTORE_CREDENTIALS + "}")
    private String truststoreCredentials;

    @Value("${" + NATIONAL_BROKER_PAYLOAD_DIGEST_ALGORITHM + "}")
    private String payloadDigestAlgorithm;

    //########## OAUTH2 #############
    @Value("${" + NATIONAL_BROKER_OAUTH2_SECURITY_ENABLED + "}")
    private String oauthSecurity_EnabledFlag;

    @Value("${" + NATIONAL_BROKER_OAUTH2_UICLIENT_IDP_ISSUER + "}")
    private String oauth_uiclient_idp_issuer;

    @Value("${" + NATIONAL_BROKER_OAUTH2_UICLIENT_IDP_CERTIFICATE_ALIAS + "}")
    private String oauth_uiclient_idp_certificate_alias;

    @Value("${" + NATIONAL_BROKER_OAUTH2_ACCESSTOKEN_EXPIRY_SECONDS + "}")
    private int oauthAccessTokenExpiryPeriodInSeconds;

    @Value("${" + NATIONAL_BROKER_OAUTH2_IDP_TOKEN_EXPECTED_AUDIENCE + "}")
    private String oauth_IdpAccessToken_ExpectedAudience;

    @Value("${" + NATIONAL_BROKER_OAUTH2_PATH_EXCEPTION + "}")
    private String oauth_path_exception;



    @Value("${" + NATIONAL_BROKER_DSD_OAUTH_CLIENT_TOKEN_URL + "}")
    private String oauth_DSDCient_TokenURL;

    @Value("${" + NATIONAL_BROKER_DSD_OAUTH_CLIENT_JWK_KEYSET_URL + "}")
    private String oauth_DSDClient_JWK_Keyset_Url;

    @Value("${" + NATIONAL_BROKER_DSD_OAUTH_CLIENT_CLIENTID + "}")
    private String oauth_DSDClient_ClientId;

    @Value("${" + NATIONAL_BROKER_DSD_OAUTH_CLIENT_CLIENT_SECRET + "}")
    private String oauth_DSDClient_ClientSecret;

    @Value("${" + NATIONAL_BROKER_DSD_OAUTH_CLIENT_SCOPES + "}")
    private String oauth_DSDClient_Scopes;
    //########## OAUTH2 #############

    // Because of the simplicity for the pilot is one DSD but this can be extended to map with multiple URLS and final
    // recipients chosen by country code!
    @Value("${" + NATIONAL_BROKER_DSD_URL + "}")
    private String dsdUrl;
    @Value("${" + NATIONAL_BROKER_DSD_FINAL_RECIPIENT + "}")
    private String dsdFinalRecipient;

    @Value("${" + NATIONAL_BROKER_WEBHOOK_DSD_URL + "}")
    private String dsdWebhookUrl;

    @Value("${" + NATIONAL_BROKER_APPLICATION_ORGINAL_SENDER + "}")
    private String applicationOriginalSender;
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getCreateDatabase() {
        return createDatabase;
    }


    public String[] getCrossOriginUrls() {
        return StringUtils.split(crossOriginUrls, ",");
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

    public boolean isOAuthSecurityEnabled() {
        return (StringUtils.equalsIgnoreCase(oauthSecurity_EnabledFlag, "true")) ? true : false;
    }

    public String getOauth_uiclient_idp_issuer() {
        return oauth_uiclient_idp_issuer;
    }

    public String getOauth_uiclient_idp_certificate_alias() {
        return oauth_uiclient_idp_certificate_alias;
    }

    public int getOauthAccessTokenExpiryPeriodInSeconds() {
        return oauthAccessTokenExpiryPeriodInSeconds;
    }

    public String getOauth_IdpAccessToken_ExpectedAudience() {
        return oauth_IdpAccessToken_ExpectedAudience;
    }

    public List<String> getOauthPathExceptions() {
        return StringUtils.isBlank(oauth_path_exception)? Collections.emptyList():Arrays.asList(oauth_path_exception.split(","));
    }

    public String getOauth_DSDCient_TokenURL() {
        return oauth_DSDCient_TokenURL;
    }

    public String getOauth_DSDClient_JWK_Keyset_Url() {
        return oauth_DSDClient_JWK_Keyset_Url;
    }

    public String getOauth_DSDClient_ClientId() {
        return oauth_DSDClient_ClientId;
    }

    public String getOauth_DSDClient_ClientSecret() {
        return oauth_DSDClient_ClientSecret;
    }

    public List<String> getOauth_DSDClient_Scopes() {
        return Arrays.asList(oauth_DSDClient_Scopes.split(","));
    }

    public String getDsdUrl() {
        return dsdUrl;
    }

    public String getDsdFinalRecipient() {
        return dsdFinalRecipient;
    }

    public String getApplicationOriginalSender() {
        return applicationOriginalSender;
    }

    public String getDsdWebhookUrl() {
        return dsdWebhookUrl;
    }
}
