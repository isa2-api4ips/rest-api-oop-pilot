package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.OrganizationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableJms
@EnableScheduling
@ComponentScan(basePackages = {"eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker",
        "eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd",
        "org.springdoc"})
@EnableTransactionManagement(proxyTargetClass = true)
public class NationalBrokerWebAppConfig implements WebMvcConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(NationalBrokerWebAppConfig.class);

    NationalBrokerProperties configurer;

    public NationalBrokerWebAppConfig(@Autowired  NationalBrokerProperties configurer) {
        this.configurer = configurer;
    }

    /**
     *  Bean needed for initializing org.springdoc (with spring boot this is not needed!)
     *  https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-spring-mvc
     * @return ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper springDocObjectMapper() {
        com.fasterxml.jackson.databind.ObjectMapper responseMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return responseMapper;
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // allow for all
        CorsRegistration registration = registry.addMapping("/**");
        LOG.info("Allowed Cors [{}]",configurer.getCrossOriginUrls() );
        registration.allowedOrigins(configurer.getCrossOriginUrls());
        registration.allowedMethods("GET", "PUT","POST");
    }
}
