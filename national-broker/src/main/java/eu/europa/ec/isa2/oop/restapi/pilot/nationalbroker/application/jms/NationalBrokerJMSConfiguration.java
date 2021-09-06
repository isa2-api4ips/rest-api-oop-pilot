package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.Optional;

import static eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerPropertyMetaDataManager.*;

@Configuration
public class NationalBrokerJMSConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(NationalBrokerJMSConfiguration.class);

    /**
     * Bean name for the JMS connection factory that is to be used when working with Spring's
     * {@link org.springframework.jms.core.JmsOperations JmsTemplate}.
     *
     * <p><b>Note: for performace reasons, the actual bean instance must implement caching of message producers (e.g.
     * {@link org.springframework.jms.connection.CachingConnectionFactory})<b/></p>
     */
    @Bean(NATIONAL_BROKER_JMS_CACHING_XACONNECTION_FACTORY_BEAN)
    public ConnectionFactory cachingConnectionFactory(@Qualifier(NATIONAL_BROKER_JMS_XACONNECTION_FACTORY_BEAN) ConnectionFactory wildflyConnectionFactory,
                                                      @Qualifier(NATIONAL_BROKER_PROPERTIES) NationalBrokerProperties nationalBrokerProperties) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        String jmsConnectionSessionCacheSize = nationalBrokerProperties.getJmsConnectionFactoryConnectionCacheSize();
        LOG.info("Using session cache size:[{}] for JMS connection factory.", jmsConnectionSessionCacheSize);
        int sessionCacheSize = Integer.parseInt(jmsConnectionSessionCacheSize);
        cachingConnectionFactory.setSessionCacheSize(sessionCacheSize);
        cachingConnectionFactory.setTargetConnectionFactory(wildflyConnectionFactory);
        cachingConnectionFactory.setCacheConsumers(false);

        return cachingConnectionFactory;
    }

    /**
     * Bean name for the JMS connection factory that is to be used when working with Spring
     * {@link org.springframework.jms.listener.DefaultMessageListenerContainer message listener containers}.
     *
     * <p><b>Note: the actual bean instance must take advantage of the caching provided by the message listeners
     * themselves and must not be used in conjunction with the
     * {@link org.springframework.jms.connection.CachingConnectionFactory} if dynamic scaling is required.<b/></p>
     */
    @Bean(NATIONAL_BROKER_JMS_XACONNECTION_FACTORY_BEAN)
    public JndiObjectFactoryBean connectionFactory() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiName(NATIONAL_BROKER_JMS_CONNECTION_FACTORY_JNDI_NAME);
        jndiObjectFactoryBean.setLookupOnStartup(false);
        jndiObjectFactoryBean.setExpectedType(ConnectionFactory.class);
        return jndiObjectFactoryBean;
    }

    @Bean(NATIONAL_BROKER_JMS_INTERNAL_DESTINATION_RESOLVER)
    public JndiDestinationResolver internalDestinationResolver() {
        JndiDestinationResolver result = new JndiDestinationResolver();
        result.setCache(true);
        result.setFallbackToDynamicDestination(false);
        return result;
    }


    @Bean(NATIONAL_BROKER_JSONMESSAGE_CONVERTER_BEAN) // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
        converter.setObjectMapper(objectMapper);
        return converter;
    }


    @Bean(NATIONAL_BROKER_JMS_LISTENER_CONTAINER_FACTORY)
    public JmsListenerContainerFactory nationalBrokerJMSListenerContainerFactory(@Qualifier(NATIONAL_BROKER_JMS_CACHING_XACONNECTION_FACTORY_BEAN) ConnectionFactory connectionFactory,
                                                                                 @Qualifier(NATIONAL_BROKER_TRANSACTION_MANAGER) PlatformTransactionManager transactionManager,
                                                                                 @Qualifier(NATIONAL_BROKER_JMS_INTERNAL_DESTINATION_RESOLVER) DestinationResolver destinationResolver,
                                                                                 @Qualifier(NATIONAL_BROKER_JSONMESSAGE_CONVERTER_BEAN) MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(connectionFactory);
        defaultJmsListenerContainerFactory.setTransactionManager(transactionManager);
        defaultJmsListenerContainerFactory.setConcurrency("1-10");
        defaultJmsListenerContainerFactory.setSessionTransacted(true);
        defaultJmsListenerContainerFactory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        defaultJmsListenerContainerFactory.setDestinationResolver(destinationResolver);
        defaultJmsListenerContainerFactory.setMessageConverter(messageConverter);
        return defaultJmsListenerContainerFactory;
    }

}
