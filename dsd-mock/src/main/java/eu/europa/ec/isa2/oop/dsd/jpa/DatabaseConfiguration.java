package eu.europa.ec.isa2.oop.dsd.jpa;

import eu.europa.ec.isa2.oop.dsd.property.DsdMockProperties;
import eu.europa.ec.isa2.oop.dsd.property.DsdMockPropertyMetaDataManager;
import org.apache.commons.lang3.StringUtils;
import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;


@Service
public class DatabaseConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Autowired
    @Qualifier(DsdMockPropertyMetaDataManager.DSD_MOCK_JDBC_XA_DATA_SOURCE)
    public DataSource dataSource;

    @Autowired
    DsdMockProperties dsdMockProperties;


    @PostConstruct
    @DependsOn(DsdMockPropertyMetaDataManager.DSD_MOCK_JDBC_XA_DATA_SOURCE)
    public void initializeH2Database(){
        try(Connection connection = dataSource.getConnection();) {
            LOG.info("DB connection.getMetaData().getDatabaseProductName():[{}]", connection.getMetaData().getDatabaseProductName());
            LOG.info("DB connection.getMetaData().getDatabaseProductVersion():[{}]", connection.getMetaData().getDatabaseProductVersion());
            LOG.info("DB connection.getCatalog():[{}]", connection.getCatalog());
            LOG.info("DB connection.getMetaData().getURL():[{}]", connection.getMetaData().getURL());

            connection.setAutoCommit(true);
            LOG.info("Initializing DSDMock H2 database.");
            String databaseInitScript = dsdMockProperties.getDatabaseInitScript();
            if (!StringUtils.isBlank(databaseInitScript)) {
                File fInitScript = new File(databaseInitScript);
                if (!fInitScript.exists()) {
                    LOG.error("Initialize script [{}] does not exist!", databaseInitScript);
                    return;
                }
                LOG.info("Initialize with script [{}] !", databaseInitScript);
                ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
                rdp.addScript(new FileSystemResource(fInitScript));
                rdp.populate(connection);
            } else {
                LOG.info("No init script!");
            }

        } catch (SQLException throwables) {
            LOG.error("Error initializing H2 database tables.", throwables);
            throwables.printStackTrace();
        }
    }

    public static void configureDatabase(ServletContext servletContext) {
        configureH2Database(servletContext);
    }

    private static void configureH2Database(ServletContext servletContext) {
        LOG.info("Registering H2 DB Console Web Servlet");
        //Register the H2 Console Web Servlet
        WebServlet h2WebServlet = new WebServlet();
        ServletRegistration.Dynamic h2WebServletRegistration = servletContext.addServlet("h2DSDWebServlet", h2WebServlet);
        h2WebServletRegistration.addMapping("/h2_console/*");
        h2WebServletRegistration.setInitParameter("webAllowOthers", "true");
        h2WebServletRegistration.setInitParameter("trace", "true");
    }

}
