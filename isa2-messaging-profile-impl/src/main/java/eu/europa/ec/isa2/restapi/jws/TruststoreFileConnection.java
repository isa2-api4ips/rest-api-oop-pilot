package eu.europa.ec.isa2.restapi.jws;

import eu.europa.esig.dss.enumerations.CertificateSourceType;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.spi.x509.ListCertificateSource;
import eu.europa.esig.dss.token.AbstractSignatureTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KSPrivateKeyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.DestroyFailedException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class TruststoreFileConnection extends CommonTrustedCertificateSource  {

    private static final Logger LOG = LoggerFactory.getLogger(TruststoreFileConnection.class);

    long keystoreLastModified  = 0;
    File keystoreLocation = null;
    KeyStore keyStore;

    TruststoreDataProvider keystoreDataProvider;

    public TruststoreFileConnection(TruststoreDataProvider dataProvider) {
        keystoreDataProvider = dataProvider;
        getKeyStore();
    }



    protected boolean keystoreChanged(){
        File keystore = new File(keystoreDataProvider.getTruststoreLocation());
        return (this.keyStore == null
                || keystoreLocation==null
                || keystoreLastModified==0
                || !keystoreLocation.equals(keystore)
                || keystoreLastModified!=keystore.lastModified());
    }

    public X509Certificate getCertificate(String alias) {
        KeyStore keyStore = this.getKeyStore();
        Certificate cert = null;
        try {
            cert = keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            new DSSException("Error occurred while retrieving certificate for alias ["+alias+"]!", e);
        }
        if (cert instanceof  X509Certificate) {
            return (X509Certificate)cert;
        }
        throw new DSSException("Certificate ["+cert.getClass().getName()+"] for alias ["+alias+"] can not be cast to X509Certificate!");
    }

    public CertificateToken getCertificateToken(String alias) {
        X509Certificate certificate = this.getCertificate(alias);
        return new CertificateToken(certificate);
    }




    public KeyStore getKeyStore()  throws DSSException {

        if ( keystoreChanged()) {
            File keystoreFile = new File(keystoreDataProvider.getTruststoreLocation());
            LOG.info("Load truststore: [{}]", keystoreFile.getAbsolutePath());
            try {
                keyStore = KeyStore.getInstance(keystoreDataProvider.getTruststoreType());
                keyStore.load(new FileInputStream(keystoreFile), keystoreDataProvider.getTruststoreCredentials());
                keystoreLocation = keystoreFile;
                keystoreLastModified = keystoreFile.lastModified();
            } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
                throw new DSSException("Error occurred while loading keystore ", e);
            }
            reset();
        }
        return keyStore;
    }

    public void reset(){
        super.reset();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while(aliases.hasMoreElements()){
                String alias = aliases.nextElement();

                addCertificate(getCertificateToken(alias));
            }

        } catch (KeyStoreException e) {
            throw new DSSException("Error occurred while loading keystore ", e);
        }

    }


}
