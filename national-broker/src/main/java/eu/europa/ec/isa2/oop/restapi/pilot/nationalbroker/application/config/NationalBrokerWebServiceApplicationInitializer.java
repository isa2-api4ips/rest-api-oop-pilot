package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.config;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.jpa.DatabaseConfiguration;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerConfigLocationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class NationalBrokerWebServiceApplicationInitializer implements WebApplicationInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(NationalBrokerWebServiceApplicationInitializer.class);


    @Override
    public void onStartup(ServletContext servletContext) {
        //lookup national broker config location
        String nationalBrokerConfigLocation = NationalBrokerConfigLocationProvider.loadNationalBrokerConfigLocation(servletContext);
        LOG.info("National Broker Config Location: [{}]", nationalBrokerConfigLocation);

        //configure logging
        configureLogging();

        // Load Spring web application configuration
        AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
        webAppContext.register(NationalBrokerWebAppConfig.class);
        webAppContext.setServletContext(servletContext);
        LOG.info("Registered NationalBroker web app context.");

        //Database initialization steps
        DatabaseConfiguration.configureDatabase(servletContext);

        registerDispatcherServlet(servletContext, webAppContext);


    }

    private void registerDispatcherServlet(ServletContext servletContext, AnnotationConfigWebApplicationContext webAppContext) {
        // Create and register the DispatcherServlet for National Broker
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
        LOG.info("Registered the dispatcher servlet for National Broker!");
    }

    private void configureLogging() {
        LoggingConfigurator loggingConfigurator = new LoggingConfigurator();
        loggingConfigurator.configureLogging();
    }

}
