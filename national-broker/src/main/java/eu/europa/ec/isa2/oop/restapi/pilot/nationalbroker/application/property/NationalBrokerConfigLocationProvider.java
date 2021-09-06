package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * For the REST API Pilot, directory structure is expected as:
 * config/
 *    |-> national_broker
 *    |-> roa_mock
 *    |-> dsd_mock
 * This method will return the path to the national_broker directory
 */
public class NationalBrokerConfigLocationProvider {
    public static final Logger LOG = LoggerFactory.getLogger(NationalBrokerConfigLocationProvider.class);

    public static final String NATIONAL_BROKER_CONFIG_DIRECTORY = "national_broker";

    /**
     * Path expected {@link NationalBrokerPropertyMetaDataManager#OOP_REST_API_PILOT_CONFIG_BASE_LOCATION}/{@link NationalBrokerPropertyMetaDataManager#NATIONAL_BROKER_CONFIG_LOCATION}
     * @param servletContext
     * @return
     */
    public static String loadNationalBrokerConfigLocation(ServletContext servletContext) {
        String oop_rest_api_pilot_config_base_location = getOOPRestAPIPilotConfigBaseLocation(servletContext);
        NationalBrokerProperties.NATIONAL_BROKER_CONFIG_LOCATION = oop_rest_api_pilot_config_base_location + File.separator + NATIONAL_BROKER_CONFIG_DIRECTORY;
        LOG.info("National Broker config Location : [{}]", NationalBrokerProperties.NATIONAL_BROKER_CONFIG_LOCATION);

        return NationalBrokerProperties.NATIONAL_BROKER_CONFIG_LOCATION;
    }

    /**
     * For the OOP Rest API pilot, in addition to national broker, mock application for ROA and mock application for DSD is needed.
     * Hence creating a base config location folder from which other application specific configs will be retrieved.<br/>
     * Specify the path to the config base location folder using the environment variable: <b>-Doop.rest.api.pilot.config.base.loc</b><br/>
     * for example:
     * <ul>
     * <li>for wildfly in windows edit standalone.conf.bat to add <code>set "JAVA_OPTS=%JAVA_OPTS% -Doop.rest.api.pilot.config.base.loc=%JBOSS_HOME%\config"</code></li>
     * </ul>
     */
    private static String getOOPRestAPIPilotConfigBaseLocation(ServletContext servletContext) {
        String oopRestApiPilotConfigBaseLocationInitParameter = servletContext.getInitParameter(NationalBrokerPropertyMetaDataManager.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION);
        if (StringUtils.isNotBlank(oopRestApiPilotConfigBaseLocationInitParameter)) {
            LOG.debug("Property [{}] is configured as a servlet init parameter with [{}]", NationalBrokerPropertyMetaDataManager.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION, oopRestApiPilotConfigBaseLocationInitParameter);
            NationalBrokerProperties.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION = oopRestApiPilotConfigBaseLocationInitParameter;
            return oopRestApiPilotConfigBaseLocationInitParameter;
        }

        String oopRestApiPilotConfigBaseLocation = System.getProperty(NationalBrokerPropertyMetaDataManager.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION);
        if (StringUtils.isNotBlank(oopRestApiPilotConfigBaseLocation)) {
            LOG.debug("Property [{}] is configured as a system property with [{}]", NationalBrokerPropertyMetaDataManager.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION, oopRestApiPilotConfigBaseLocation);
            NationalBrokerProperties.OOP_REST_API_PILOT_CONFIG_BASE_LOCATION = oopRestApiPilotConfigBaseLocation;
            return oopRestApiPilotConfigBaseLocation;
        }
        return null;
    }

}
