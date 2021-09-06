package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.cron;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.DSDDataUpdateDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.OrganizationDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.DSDDatasetMessagingService;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging.DSDOrganizationMessagingService;
import eu.europa.eu.dsd.messaging.gen.dataset.model.DatasetStatusResult;
import eu.europa.eu.dsd.messaging.gen.organization.model.OrganizationStatusResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DSDMessageUpdateStatusScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(DSDMessageUpdateStatusScheduler.class);

    DSDOrganizationMessagingService dsdOrganizationMessagingService;
    DSDDatasetMessagingService dsdDatasetMessagingService;
    OrganizationDao organizationDao;
    DSDDataUpdateDao dsdDataUpdateDao;
    NationalBrokerProperties nationalBrokerProperties;

    @Autowired
    public DSDMessageUpdateStatusScheduler(DSDOrganizationMessagingService dsdOrganizationMessagingService,
                                           DSDDatasetMessagingService dsdDatasetMessagingService,
                                           OrganizationDao organizationDao,
                                           DSDDataUpdateDao dsdDataUpdateDao,
                                           NationalBrokerProperties nationalBrokerProperties) {
        this.dsdOrganizationMessagingService = dsdOrganizationMessagingService;
        this.dsdDatasetMessagingService = dsdDatasetMessagingService;
        this.organizationDao = organizationDao;
        this.dsdDataUpdateDao = dsdDataUpdateDao;
        this.nationalBrokerProperties = nationalBrokerProperties;
    }

    @Scheduled(cron = "15 2/1 * * * ?")
    public void retrieveStatusForOrganizationUpdates() {
        LOG.info("retrieveStatusForOrganizationUpdates");
        // get messages
        List<String> messageIds = dsdOrganizationMessagingService.getReadyStatusMessageIds();
        LOG.info("got message ids: [{}]", String.join(",", messageIds));
// read messages
        for (String s : messageIds) {
            OrganizationStatusResult result = dsdOrganizationMessagingService.getStatusMessage(s);
            updateStatus(result, s);
        }
        // read messages
    }

    @Scheduled(cron = "15 1/1 * * * ?")
    public void retrieveStatusForDatasetUpdates() {
        LOG.info("retrieveStatusForDatasetUpdates");
        // get messages
        List<String> messageIds = dsdDatasetMessagingService.getReadyStatusMessageIds();
        LOG.info("got message ids: [{}]", String.join(",", messageIds));
// read messages
        for (String s : messageIds) {

            DatasetStatusResult result = dsdDatasetMessagingService.getStatusMessage(s);
            updateDatasetStatus(result, s);
        }
        // read messages
    }

    public void updateDatasetStatus(DatasetStatusResult result, String messageId) {
        // update value to database
        if (result.getObject()==null) {
            LOG.warn("Got null result");
            return;
        }
        dsdDataUpdateDao.updateStatusToRequest(result.getStatus(), result.getDescription(), result.getRefMessage(), messageId);
        // update value to server - there is only one signal
        dsdOrganizationMessagingService.notifyMessageReceived(messageId, nationalBrokerProperties.getApplicationOriginalSender());

    }

    public void updateStatus(OrganizationStatusResult result, String messageId) {
        // update value to database
        if (result.getObject()==null) {
            LOG.warn("Got null result");
            return;
        }
        organizationDao.setStatusToOrganization(result.getObject().getIdentifier(), result.getStatus(), result.getDescription(), result.getRefMessage(), messageId);
        // update value to server
        dsdOrganizationMessagingService.notifyMessageReceived(messageId, nationalBrokerProperties.getApplicationOriginalSender());

    }
}
