package eu.europa.ec.isa2.oop.dsd.property;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * DsdMockConfigLocationProvider:
 */
public class DsdMockConfigLocationProvider {
    public static final Logger LOG = LoggerFactory.getLogger(DsdMockConfigLocationProvider.class);

    public static final String DSD_MOCK_CONFIG_DIRECTORY = "dsd" + File.separator + "config";

    /**
     * Path expected {@link DsdMockPropertyMetaDataManager#OOP_REST_API_PILOT_CONFIG_BASE_LOCATION}/{@link DsdMockPropertyMetaDataManager#DSD_MOCK_CONFIG_LOCATION}
     *
     * @param servletContext
     * @return
     */
    public static String loadNationalBrokerConfigLocation(ServletContext servletContext) {
        String oop_rest_api_pilot_config_base_location = getOOPRestAPIPilotConfigBaseLocation(servletContext);
        DsdMockProperties.DSD_MOCK_CONFIG_LOCATION = oop_rest_api_pilot_config_base_location + File.separator + DSD_MOCK_CONFIG_DIRECTORY;
        LOG.info("DSD Mock config Location : [{}]", DsdMockProperties.DSD_MOCK_CONFIG_LOCATION);

        return DsdMockProperties.DSD_MOCK_CONFIG_LOCATION;
    }

    /**
     * For the OOP Rest API pilot, in addition to DSD Mock, application for ROA and mock application for DSD is needed.
     * Hence creating a base config location folder from which other application specific configs will be retrieved.<br/>
     * Specify the path to the config base location folder using the environment variable: <b>-Doop.rest.api.pilot.config.base.loc</b><br/>
     * for example:
     * <ul>
     * <li>for wildfly in windows edit standalone.conf.bat to add <code>set "JAVA_OPTS=%JAVA_OPTS% -Doop.rest.api.pilot.config.base.loc=%JBOSS_HOME%\config"</code></li>
     * </ul>
     */
    private static String getOOPRestAPIPilotConfigBaseLocation(ServletContext servletContext) {
        String oopRestApiPilotConfigBaseLocationInitParameter = servletContext.getInitParameter(DsdMockPropertyMetaDataManager.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION);
        if (StringUtils.isNotBlank(oopRestApiPilotConfigBaseLocationInitParameter)) {
            LOG.debug("Property [{}] is configured as a servlet init parameter with [{}]", DsdMockPropertyMetaDataManager.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION, oopRestApiPilotConfigBaseLocationInitParameter);
            DsdMockProperties.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION = oopRestApiPilotConfigBaseLocationInitParameter;
            return oopRestApiPilotConfigBaseLocationInitParameter;
        }

        String oopRestApiPilotConfigBaseLocation = System.getProperty(DsdMockPropertyMetaDataManager.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION);
        if (StringUtils.isNotBlank(oopRestApiPilotConfigBaseLocation)) {
            LOG.debug("Property [{}] is configured as a system property with [{}]", DsdMockPropertyMetaDataManager.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION, oopRestApiPilotConfigBaseLocation);
            DsdMockProperties.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION = oopRestApiPilotConfigBaseLocation;
            return oopRestApiPilotConfigBaseLocation;
        }
        return null;
    }

}
