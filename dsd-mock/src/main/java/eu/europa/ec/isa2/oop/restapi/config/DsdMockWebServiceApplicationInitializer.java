package eu.europa.ec.isa2.oop.restapi.config;


import eu.europa.ec.isa2.oop.dsd.jpa.DatabaseConfiguration;
import eu.europa.ec.isa2.oop.dsd.property.DsdMockConfigLocationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class DsdMockWebServiceApplicationInitializer implements WebApplicationInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(DsdMockWebServiceApplicationInitializer.class);


    @Override
    public void onStartup(ServletContext servletContext) {
        //lookup national broker config location
        String nationalBrokerConfigLocation = DsdMockConfigLocationProvider.loadNationalBrokerConfigLocation(servletContext);
        LOG.info("National Broker Config Location: [{}]", nationalBrokerConfigLocation);

        //configure logging
        configureLogging();

        // Load Spring web application configuration
        AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
        webAppContext.register(DsdMockWebAppConfig.class);
        webAppContext.setServletContext(servletContext);
        LOG.info("Registered DSD Mock web app context.");

        //Database h2 web-console initialization
        DatabaseConfiguration.configureDatabase(servletContext);

        registerDispatcherServlet(servletContext, webAppContext);
    }



    private void registerDispatcherServlet(ServletContext servletContext, AnnotationConfigWebApplicationContext webAppContext) {
        // Create and register the DispatcherServlet for National Broker
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
        registration.setMultipartConfig(new MultipartConfigElement("/", 2097152, 4193304, 2097152));
        LOG.info("Registered the dispatcher servlet for DSD Mock!");
    }

    private void configureLogging() {
        LoggingConfigurator loggingConfigurator = new LoggingConfigurator();
        loggingConfigurator.configureLogging();
    }


/*
    @Bean
    public StandardServletMultipartResolver filterMultipartResolver(){
           return new StandardServletMultipartResolver();
    }
*/
}
