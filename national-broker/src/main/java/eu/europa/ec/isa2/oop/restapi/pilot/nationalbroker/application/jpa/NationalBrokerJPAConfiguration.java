package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.jpa;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerPropertyMetaDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

import static eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerPropertyMetaDataManager.*;

@Configuration
public class NationalBrokerJPAConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(NationalBrokerJPAConfiguration.class);

    @Autowired
    @Qualifier(NATIONAL_BROKER_PROPERTIES)
    private NationalBrokerProperties nationalBrokerProperties;

    @Bean(NATIONAL_BROKER_JDBC_XA_DATA_SOURCE)
    public JndiObjectFactoryBean xaDatasource() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setExpectedType(DataSource.class);

        LOG.debug("Configuring datasource with jndi name [{}]", NationalBrokerPropertyMetaDataManager.NATIONAL_BROKER_DATASOURCE_JNDI_NAME);
        jndiObjectFactoryBean.setJndiName(NationalBrokerPropertyMetaDataManager.NATIONAL_BROKER_DATASOURCE_JNDI_NAME);
        return jndiObjectFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier(NATIONAL_BROKER_JDBC_XA_DATA_SOURCE) DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean result = new LocalContainerEntityManagerFactoryBean();
        result.setPersistenceUnitName(NATIONAL_BROKER_PERSISTENCE_UNIT);
        result.setPackagesToScan("eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker");
        result.setJtaDataSource(dataSource);
        result.setJpaVendorAdapter(jpaVendorAdapter());
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.connection.driver_class", nationalBrokerProperties.getHibernateConnectionDriverClass());
        jpaProperties.put("hibernate.dialect", nationalBrokerProperties.getHibernateDialect());
        jpaProperties.put("hibernate.id.new_generator_mappings", nationalBrokerProperties.getHibernateNewIdGeneratorMappings());
        jpaProperties.put("hibernate.transaction.factory_class", nationalBrokerProperties.getHibernateTransactionFactoryClass());
        jpaProperties.put("hibernate.transaction.jta.platform", nationalBrokerProperties.getHibernateTransactionJtaPlatform());
        jpaProperties.put("hibernate.format_sql", nationalBrokerProperties.getHibernateFormatSql());
        jpaProperties.put("hibernate.show_sql", nationalBrokerProperties.getHibernateShowSql());
        // create database if nationalbroker.database.create is set to true
        if (nationalBrokerProperties.createDatabase()) {
            jpaProperties.put("hibernate.hbm2ddl.auto", "create");
        }
        result.setJpaProperties(jpaProperties);

        return result;
    }

}
