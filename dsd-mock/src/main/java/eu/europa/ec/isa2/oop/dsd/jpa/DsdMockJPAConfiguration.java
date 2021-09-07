package eu.europa.ec.isa2.oop.dsd.jpa;
import eu.europa.ec.isa2.oop.dsd.property.DsdMockProperties;
import eu.europa.ec.isa2.oop.dsd.property.DsdMockPropertyMetaDataManager;
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
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DsdMockJPAConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DsdMockJPAConfiguration.class);

    @Autowired
    @Qualifier(DsdMockPropertyMetaDataManager.DSD_MOCK_PROPERTIES)
    private DsdMockProperties dsdMockProperties;

    @Bean(DsdMockPropertyMetaDataManager.DSD_MOCK_JDBC_XA_DATA_SOURCE)
    public JndiObjectFactoryBean xaDatasource() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setExpectedType(DataSource.class);
        LOG.warn("Configuring datasource with jndi name [{}]", DsdMockPropertyMetaDataManager.DSD_MOCK_DATASOURCE_JNDI_NAME);
        jndiObjectFactoryBean.setJndiName(DsdMockPropertyMetaDataManager.DSD_MOCK_DATASOURCE_JNDI_NAME);
        return jndiObjectFactoryBean;
    }

    @Bean(DsdMockPropertyMetaDataManager.DSD_MOCK_TRANSACTION_MANAGER)
    public JtaTransactionManager jtaTransactionManager() {
        LOG.info("Creating org.springframework.transaction.jta.JtaTransactionManager for " + DsdMockPropertyMetaDataManager.DSD_MOCK_TRANSACTION_MANAGER);
        return new JtaTransactionManager();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    @DependsOn({DsdMockPropertyMetaDataManager.DSD_MOCK_PROPERTIES, DsdMockPropertyMetaDataManager.DSD_MOCK_TRANSACTION_MANAGER, DsdMockPropertyMetaDataManager.DSD_MOCK_JDBC_XA_DATA_SOURCE})
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier(DsdMockPropertyMetaDataManager.DSD_MOCK_JDBC_XA_DATA_SOURCE) DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean result = new LocalContainerEntityManagerFactoryBean();
        result.setPersistenceUnitName(DsdMockPropertyMetaDataManager.DSD_MOCK_PERSISTENCE_UNIT);
        result.setPackagesToScan("eu.europa.ec.isa2.oop.dsd.dao.entities");
        result.setJtaDataSource(dataSource);
        result.setJpaVendorAdapter(jpaVendorAdapter());


        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.connection.driver_class", dsdMockProperties.getHibernateConnectionDriverClass());
        jpaProperties.put("hibernate.dialect", dsdMockProperties.getHibernateDialect());
        jpaProperties.put("hibernate.id.new_generator_mappings", dsdMockProperties.getHibernateNewIdGeneratorMappings());
        jpaProperties.put("hibernate.transaction.factory_class", dsdMockProperties.getHibernateTransactionFactoryClass());
        jpaProperties.put("hibernate.transaction.jta.platform", dsdMockProperties.getHibernateTransactionJtaPlatform());
        jpaProperties.put("hibernate.format_sql", dsdMockProperties.getHibernateFormatSql());
        jpaProperties.put("hibernate.show_sql", dsdMockProperties.getHibernateShowSql());

        if (dsdMockProperties.createDatabase()) {
            jpaProperties.put("hibernate.hbm2ddl.auto", "create");
        }
        result.setJpaProperties(jpaProperties);

        return result;
    }

}
