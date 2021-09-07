package eu.europa.ec.isa2.restapi.reader;

import eu.europa.ec.isa2.restapi.profile.annotation.SubmitMessageOperation;
import eu.europa.ec.isa2.restapi.profile.annotation.MultipartPayload;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;
import org.springframework.http.MediaType;

import javax.ws.rs.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MessagingReaderTest {

    @Path("/v1/messaging/")
    interface MessageSumbissionTestApi {

        @SubmitMessageOperation(service = "organization", action = "search", sync = true,
                tags = {"DSD Organization"},
                operationId = "organizationSearchMethodId",
                summary = "DSD Mock: REST Interface to query mock organizations",
                description = "The Message Submission with Asynchronous Response of list of organizations.",
                requestPayloads = {
                        @MultipartPayload(
                                name = "search-parameter",
                                contentType = MediaType.APPLICATION_JSON_VALUE,
                                instance = TestOrganizationSearchParameters.class,
                                example = "{\n" +
                                        "  \"country\": \"BE\",\n" +
                                        "  \"name\": \"Company name\",\n" +
                                        "  \"limit\": 100,\n" +
                                        "  \"offset\": 5,\n" +
                                        "  \"sort\": \"+name,-country\"\n" +
                                        "}"
                        )
                })

        default List<TestOrganizationSearchParameters> searchOrganizationList(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of search parameters", required = true)
                                                                                      TestOrganizationSearchParameters organization) {
            return Collections.emptyList();
        }


        class TestOrganizationSearchParameters {
            String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

    @Test
    public void testReadMessageSubmissionOperation() {
        MessagingReader reader = new MessagingReader();
        OpenAPI result = reader.read(MessageSumbissionTestApi.class);

        assertNotNull(result);
        assertNotNull(result.getPaths());
        assertEquals(1, result.getPaths().size());
        assertTrue(result.getPaths().containsKey("/v1/messaging/organization/search/{messageId}/sync"));


    }
}