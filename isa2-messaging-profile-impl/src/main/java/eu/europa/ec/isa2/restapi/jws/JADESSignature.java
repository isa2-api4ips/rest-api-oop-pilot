package eu.europa.ec.isa2.restapi.jws;

import eu.europa.ec.isa2.restapi.profile.docsapi.exceptions.MessagingAPIException;
import eu.europa.ec.isa2.restapi.profile.enums.APIProblemType;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingParameterType;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.jades.HTTPHeader;
import eu.europa.esig.dss.jades.HTTPHeaderDigest;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.policy.ValidationPolicy;
import eu.europa.esig.dss.policy.jaxb.ConstraintsParameters;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class JADESSignature {
    private static final Logger LOG = LoggerFactory.getLogger(JADESSignature.class);
    final KeystoreFileConnection keystoreFileConnection;
    final CommonCertificateSource storeCertificateSource;


    public JADESSignature(KeystoreFileConnection keystoreFileConnection, CommonCertificateSource storeCertificateSource ) {
        this.keystoreFileConnection = keystoreFileConnection;
        this.storeCertificateSource = storeCertificateSource;
    }

    protected CertificateVerifier getOfflineCertificateVerifier() {
        CertificateVerifier cv = new CommonCertificateVerifier();
        cv.setDataLoader(new IgnoreDataLoader());

        return cv;
    }

    /**
     * Method generates detached JADES signature for Sign type http://uri.etsi.org/19182/HttpHeaders
     *
     * @param headersToSign
     * @param alias
     * @param addChain
     * @param signDigestAlgorithm
     * @return
     * @throws IOException
     */
    public String createDetachedSignature(List<DSSDocument> headersToSign, String alias, boolean addChain, DigestAlgorithm signDigestAlgorithm) throws IOException {

        CertificateToken signCertToken = keystoreFileConnection.getCertificateToken(alias);

        JAdESSignatureParameters signatureParameters = new JAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(Calendar.getInstance().getTime());
        signatureParameters.setSigningCertificate(signCertToken);
        if (addChain) {
            signatureParameters.setCertificateChain(keystoreFileConnection.getCertificateTokenChain(alias));
        } else {
            signatureParameters.setCertificateChain(signCertToken);
        }
        signatureParameters.setBase64UrlEncodedPayload(false);
        signatureParameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        signatureParameters.setSignatureLevel(SignatureLevel.JAdES_BASELINE_B);
        signatureParameters.setDigestAlgorithm(signDigestAlgorithm);
        signatureParameters.setSigDMechanism(SigDMechanism.HTTP_HEADERS);

        CertificateVerifier commonCertificateVerifier = getOfflineCertificateVerifier();
        JAdESService service = new JAdESService(commonCertificateVerifier);
        ToBeSigned dataToSign = service.getDataToSign(headersToSign, signatureParameters);

        SignatureValue signatureValue = keystoreFileConnection.sign(dataToSign, signatureParameters.getDigestAlgorithm(),
                signatureParameters.getMaskGenerationFunction(), keystoreFileConnection.getKey(alias));

        // We invoke the service to sign the document with the signature value obtained in
        // the previous step.
        DSSDocument signedDocument = service.signDocument(headersToSign, signatureParameters, signatureValue);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signedDocument.writeTo(baos);
        return new String(baos.toByteArray());
    }

    public SignedMimeMultipart createSignedMimeMultipartFromJson(Object jsonPayload, String alias, boolean addChain, DigestAlgorithm signDigestAlgorithm, Map<String, String> mapHeaders) throws MessagingException, IOException {

        SignedMimeMultipart multipartResponse = new SignedMimeMultipart();
        // set mime with boundary

        MimeBodyPart part1 = createSignedMultipartPayloadFromJson(jsonPayload, alias, addChain, signDigestAlgorithm);
        multipartResponse.addBodyPart(part1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        multipartResponse.writeTo(baos);
        // for the pilot project use InMemoryDocument. In case of further development consider
        // less memory intensive approach
        InMemoryDocument inMemoryDocument = new InMemoryDocument(baos.toByteArray());

        List<DSSDocument> headersToSign = new ArrayList<>();
        String contentType =normalizeHeader(multipartResponse.getContentType());
        headersToSign.add(new HTTPHeader(HttpHeaders.CONTENT_TYPE,contentType));
        headersToSign.add(new HTTPHeader(HttpHeaders.CONTENT_LENGTH,baos.toByteArray().length+""));

        LOG.info("add content type:" + contentType);
        headersToSign.add(new HTTPHeaderDigest(inMemoryDocument, signDigestAlgorithm));
        if ( mapHeaders != null && !mapHeaders.isEmpty()) {
            mapHeaders.forEach( (header, value) -> headersToSign.add(new HTTPHeader(header, value)));
        }

        // set digest
        String signValue = createDetachedSignature(headersToSign, alias, addChain, signDigestAlgorithm);
        multipartResponse.addHttpHeader(MessagingParameterType.EDEL_MESSAGE_SIG.getName(), signValue);
        for (DSSDocument header : headersToSign) {
            multipartResponse.addHttpHeader(header.getName(), ((HTTPHeader) header).getValue());
        }
        return multipartResponse;
    }

    public MimeBodyPart createSignedMultipartPayloadFromJson(Object payload, String alias, boolean addChain, DigestAlgorithm signDigestAlgorithm) throws MessagingException, IOException {

        JsonDssDocument jsonDssDocument = new JsonDssDocument(payload);
        List<DSSDocument> headersToSign = generatedHeadersFromJsonObject(jsonDssDocument, signDigestAlgorithm);

        MimeBodyPart part = new MimeBodyPart();
        part.addHeader(MessagingParameterType.EDEL_PAYLOAD_SIG.getName(), createDetachedSignature(headersToSign, alias, addChain, signDigestAlgorithm));
        part.setDataHandler(new DataHandler(jsonDssDocument));
        // add signed headers to payload
        for (DSSDocument header : headersToSign) {
            part.addHeader(header.getName(), ((HTTPHeader) header).getValue());
        }
        return part;
    }

    public List<DSSDocument> generatedHeadersFromJsonObject(JsonDssDocument payload, DigestAlgorithm signDigestAlgorithm) {
        return generatedHeadersFromJsonObject(payload, signDigestAlgorithm, MediaType.APPLICATION_JSON);
    }

    public List<DSSDocument> generatedHeadersFromJsonObject(JsonDssDocument payload, DigestAlgorithm signDigestAlgorithm, MediaType mimeType) {
        HTTPHeaderDigest httpHeaderDigest = new HTTPHeaderDigest(payload, signDigestAlgorithm);
        List<DSSDocument> headersToSign = new ArrayList<>();
        headersToSign.add(new HTTPHeader(HttpHeaders.CONTENT_DISPOSITION, "name=\"" + payload.getClass().getName() + "\"; filename=\"" + payload.getClass().getName() + ".json\""));
        headersToSign.add(new HTTPHeader(HttpHeaders.CONTENT_TYPE, mimeType==null?MediaType.APPLICATION_JSON_VALUE: mimeType.toString()));
        headersToSign.add(new HTTPHeader(HttpHeaders.CONTENT_LENGTH, payload.getSize()+""));
        headersToSign.add(new HTTPHeaderDigest(payload, signDigestAlgorithm));
        return headersToSign;
    }

    public Reports validateHeaderSignature(DSSDocument signature,   List<DSSDocument> allHeaders){
        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signature);

        validator.setDetachedContents(allHeaders);
        validator.setTokenExtractionStrategy(TokenExtractionStrategy.NONE);

        CertificateVerifier certificateVerifier = getOfflineCertificateVerifier();
        certificateVerifier.setTrustedCertSources(storeCertificateSource);
        validator.setCertificateVerifier(certificateVerifier);

        // because of XML parse dependency conflicts - load validation policy manually.
        // This is OK for Pilot project - in case of production fix DSS dependency issues...
        ValidationPolicy policy = getValidationPolicy();
        return validator.validateDocument(policy);
    }

    public ValidationPolicy getValidationPolicy()  {
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance( "eu.europa.esig.dss.policy.jaxb");
            Unmarshaller u = jc.createUnmarshaller();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(JADESSignature.class.getResourceAsStream("/policy/constraint.xml"));
            Node fooSubtree = doc.getFirstChild();
            JAXBElement<ConstraintsParameters> account = u.unmarshal( fooSubtree, ConstraintsParameters.class);
            return new EtsiValidationPolicy(account.getValue());
        } catch (JAXBException | ParserConfigurationException | SAXException | IOException e) {
            throw new MessagingAPIException(APIProblemType.INTERNAL_SERVER_ERROR, "Error occurred while reading ConstraintsParameters", null, e);
        }

    }
    public String normalizeHeader(String value){
        return StringUtils.isBlank(value)?"":value.trim().replaceAll("\\s+", " ");
    }
}
