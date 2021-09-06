package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.messaging;

import eu.europa.eu.dsd.messaging.gen.organization.model.OrganizationStatusResult;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

@Ignore
public class DSDOrganizationMessagingServiceTest {

    DSDOrganizationMessagingService messagingService = new DSDOrganizationMessagingService(null, null, null, null, null);

    @Before
    public void setUp() throws Exception {
        messagingService.init();
    }

    @Test
    public void getStatusMessage() {
        OrganizationStatusResult result = messagingService.getStatusMessage("43423dca-235c-440c-b711-decc0a90e387");
        assertNotNull(result);
        assertNotNull(result.getObject());
    }
}