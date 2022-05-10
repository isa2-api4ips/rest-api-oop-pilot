package eu.europa.ec.isa2.restapi.reader;

import eu.europa.ec.isa2.restapi.profile.annotation.MultipartPayload;
import eu.europa.ec.isa2.restapi.profile.annotation.SubmitMessageOperation;
import eu.europa.ec.isa2.restapi.profile.docsapi.MessageSubmissionEndpointAPI;
import eu.europa.ec.isa2.restapi.profile.docsapi.ResponseMessageSubmissionEndpointAPI;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

import javax.ws.rs.Path;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Ignore
public class MessagingReaderTest {

    @Path("/v1/messaging/")
    interface MessageSubmissionTestApi {

        @SubmitMessageOperation(service = "organization", action = "search", sync = true,
                tags = {"DSD Organization"},
                operationId = "organizationSearchMethodId",
                summary = "DSD Mock: REST Interface to query mock organizations",
                description = "The Message Submission with Asynchronous Response of list of organizations",
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
        OpenAPI result = reader.read(MessageSubmissionTestApi.class);

        assertNotNull(result);
        assertNotNull(result.getPaths());
        assertEquals(1, result.getPaths().size());
        assertTrue(result.getPaths().containsKey("/v1/messaging/organization/search/{messageId}/sync"));
    }

    @Test
    public void testReadMessageSubmissionHeadersOperation() {
        // when
        String path = "/v1/messaging/{service}/{action}/{messageId}";
        // request parameters
        List<MessagingParameterType> requestMsgParam = Arrays.asList(MessagingParameterType.SERVICE,
                MessagingParameterType.ACTION,
                MessagingParameterType.MESSAGE_ID,
                MessagingParameterType.ORIGINAL_SENDER,
                MessagingParameterType.FINAL_RECIPIENT,
                MessagingParameterType.TIMESTAMP,
                MessagingParameterType.EDEL_MESSAGE_SIG,
                MessagingParameterType.RESPONSE_WEBHOOK,
                MessagingParameterType.SIGNAL_WEBHOOK
        );

        List<String> responseCodes = Arrays.asList("202", "400", "500");

        MessagingReader reader = new MessagingReader(MessagingAPIDefinitionsLocation.INLINE, null, "v1", new Properties());
        OpenAPI result = reader.read(MessageSubmissionEndpointAPI.class);

        // then
        assertTrue(result.getPaths().containsKey(path));
        PathItem pathItem = result.getPaths().get(path);
        Operation operation = pathItem.getPost();
        assertNotNull(operation);
        Map<String, Parameter> stringParameterMap = operation.getParameters().stream().collect(Collectors.toMap(prm -> prm.getName(),prm->prm));
        requestMsgParam.forEach(param -> {
                    assertTrue("Must have parameter: " + param.name(), stringParameterMap.containsKey(param.getName()));
                    // just to make sure we have the right ORIGINAL_SENDER and FINAL_RECIPIENT
                    assertEquals( param.getDescription(), stringParameterMap.get(param.getName()).getDescription());
                }
        );        // response parameters
        assertEquals(responseCodes.size(), operation.getResponses().size());
        responseCodes.forEach(respondCode ->
                {
                    ApiResponse rsp = operation.getResponses().get(respondCode);
                    // assert has onnly one header
                    assertEquals(3, rsp.getHeaders().size());
                    assertNotNull(rsp.getHeaders().get("Edel-Message-Sig"));

                    assertNotNull("Must have response: " + respondCode, rsp);
                    Content content = rsp.getContent();
                    assertNotNull(content);
                    // all signal has type problem+json
                    io.swagger.v3.oas.models.media.MediaType mediaType = content.get("application/problem+json");
                    assertNotNull(mediaType);
                }
        );
    }

