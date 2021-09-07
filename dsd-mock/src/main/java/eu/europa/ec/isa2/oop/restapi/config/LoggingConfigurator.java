package eu.europa.ec.isa2.oop.restapi.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import eu.europa.ec.isa2.oop.dsd.property.DsdMockProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LoggingConfigurator {

    public static final String DEFAULT_LOGBACK_FILE_NAME = "logback.xml";

    private static final Logger LOG = LoggerFactory.getLogger(LoggingConfigurator.class);


    public void configureLogging() {
        String logbackConfigurationFile = getLoggingConfigurationFile();
        configureLogging(logbackConfigurationFile);
    }

    private String getLoggingConfigurationFile() {
        if (StringUtils.isBlank(DsdMockProperties.DSD_MOCK_CONFIG_LOCATION)) {
            return null;
        }
        return DsdMockProperties.DSD_MOCK_CONFIG_LOCATION + File.separator + LoggingConfigurator.DEFAULT_LOGBACK_FILE_NAME;
    }

    private void configureLogging(String logbackConfigurationFile) {
        if (StringUtils.isEmpty(logbackConfigurationFile)) {
            LOG.warn("Could not configure logging: the provided configuration file is empty");
            return;
        }

        LOG.info("Using the logback configuration file from [" + logbackConfigurationFile + "]");

        if (!new File(logbackConfigurationFile).exists()) {
            LOG.warn("Could not configure logging: the file [" + logbackConfigurationFile + "] does not exists");
            return;
        }

        configureLogback(logbackConfigurationFile);
    }

    private void configureLogback(String logbackConfigurationFile) {
        // assume SLF4J is bound to logback in the current environment
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            configurator.doConfigure(logbackConfigurationFile);
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
}
