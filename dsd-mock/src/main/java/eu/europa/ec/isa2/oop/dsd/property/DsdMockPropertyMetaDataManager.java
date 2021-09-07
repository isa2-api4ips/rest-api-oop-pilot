package eu.europa.ec.isa2.oop.dsd.property;

public abstract class DsdMockPropertyMetaDataManager {
    public static final String OOP_REST_API_PILOT_CONFIG_BASE_LOCATION = "oop.rest.api.pilot.config.base.loc";
    public static final String DSD_MOCK_CONFIG_LOCATION = "dsd.config.location";
    public static final String DSD_MOCK_STORAGE_LOCATION = "dsd.storage.location";
    public static final String DSD_MOCK_PROPERTIES_FILE = "dsd.properties";
    public static final String DSD_MOCK_PROPERTIES = "DsdMockProperties";

    //Database
    public static final String DSD_MOCK_DATASOURCE_JNDI_NAME = "jdbc/DsdMockDS";
    public static final String DSD_MOCK_TRANSACTION_MANAGER = "DsdMock_TransactionManager";
    public static final String DSD_MOCK_JDBC_XA_DATA_SOURCE = "DsdMockJDBC_XADataSource";
    public static final String DSD_MOCK_PERSISTENCE_UNIT = "DsdMock_PersistenceUnit";

    public static final String DSD_MOCK_HIBERNATE_CONNECTION_DRIVER_CLASS = "dsd.hibernate.connection.driver_class";
    public static final String DSD_MOCK_HIBERNATE_DIALECT = "dsd.hibernate.dialect";
    public static final String DSD_MOCK_HIBERNATE_NEW_ID_GENERATOR_MAPPING = "dsd.hibernate.id.new_generator_mappings";
    public static final String DSD_MOCK_HIBERNATE_TRANSACTION_FACTORY_CLASS = "dsd.hibernate.transaction.factory_class";
    public static final String DSD_MOCK_HIBERNATE_TRANSACTION_JTA_PLATFORM = "dsd.hibernate.transaction.jta.platform";
    public static final String DSD_MOCK_HIBERNATE_FORMAT_SQL = "dsd.hibernate.format_sql";
    public static final String DSD_MOCK_HIBERNATE_SHOW_SQL = "dsd.hibernate.show_sql";
    public static final String DSD_MOCK_DB_CREATE ="dsd.database.create";
    public static final String DSD_MOCK_DB_INIT_SCRIPT ="dsd.database.script";

    //JMS

    public static final String DSD_MOCK_JMS_CACHING_XACONNECTION_FACTORY_BEAN = "DsdMockJMS-CachingXAConnectionFactory";

    public static final String DSD_MOCK_JMS_XACONNECTION_FACTORY_BEAN = "DsdMockJMS-XAConnectionFactory";

    public static final String DSD_MOCK_JMS_INTERNAL_DESTINATION_RESOLVER = "DsdMockInternalDestinationResolver";
    public static final String DSD_MOCK_JSONMESSAGE_CONVERTER_BEAN = "DsdMockJSONMessageConverter";

    public static final String DSD_MOCK_JMS_CONNECTION_FACTORY_JNDI_NAME = "jms/ConnectionFactory";
    public static final String DSD_MOCK_ROA_COMMANDS_QUEUE_JNDI_NAME = "jms/dsd.jms.roaCommandsQueue";
    public static final String DSD_MOCK_JMS_CONNECTIONFACTORY_SESSION_CACHE_SIZE = "dsd.jms.connectionFactory.session.cache.size";
    public static final String DSD_MOCK_ROA_COMMANDS_QUEUE_BEAN = "DsdMock_ROACommandsQueue";
    public static final String DSD_MOCK_ROAJMSTEMPLATE_BEAN = "DsdMockROAJMSTemplate";
    public static final String DSD_MOCK_JMS_LISTENER_CONTAINER_FACTORY = "DsdMockJMSListenerContainerFactory";

    public static final String DSD_MOCK_MESSAGING_API_TYPE = "dsd.messaging.api.type";
    public static final String DSD_MOCK_MESSAGING_API_DEFINITION_URL = "dsd.messaging.api.definition.url";

    //keystore and truststore
    public static final String DSD_MOCK_KEYSTORE_LOCATION = "dsd.messaging.security.keystore.location";
    public static final String DSD_MOCK_KEYSTORE_TYPE ="dsd.messaging.security.keystore.type";
    public static final String DSD_MOCK_KEYSTORE_CREDENTIALS ="dsd.messaging.security.keystore.password";
    public static final String DSD_MOCK_SIGNATURE_KEY_ALIAS="dsd.messaging.security.signature.key.alias";
    public static final String DSD_MOCK_SIGNATURE_KEY_CREDENTIALS="dsd.messaging.security.signature.key.password";

    public static final String DSD_MOCK_TRUSTSTORE_LOCATION = "dsd.messaging.security.truststore.location";
    public static final String DSD_MOCK_TRUSTSTORE_TYPE ="dsd.messaging.security.truststore.type";
    public static final String DSD_MOCK_TRUSTSTORE_CREDENTIALS ="dsd.messaging.security.truststore.password";

    // MESSAGES
    public static final String DSD_PAYLOAD_DIGEST_ALGORITHM ="dsd.messaging.payload.digest.algorithm";
    public static final String DSD_MESSAGING_SECURITY_ENABLED ="dsd.messaging.security.validation.enabled";

    //OAuth2
    public static final String DSD_OAUTH2_SECURITY_ENABLED="dsd.oauth2.spring.security.enabled";
    public static final String DSD_OAUTH2_SECURITY_TOKEN_URL ="dsd.oauth2.spring.security.token.url";
    public static final String DSD_OAUTH2_SECURITY_JWK_KEYSET_URL ="dsd.oauth2.spring.security.jwk.keyset.url";

}