    @Test
    public void testReadMessageSubmissionSyncHeadersOperation() {
        // when
        String path = "/v1/messaging/{service}/{action}/{messageId}/sync";
        // request parameters
        List<MessagingParameterType> requestMsgParam = Arrays.asList(MessagingParameterType.SERVICE,
                MessagingParameterType.ACTION,
                MessagingParameterType.MESSAGE_ID,
                MessagingParameterType.ORIGINAL_SENDER,
                MessagingParameterType.FINAL_RECIPIENT,
                MessagingParameterType.TIMESTAMP,
                MessagingParameterType.EDEL_MESSAGE_SIG
        );

        List<String> responseCodes = Arrays.asList("202", "400", "500");

        MessagingReader reader = new MessagingReader(MessagingAPIDefinitionsLocation.INLINE, null, "v1", new Properties());
        OpenAPI result = reader.read(MessageSubmissionEndpointAPI.class);

        // then
        assertTrue(result.getPaths().containsKey(path));
        PathItem pathItem = result.getPaths().get(path);
        Operation operation = pathItem.getPost();
        assertNotNull(operation);
        Map<String, Parameter> stringParameterMap = operation.getParameters().stream().collect(Collectors.toMap(prm -> prm.getName(),prm->prm));
        requestMsgParam.forEach(param -> {
                    assertTrue("Must have parameter: " + param.name(), stringParameterMap.containsKey(param.getName()));
                    // just to make sure we have the right ORIGINAL_SENDER and FINAL_RECIPIENT
                    assertEquals( param.getDescription(), stringParameterMap.get(param.getName()).getDescription());
                }
        );
        // response parameters
        assertEquals(responseCodes.size(), operation.getResponses().size());
        responseCodes.forEach(respondCode ->
                {
                    ApiResponse rsp = operation.getResponses().get(respondCode);
                    // assert has only one header
                    assertEquals(3, rsp.getHeaders().size());
                    assertNotNull(rsp.getHeaders().get("Edel-Message-Sig"));

                    assertNotNull("Must have response: " + respondCode, rsp);
                    Content content = rsp.getContent();
                    assertNotNull(content);
                }
        );
    }

    @Test
    @Ignore
    public void testReadResponseMessageSubmissionHeadersOperation() {
        // when
        String path = "/v1/messaging/{service}/{action}/{messageId}/response/{rService}/{rAction}/{rMessageId}";
        // request parameters
        //List<String> requestParameters = Arrays.asList("service","action","messageId","rService","rAction","rMessageId","Original-Sender","Final-Recipient","Timestamp","Edel-Message-Sig");
        List<MessagingParameterType> requestMsgParam = Arrays.asList(MessagingParameterType.SERVICE,
                MessagingParameterType.ACTION,
                MessagingParameterType.MESSAGE_ID,
                MessagingParameterType.RESPONSE_SERVICE,
                MessagingParameterType.RESPONSE_ACTION,
                MessagingParameterType.RESPONSE_MESSAGE_ID,
                MessagingParameterType.ORIGINAL_SENDER,
                MessagingParameterType.ORIGINAL_SENDER_TOKEN,
                MessagingParameterType.FINAL_RECIPIENT,
                MessagingParameterType.TIMESTAMP,
                MessagingParameterType.EDEL_MESSAGE_SIG
        );
        List<String> responseCodes = Arrays.asList("202", "400", "500");

        MessagingReader reader = new MessagingReader(MessagingAPIDefinitionsLocation.INLINE, null, "v1", new Properties());
        OpenAPI result = reader.read(ResponseMessageSubmissionEndpointAPI.class);

        // then
        assertTrue(result.getPaths().containsKey(path));
        PathItem pathItem = result.getPaths().get(path);
        Operation operation = pathItem.getPost();
        assertNotNull(operation);
        assertEquals(requestMsgParam.size(), operation.getParameters().size());
        // request parameters
        Map<String, Parameter> stringParameterMap = operation.getParameters().stream().collect(Collectors.toMap(prm -> prm.getName(),prm->prm));
        requestMsgParam.forEach(param -> {
                    assertTrue("Must have parameter: " + param.name(), stringParameterMap.containsKey(param.getName()));
                    // just to make sure we have the right ORIGINAL_SENDER and FINAL_RECIPIENT
                    assertEquals( param.getDescription(), stringParameterMap.get(param.getName()).getDescription());
                }
        );
        // response parameters
        assertEquals(responseCodes.size(), operation.getResponses().size());
        responseCodes.forEach(respondCode ->
                {
                    ApiResponse rsp = operation.getResponses().get(respondCode);
                    // assert has onnly one header
                    assertEquals(1, rsp.getHeaders().size());
                    assertNotNull(rsp.getHeaders().get("Edel-Message-Sig"));

                    assertNotNull("Must have response: " + respondCode, rsp);
                    Content content = rsp.getContent();
                    assertNotNull(content);
                    // all signal has type problem+json
                    io.swagger.v3.oas.models.media.MediaType mediaType = content.get("application/problem+json");
                    assertNotNull(mediaType);
                }
        );
    }

