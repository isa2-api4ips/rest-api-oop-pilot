package eu.europa.ec.isa2.oop.restapi.controller.profile.controllers;

import eu.europa.ec.isa2.oop.dsd.property.DsdMockProperties;
import eu.europa.ec.isa2.restapi.jws.*;
import eu.europa.ec.isa2.restapi.profile.docsapi.exceptions.MessagingAPIException;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.TokenExtractionStrategy;
import eu.europa.esig.dss.jades.HTTPHeader;
import eu.europa.esig.dss.jades.HTTPHeaderDigest;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.SignaturePolicyProvider;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwsService implements KeystoreDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(JwsService.class);
    DsdMockProperties dsdMockProperties;

    KeystoreFileConnection keystoreFileConnection;
    TruststoreFileConnection truststoreFileConnection;
    JADESSignature jadesSignature;

    @Autowired
    public JwsService(DsdMockProperties dsdMockProperties) {
        this.dsdMockProperties = dsdMockProperties;
    }

    @PostConstruct
    protected void init() {
        keystoreFileConnection = new KeystoreFileConnection(this);
        truststoreFileConnection = new TruststoreFileConnection(dsdMockProperties);
        jadesSignature = new JADESSignature(keystoreFileConnection, truststoreFileConnection);
    }

    public void signJsonResponse(Object json, HttpServletResponse response, MediaType type) {
        try (OutputStream sos = response.getOutputStream()) {
            Map<String, String>  headers = signAndWriteJsonResponse(json, sos, type);
            headers.forEach((header, value) -> response.addHeader(header, value) );
        } catch (IOException e) {
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Error occurred while reading signing the JSON object", null, e);
        }
    }

    public String createOriginalSenderToken(String originalSender){
        JsonDssDocument inMemoryDocument = new JsonDssDocument(new OriginalSenderTokenSimplePayload(originalSender));
        String alias = dsdMockProperties.getSignatureKeyAlias();
        DigestAlgorithm algorithm = DigestAlgorithm.forName(dsdMockProperties.getPayloadDigestAlgorithm());
        try {
            return jadesSignature.createCompactSignature(inMemoryDocument,alias, false, algorithm);
        } catch (IOException e) {
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Error occurred while generating original sender token!", null, e);
        }
    }

    public Map<String, String> signAndWriteJsonResponse(Object json, OutputStream outputStream, MediaType mimeType) {

        DigestAlgorithm algorithm = DigestAlgorithm.forName(dsdMockProperties.getPayloadDigestAlgorithm());
        JsonDssDocument inMemoryDocument = new JsonDssDocument(json);
        List<DSSDocument> headersToSign = jadesSignature.generatedHeadersFromJsonObject(inMemoryDocument, algorithm, mimeType, false);

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
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Error occurred while writing the JSON object to out stream", null, e);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put(MessagingParameterType.EDEL_MESSAGE_SIG.getName(), signature);
        headers.put(MessagingParameterType.TIMESTAMP.getName(), LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        headersToSign.forEach(header -> headers.put(header.getName(), ((HTTPHeader) header).getValue()));
        return headers;

    }

    public String signMessageHeaders(List<DSSDocument> headersToSign) throws IOException {

        String alias = dsdMockProperties.getSignatureKeyAlias();
        DigestAlgorithm algorithm = DigestAlgorithm.forName(dsdMockProperties.getPayloadDigestAlgorithm());
        return jadesSignature.createDetachedSignature(headersToSign,alias, false, algorithm);
    }

    public MimeBodyPart generateSignedMultipartPayloadFromJson(Object payload) throws MessagingException, IOException {
        String alias = dsdMockProperties.getSignatureKeyAlias();
        DigestAlgorithm algorithm = DigestAlgorithm.forName(dsdMockProperties.getPayloadDigestAlgorithm());
        return jadesSignature.createSignedMultipartPayloadFromJson(payload, alias, false, algorithm);
    }

    public SignedMimeMultipart createSignedMimeMultipartFromJson(Object payload) throws MessagingException, IOException {
        String alias = dsdMockProperties.getSignatureKeyAlias();
        DigestAlgorithm algorithm = DigestAlgorithm.forName(dsdMockProperties.getPayloadDigestAlgorithm());
        return jadesSignature.createSignedMimeMultipartFromJson(payload, alias, false, algorithm, null);
    }

    public void validateSignature(String signatureValue, List<DSSDocument> headersToSign ){

        // validated if payload digest matches header digest.
        LOG.debug("Validate signature: [{}]", signatureValue);
        boolean securityEnabled = dsdMockProperties.isMessagingSecurityEnabled();
        DSSDocument signDoc = new InMemoryDocument(signatureValue.getBytes());


        Reports reports = jadesSignature.validateHeaderSignature(signDoc, headersToSign);
        SimpleReport simpleReport = reports.getSimpleReport();
        if (simpleReport.getSignaturesCount()<0) {
            LOG.error("No signature found");
            if (securityEnabled) {
                throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "No signature found", null);
            }
        }
        List<String> signatureIdList = simpleReport.getSignatureIdList();
        for (String signatureId: signatureIdList) {
            if (!simpleReport.isValid(signatureId)){
                LOG.error("Signature: [{}] is not valid", signatureId);
                LOG.error("----------------Validation report---------------");
                LOG.error(reports.getXmlDetailedReport());
                if (securityEnabled) {
                    throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Signature: ["+signatureId+"] is not valid", null);
                }
            }
        }
    }



    @Override
    public String getLocation() {
        return dsdMockProperties.getKeystoreLocation();
    }

    @Override
    public String getType() {
        return dsdMockProperties.getKeystoreType();
    }

    @Override
    public char[] getCredentials() {
        return dsdMockProperties.getKeystoreCredentials().toCharArray();
    }

    @Override
    public KeyStore.ProtectionParameter getKeyCredentials(String alias) {
        return new KeyStore.PasswordProtection(dsdMockProperties.getSignatureKeyCredentials().toCharArray());
    }

}
