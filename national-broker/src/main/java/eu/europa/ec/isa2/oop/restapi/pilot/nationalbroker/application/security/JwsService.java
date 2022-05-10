package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerProperties;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.OriginalSenderTokenSimplePayloadRO;
import eu.europa.ec.isa2.restapi.jws.*;
import eu.europa.ec.isa2.restapi.profile.docsapi.exceptions.MessagingAPIException;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.jades.HTTPHeader;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class JwsService implements KeystoreDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(JwsService.class);
    NationalBrokerProperties nationalBrokerProperties;

    KeystoreFileConnection keystoreFileConnection;
    TruststoreFileConnection truststoreFileConnection;
    JADESSignature jadesSignature;

    @Autowired
    public JwsService(NationalBrokerProperties nationalBrokerProperties) {
        this.nationalBrokerProperties = nationalBrokerProperties;
    }

    @PostConstruct
    protected void init() {
        keystoreFileConnection = new KeystoreFileConnection(this);
        truststoreFileConnection = new TruststoreFileConnection(nationalBrokerProperties);
        jadesSignature = new JADESSignature(keystoreFileConnection, truststoreFileConnection);
    }


    public String signMessageHeaders(List<DSSDocument> headersToSign) throws IOException {

        String alias = nationalBrokerProperties.getSignatureKeyAlias();
        DigestAlgorithm algorithm = DigestAlgorithm.forName(nationalBrokerProperties.getPayloadDigestAlgorithm());
        return jadesSignature.createDetachedSignature(headersToSign, alias, false, algorithm);
    }

    @Override
    public String getLocation() {
        return nationalBrokerProperties.getKeystoreLocation();
    }

    @Override
    public String getType() {
        return nationalBrokerProperties.getKeystoreType();
    }

    @Override
    public char[] getCredentials() {
        return nationalBrokerProperties.getKeystoreCredentials().toCharArray();
    }

    @Override
    public KeyStore.ProtectionParameter getKeyCredentials(String alias) {
        return new KeyStore.PasswordProtection(nationalBrokerProperties.getSignatureKeyCredentials().toCharArray());
    }

    public String createOriginalSenderToken(String originalSender){
        JsonDssDocument inMemoryDocument = new JsonDssDocument(new OriginalSenderTokenSimplePayloadRO(originalSender));
        String alias = nationalBrokerProperties.getSignatureKeyAlias();
        DigestAlgorithm algorithm = DigestAlgorithm.forName(nationalBrokerProperties.getPayloadDigestAlgorithm());
        try {
            return jadesSignature.createCompactSignature(inMemoryDocument,alias, false, algorithm);
        } catch (IOException e) {
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Error occurred while generating original sender token!", null, e);
        }
    }

    public void signJsonResponse(Object json, HttpServletResponse response) {
        try (OutputStream sos = response.getOutputStream()) {
            Map<String, String>  headers = signAndWriteJsonResponse(json, sos, MediaType.APPLICATION_JSON, null);
            headers.forEach((header, value) -> response.addHeader(header, value) );
        } catch (IOException e) {
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Error occurred while reading signing the JSON object", null, e);
        }
    }

    public Map<String, String> signAndWriteJsonResponse(Object json, OutputStream outputStream, MediaType mimeType, Map<String, String> headers ) {

        DigestAlgorithm algorithm = DigestAlgorithm.forName(nationalBrokerProperties.getPayloadDigestAlgorithm());
        JsonDssDocument inMemoryDocument = new JsonDssDocument(json);
        List<DSSDocument> headersToSign = jadesSignature.generatedHeadersFromJsonObject(inMemoryDocument, algorithm, mimeType, false);
        if (headers!=null) {
            headers.forEach( (key, val) -> {
                headersToSign.add(new HTTPHeader(key, val));
            });
        }
        String signature;
        try {
            signature = signMessageHeaders(headersToSign);
        } catch (IOException e) {
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Error occurred while signing the JSON object", null, e);
        }

        try {
            InputStream inputStream = inMemoryDocument.getInputStream();
            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }
        } catch (IOException e) {
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Error occurred while streaming the JSON object!", null, e);
        }
        Map<String, String> signedHeaders = new HashMap<>();
        signedHeaders.put(MessagingParameterType.EDEL_MESSAGE_SIG.getName(), signature);
        headersToSign.forEach(header -> signedHeaders.put(header.getName(), ((HTTPHeader) header).getValue()));
        return signedHeaders;
    }

    /**
     * This signature check signed header. Digest must be validated before this method
     *
     * @param messageHeaders
     */
    public String getValidationSignatureXMLReportFromHeaders(Map<String, String> messageHeaders) {
        LOG.info("getValidationSignatureXMLReportFromHeaders");

        if (!messageHeaders.containsKey(MessagingParameterType.EDEL_MESSAGE_SIG.getName())) {
            return "<DetailedReport >Message does not have header: Edel-Message-Sig! The signature can not be validated!</DetailedReport>";
        }


        InMemoryDocument signature = null;
        List<DSSDocument> headersToSign = new ArrayList<>();
        Iterator<String> iterator = messageHeaders.keySet().iterator();
        while (iterator.hasNext()) {
            String headerName = iterator.next();
            String value = messageHeaders.get(headerName);
            if (StringUtils.equalsIgnoreCase(MessagingParameterType.EDEL_MESSAGE_SIG.getName(), headerName)) {
                LOG.info("validate signature: [{}]", value);
                signature = new InMemoryDocument(value.getBytes());
            } else {
                LOG.info("validate signature header:[{}] --> [{}]", headerName, value);
                headersToSign.add(new HTTPHeader(headerName, value));
            }
        }
        Reports report = jadesSignature.validateHeaderSignature(signature, headersToSign);
        return report.getXmlDetailedReport();
    }

    public SignedMimeMultipart createSignedMimeMultipartFromJson(Object payload, Map<String, String> mapHeaders) throws MessagingException, IOException {
        String alias = nationalBrokerProperties.getSignatureKeyAlias();
        DigestAlgorithm algorithm = DigestAlgorithm.forName(nationalBrokerProperties.getPayloadDigestAlgorithm());
        return jadesSignature.createSignedMimeMultipartFromJson(payload, alias, false, algorithm, mapHeaders);
    }

}