    @Test
    public void testReadWebhookMessageSubmissionOperation() {
        // when
        String path = "/messaging-webhook/{messageId}/response/{rService}/{rAction}/{rMessageId}";
        // request parameters
        //List<String> requestParameters = Arrays.asList("service","action","messageId","rService","rAction","rMessageId","Original-Sender","Final-Recipient","Timestamp","Edel-Message-Sig");
        List<MessagingParameterType> requestMsgParam = Arrays.asList(
                MessagingParameterType.MESSAGE_ID,
                MessagingParameterType.RESPONSE_SERVICE,
                MessagingParameterType.RESPONSE_ACTION,
                MessagingParameterType.RESPONSE_MESSAGE_ID,
                MessagingParameterType.ORIGINAL_SENDER,
                MessagingParameterType.ORIGINAL_SENDER_TOKEN,
                MessagingParameterType.FINAL_RECIPIENT,
                MessagingParameterType.TIMESTAMP,
                MessagingParameterType.EDEL_MESSAGE_SIG
        );
        List<String> responseCodes = Arrays.asList("202", "400", "500");

        MessagingReader reader = new MessagingReader(MessagingAPIDefinitionsLocation.INLINE, null, "v1", new Properties());
        OpenAPI result = reader.read(ResponseMessageSubmissionEndpointAPI.class);

        // then
        assertTrue(result.getWebhooks().containsKey(path));
        PathItem pathItem = result.getWebhooks().get(path);
        Operation operation = pathItem.getPost();
        assertNotNull(operation);
        assertEquals(requestMsgParam.size(), operation.getParameters().size());
        // request parameters
        Map<String, Parameter> stringParameterMap = operation.getParameters().stream().collect(Collectors.toMap(prm -> prm.getName(),prm->prm));
        requestMsgParam.forEach(param -> {
                    assertTrue("Must have parameter: " + param.name(), stringParameterMap.containsKey(param.getName()));
                    // just to make sure we have the right ORIGINAL_SENDER and FINAL_RECIPIENT
                    assertEquals( param.getDescription(), stringParameterMap.get(param.getName()).getDescription());
                }
        );
        // response parameters
        assertEquals(responseCodes.size(), operation.getResponses().size());
        responseCodes.forEach(respondCode ->
                {
                    ApiResponse rsp = operation.getResponses().get(respondCode);
                    // assert has onnly one header
                    assertEquals(1, rsp.getHeaders().size());
                    assertNotNull(rsp.getHeaders().get("Edel-Message-Sig"));

                    assertNotNull("Must have response: " + respondCode, rsp);
                    Content content = rsp.getContent();
                    assertNotNull(content);
                    // all signal has type problem+json
                    io.swagger.v3.oas.models.media.MediaType mediaType = content.get("application/problem+json");
                    assertNotNull(mediaType);
                }
        );
    }
}