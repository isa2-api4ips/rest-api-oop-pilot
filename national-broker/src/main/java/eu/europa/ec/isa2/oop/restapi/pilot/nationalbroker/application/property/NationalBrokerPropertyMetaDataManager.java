package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property;

public abstract class NationalBrokerPropertyMetaDataManager {
    public static final String OOP_REST_API_PILOT_CONFIG_BASE_LOCATION = "oop.rest.api.pilot.config.base.loc";
    public static final String NATIONAL_BROKER_CONFIG_LOCATION = "national.broker.config.loc";
    public static final String NATIONAL_BROKER_PROPERTIES_FILE = "national_broker.properties";
    public static final String NATIONAL_BROKER_PROPERTIES = "NationalBrokerProperties";

    //Database
    public static final String NATIONAL_BROKER_DATASOURCE_JNDI_NAME = "jdbc/NationalBrokerDS";
    public static final String NATIONAL_BROKER_TRANSACTION_MANAGER = "NationalBroker_TransactionManager";
    public static final String NATIONAL_BROKER_JDBC_XA_DATA_SOURCE = "NationalBrokerJDBC_XADataSource";
    public static final String NATIONAL_BROKER_PERSISTENCE_UNIT = "NationalBroker_PersistenceUnit";

    public static final String NATIONAL_BROKER_HIBERNATE_CONNECTION_DRIVER_CLASS = "nationalbroker.hibernate.connection.driver_class";
    public static final String NATIONAL_BROKER_HIBERNATE_DIALECT = "nationalbroker.hibernate.dialect";
    public static final String NATIONAL_BROKER_HIBERNATE_NEW_ID_GENERATOR_MAPPING = "nationalbroker.hibernate.id.new_generator_mappings";
    public static final String NATIONAL_BROKER_HIBERNATE_TRANSACTION_FACTORY_CLASS = "nationalbroker.hibernate.transaction.factory_class";
    public static final String NATIONAL_BROKER_HIBERNATE_TRANSACTION_JTA_PLATFORM = "nationalbroker.hibernate.transaction.jta.platform";
    public static final String NATIONAL_BROKER_HIBERNATE_FORMAT_SQL = "nationalbroker.hibernate.format_sql";
    public static final String NATIONAL_BROKER_HIBERNATE_SHOW_SQL = "nationalbroker.hibernate.show_sql";
    public static final String NATIONAL_BROKER_DB_CREATE ="nationalbroker.database.create";
    public static final String NATIONAL_BROKER_DB_INIT_SCRIPT ="nationalbroker.database.script";

    //JMS

    public static final String NATIONAL_BROKER_JMS_CACHING_XACONNECTION_FACTORY_BEAN = "NationalBrokerJMS-CachingXAConnectionFactory";

    public static final String NATIONAL_BROKER_JMS_XACONNECTION_FACTORY_BEAN = "NationalBrokerJMS-XAConnectionFactory";

    public static final String NATIONAL_BROKER_JMS_INTERNAL_DESTINATION_RESOLVER = "NationalBrokerInternalDestinationResolver";
    public static final String NATIONAL_BROKER_JSONMESSAGE_CONVERTER_BEAN = "NationalBrokerJSONMessageConverter";

    public static final String NATIONAL_BROKER_JMS_CONNECTION_FACTORY_JNDI_NAME = "jms/ConnectionFactory";
    public static final String NATIONAL_BROKER_ROA_COMMANDS_QUEUE_JNDI_NAME = "jms/nationalbroker.jms.roaCommandsQueue";
    public static final String NATIONAL_BROKER_JMS_CONNECTIONFACTORY_SESSION_CACHE_SIZE = "nationalbroker.jms.connectionFactory.session.cache.size";
    //public static final String NATIONAL_BROKER_ROA_COMMANDS_QUEUE_BEAN = "NationalBroker_ROACommandsQueue";
    //public static final String NATIONAL_BROKER_ROAJMSTEMPLATE_BEAN = "NationalBrokerROAJMSTemplate";
    public static final String NATIONAL_BROKER_JMS_LISTENER_CONTAINER_FACTORY = "NationalBrokerJMSListenerContainerFactory";

