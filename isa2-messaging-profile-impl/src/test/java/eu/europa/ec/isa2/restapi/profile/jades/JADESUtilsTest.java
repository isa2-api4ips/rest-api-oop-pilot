package eu.europa.ec.isa2.restapi.profile.jades;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.jades.HTTPHeader;
import eu.europa.esig.dss.jades.HTTPHeaderDigest;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.model.*;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.token.AbstractKeyStoreTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignaturePolicyProvider;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JADESUtilsTest {

    public static final String KEYSTORE_TYPE ="PKCS12";
    String pass = "test123";
    String alias = "dsd-mock";
    String keystoreResourcePath="/keystore/dsd-keystore.p12";
    //String alias = "restapi-isa2-pilot";


    private DSSDocument originalDocument;

    protected CertificateVerifier getOfflineCertificateVerifier() {
        CertificateVerifier cv = new CommonCertificateVerifier();
        cv.setDataLoader(new IgnoreDataLoader());
        //cv.setTrustedCertSources(getTrustedCertificateSource());
        return cv;
    }
    protected CertificateSource getTrustedCertificateSource() {
        CommonTrustedCertificateSource trusted = new CommonTrustedCertificateSource();
        trusted.importAsTrusted(getTrustAnchors());
        return trusted;
    }
    private KeyStoreCertificateSource getTrustAnchors() {
        return new KeyStoreCertificateSource(JADESUtilsTest.class.getResourceAsStream(keystoreResourcePath), KEYSTORE_TYPE, pass);
    }

    protected AbstractKeyStoreTokenConnection getToken() {
        return new KeyStoreSignatureTokenConnection(JADESUtilsTest.class.getResourceAsStream(keystoreResourcePath), KEYSTORE_TYPE,
                new KeyStore.PasswordProtection(pass.toCharArray()));
    }


    @Test
    public void testJAdESLevelBDetached() throws Exception{

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(JADESUtilsTest.class.getResourceAsStream(keystoreResourcePath), pass.toCharArray());


        X509Certificate cert = (X509Certificate)keyStore.getCertificate(alias);


        originalDocument = new FileDocument("src/test/resources/example/organization-search-parameter.json");

        //DSSDocument documentToSign = new HTTPHeaderDigest(originalDocument, DigestAlgorithm.SHA256);
        List<DSSDocument> documentsToSign = new ArrayList<>();
        documentsToSign.add(new HTTPHeader("(request-target)", "application/json"));
        documentsToSign.add(new HTTPHeader("host", "HTTP Headers Example"));
        documentsToSign.add(new HTTPHeader("content-encoding", "HTTP Headers Example"));
        documentsToSign.add(new HTTPHeaderDigest(originalDocument, DigestAlgorithm.SHA256));
        HTTPHeaderDigest digest = new HTTPHeaderDigest(originalDocument, DigestAlgorithm.SHA256);
        System.out.println("digetst " + digest.getValue());

        JAdESSignatureParameters signatureParameters = new JAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(Calendar.getInstance().getTime());
        signatureParameters.setSigningCertificate(new CertificateToken(cert));
        signatureParameters.setCertificateChain(new CertificateToken(cert)); // it it selfsigned test cert  :)
        signatureParameters.setIncludeCertificateChain(true);


        signatureParameters.setBase64UrlEncodedPayload(false);

        signatureParameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        signatureParameters.setSignatureLevel(SignatureLevel.JAdES_BASELINE_B);
        signatureParameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        signatureParameters.setSigDMechanism(SigDMechanism.HTTP_HEADERS);


        CertificateVerifier commonCertificateVerifier = getOfflineCertificateVerifier();

        JAdESService service = new JAdESService(commonCertificateVerifier);

        ToBeSigned dataToSign = service.getDataToSign(documentsToSign, signatureParameters);

        AbstractKeyStoreTokenConnection token = getToken();


        SignatureValue signatureValue = token.sign(dataToSign, signatureParameters.getDigestAlgorithm(),
                signatureParameters.getMaskGenerationFunction(), token.getKey(alias));




// We invoke the service to sign the document with the signature value obtained in
// the previous step.
        DSSDocument signedDocument = service.signDocument(documentsToSign, signatureParameters,
                signatureValue);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        signedDocument.writeTo(baos);

        String value = new String(baos.toByteArray());

        DSSDocument signDoc = new InMemoryDocument(value.getBytes());
        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signDoc);

        validator.setDetachedContents(documentsToSign);
        validator.setTokenExtractionStrategy(TokenExtractionStrategy.NONE);

        validator.setCertificateVerifier(commonCertificateVerifier);
        Reports reports = validator.validateDocument();

        reports.print();



    }

    @Test
    public void testJAdESLevelBEnveloped() throws Exception{

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(JADESUtilsTest.class.getResourceAsStream(keystoreResourcePath), pass.toCharArray());


        X509Certificate cert = (X509Certificate)keyStore.getCertificate(alias);


        originalDocument = new FileDocument("src/test/resources/example/organization-search-parameter.json");

        JAdESSignatureParameters signatureParameters = new JAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(Calendar.getInstance().getTime());
        signatureParameters.setSigningCertificate(new CertificateToken(cert));
        signatureParameters.setCertificateChain(
                Arrays.stream(keyStore.getCertificateChain(alias))
                        .map(certificate -> new CertificateToken((X509Certificate)certificate)).collect(Collectors.toList()));
        signatureParameters.setIncludeCertificateChain(true);


        // signatureParameters.setBase64UrlEncodedPayload(false);

        // signatureParameters.setSignaturePackaging(SignaturePackaging.DETACHED);
        signatureParameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
        signatureParameters.setSignatureLevel(SignatureLevel.JAdES_BASELINE_B);
        signatureParameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        //    signatureParameters.setSigDMechanism(SigDMechanism.HTTP_HEADERS);


        CertificateVerifier commonCertificateVerifier = getOfflineCertificateVerifier();

        JAdESService service = new JAdESService(commonCertificateVerifier);

        ToBeSigned dataToSign = service.getDataToSign(originalDocument, signatureParameters);

        AbstractKeyStoreTokenConnection token = getToken();


        SignatureValue signatureValue = token.sign(dataToSign, signatureParameters.getDigestAlgorithm(),
                signatureParameters.getMaskGenerationFunction(), token.getKey(alias));




// We invoke the service to sign the document with the signature value obtained in
// the previous step.
        DSSDocument signedDocument = service.signDocument(originalDocument, signatureParameters,
                signatureValue);

        signedDocument.writeTo(System.out);



        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signedDocument);
        //validator.setDetachedContents(originalDocument);
        validator.setTokenExtractionStrategy(TokenExtractionStrategy.NONE);

        validator.setCertificateVerifier(commonCertificateVerifier);
        Reports reports = validator.validateDocument();

        reports.print();



    }

}