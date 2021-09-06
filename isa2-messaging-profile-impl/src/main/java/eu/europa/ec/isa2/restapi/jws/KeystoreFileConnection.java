package eu.europa.ec.isa2.restapi.jws;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.token.AbstractSignatureTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KSPrivateKeyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.DestroyFailedException;
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

public class KeystoreFileConnection extends AbstractSignatureTokenConnection {

    private static final Logger LOG = LoggerFactory.getLogger(KeystoreFileConnection.class);

    long keystoreLastModified  = 0;
    File keystoreLocation = null;
    KeyStore keyStore;

    KeystoreDataProvider keystoreDataProvider;

    public KeystoreFileConnection(KeystoreDataProvider dataProvider) {
        keystoreDataProvider = dataProvider;
    }

    protected boolean keystoreChanged(){
        File keystore = new File(keystoreDataProvider.getLocation());
        return (this.keyStore == null
                || keystoreLocation==null
                || keystoreLastModified==0
                || !keystoreLocation.equals(keystore)
                || keystoreLastModified!=keystore.lastModified());
    }

    @Override
    public void close() {

    }

    @Override
    public List<DSSPrivateKeyEntry> getKeys() throws DSSException {
        ArrayList list = new ArrayList();

        try {
            KeyStore keyStore = this.getKeyStore();
            Enumeration aliases = keyStore.aliases();

            while(aliases.hasMoreElements()) {
                String alias = (String)aliases.nextElement();
                DSSPrivateKeyEntry dssPrivateKeyEntry = this.getDSSPrivateKeyEntry(keyStore, alias);
                if (dssPrivateKeyEntry != null) {
                    list.add(dssPrivateKeyEntry);
                }
            }

            return list;
        } catch (GeneralSecurityException var6) {
            throw new DSSException("Unable to retrieve keys from keystore", var6);
        }
    }

    public DSSPrivateKeyEntry getKey(String alias) {
        KeyStore keyStore = this.getKeyStore();
        return this.getDSSPrivateKeyEntry(keyStore, alias);
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

    public List<CertificateToken> getCertificateTokenChain(String alias) {
        Certificate[] chain = new Certificate[0];
        try {
            chain = getKeyStore().getCertificateChain(alias);
        } catch (KeyStoreException e) {
            new DSSException("Error occurred while retrieving certificate chain for alias ["+alias+"]!", e);
        }

        return Arrays.stream(chain)
                .map(certificate -> new CertificateToken((X509Certificate)certificate)).collect(Collectors.toList());
    }

    private DSSPrivateKeyEntry getDSSPrivateKeyEntry(KeyStore keyStore, String alias) {
        KeyStore.ProtectionParameter parameter = keystoreDataProvider.getKeyCredentials(alias);
        try  {
            if (keyStore.isKeyEntry(alias)) {

                KeyStore.Entry entry = keyStore.getEntry(alias, parameter);
                if (entry instanceof KeyStore.PrivateKeyEntry) {
                    KeyStore.PrivateKeyEntry pke = (KeyStore.PrivateKeyEntry)entry;
                    return new KSPrivateKeyEntry(alias, pke);
                }
                LOG.warn("Skipped entry (unsupported class : {})", entry.getClass().getSimpleName());
            } else {
                LOG.debug("No related/supported key found for alias '{}'", alias);
            }

            return null;
        } catch (GeneralSecurityException var6) {
            throw new DSSException("Unable to retrieve key from keystore", var6);
        } finally {
            try {
                ((KeyStore.PasswordProtection)parameter).destroy();
            } catch (DestroyFailedException e) {
                LOG.warn("Destroying of the password protection failed", e);
            }
        }
    }

    public KeyStore getKeyStore()  throws DSSException {

        if ( keystoreChanged()) {
            File keystoreFile = new File(keystoreDataProvider.getLocation());
            LOG.info("Load keystore: [{}]", keystoreFile.getAbsolutePath());
            try {
                keyStore = KeyStore.getInstance(keystoreDataProvider.getType());
                keyStore.load(new FileInputStream(keystoreFile), keystoreDataProvider.getCredentials());
                keystoreLocation = keystoreFile;
                keystoreLastModified = keystoreFile.lastModified();
            } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
                throw new DSSException("Error occurred while loading keystore ", e);
            }

        }
        return keyStore;
    }



}