    public static final String NATIONAL_BROKER_CROSS_ORIGINS = "nationalbroker.cross-origin.urls";


    public static final String NATIONAL_BROKER_STORAGE_LOCATION = "nationalbroker.storage.location";

    //keystore and truststore
    public static final String NATIONAL_BROKER_KEYSTORE_LOCATION = "nationalbroker.messaging.security.keystore.location";
    public static final String NATIONAL_BROKER_KEYSTORE_TYPE ="nationalbroker.messaging.security.keystore.type";
    public static final String NATIONAL_BROKER_KEYSTORE_CREDENTIALS ="nationalbroker.messaging.security.keystore.password";
    public static final String NATIONAL_BROKER_SIGNATURE_KEY_ALIAS="nationalbroker.messaging.security.signature.key.alias";
    public static final String NATIONAL_BROKER_SIGNATURE_KEY_CREDENTIALS="nationalbroker.messaging.security.signature.key.password";

    public static final String NATIONAL_BROKER_TRUSTSTORE_LOCATION = "nationalbroker.messaging.security.truststore.location";
    public static final String NATIONAL_BROKER_TRUSTSTORE_TYPE ="nationalbroker.messaging.security.truststore.type";
    public static final String NATIONAL_BROKER_TRUSTSTORE_CREDENTIALS ="nationalbroker.messaging.security.truststore.password";

    // MESSAGES
    public static final String NATIONAL_BROKER_PAYLOAD_DIGEST_ALGORITHM ="nationalbroker.messaging.payload.digest.algorithm";

    //OAuth2 security
    public static final String NATIONAL_BROKER_OAUTH2_SECURITY_ENABLED = "nationalbroker.oauth2.spring.security.enabled";
    public static final String NATIONAL_BROKER_OAUTH2_UICLIENT_IDP_ISSUER ="nationalbroker.oauth2.uiclient.idp.issuer";
    public static final String NATIONAL_BROKER_OAUTH2_UICLIENT_IDP_CERTIFICATE_ALIAS="nationalbroker.oauth2.uiclient.idp.certificate.alias";
    public static final String NATIONAL_BROKER_OAUTH2_ACCESSTOKEN_EXPIRY_SECONDS="nationalbroker.oauth2.accesstoken.expiry.seconds";
    public static final String NATIONAL_BROKER_OAUTH2_IDP_TOKEN_EXPECTED_AUDIENCE="nationalbroker.oauth2.idp.token.expected.audience";
    public static final String NATIONAL_BROKER_OAUTH2_PATH_EXCEPTION="nationalbroker.oauth2.path.exceptions";

    //OAuth2 Client
    public static final String NATIONAL_BROKER_DSD_OAUTH_CLIENT_TOKEN_URL="nationalbroker.dsd.oauth.client.token.url";
    public static final String NATIONAL_BROKER_DSD_OAUTH_CLIENT_JWK_KEYSET_URL="nationalbroker.dsd.oauth.client.jwk.keyset.url";
    public static final String NATIONAL_BROKER_DSD_OAUTH_CLIENT_CLIENTID="nationalbroker.dsd.oauth.client.clientid";
    public static final String NATIONAL_BROKER_DSD_OAUTH_CLIENT_CLIENT_SECRET="nationalbroker.dsd.oauth.client.clientsecret";
    public static final String NATIONAL_BROKER_DSD_OAUTH_CLIENT_SCOPES="nationalbroker.dsd.oauth.client.scopes";

    public static final String NATIONAL_BROKER_DSD_URL="nationalbroker.dsd.url";
    public static final String NATIONAL_BROKER_WEBHOOK_DSD_URL="nationalbroker.dsd.webhook.url";

    public static final String NATIONAL_BROKER_DSD_FINAL_RECIPIENT="nationalbroker.dsd.finalRecipient";
    public static final String NATIONAL_BROKER_APPLICATION_ORGINAL_SENDER="nationalbroker.application.originalSender";

}
