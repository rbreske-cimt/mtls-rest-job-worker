import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.Random;

public class mTLSClient {

    private HttpClient client = null;

    // dyn configuration
    private String keyStorePW;

    // static configuration
    private static final int keyStorePWLen = 20;
    private static final String appName = "mtlsClient";
    private static final String certType = "X.509"; // certificate type
    private static final String encryptAlgorithm = "RSA"; // what algorithm cert and key have been encrypted with
    private static final String keyStoreType = "jks"; //
    private static final String alg = "SUNX509"; //
    private static final String protocolType = "TLSv1.2"; //

    public mTLSClient() {
        // generate random pw for keystore
        byte[] arr = new byte[keyStorePWLen];
        new Random().nextBytes(arr);
        this.keyStorePW = new String(arr, Charset.forName("UTF-8"));

        // get cert and key, parse them
        SSLContext sslCtx = null;
        try {
            sslCtx = CreateSSLCtx();
        } catch(Exception e) {
            // TODO : better error handling ?
            e.printStackTrace();
            System.exit(1);
        }

        // require client auth
        SSLParameters sslParam = new SSLParameters();
        sslParam.setNeedClientAuth(true);

        // build actual client
        this.client = HttpClient.newBuilder()
                .sslContext(sslCtx)
                .sslParameters(sslParam)
                .build();

    }

    // expects pem files in X.509 format and RSA key
    private SSLContext CreateSSLCtx() throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            InvalidKeySpecException, IOException, UnrecoverableKeyException, KeyManagementException {

        // TODO : read certificate and key
        final byte[] pubCert = null;
        final byte[] privKey = null;

        // parse cert
        final CertificateFactory certFactory = CertificateFactory.getInstance(certType);
        final Collection<? extends Certificate> chain = certFactory
                .generateCertificates(new ByteArrayInputStream(pubCert));

        // parse key
        final Key key = KeyFactory.getInstance(encryptAlgorithm).generatePrivate(new PKCS8EncodedKeySpec(privKey));

        // place cert+key in keystore which is a PKCS#12 format file containing :
        // - pub key
        // - priv key
        KeyStore clientKeyStore = KeyStore.getInstance(keyStoreType);
        final char[] pwdChars = this.keyStorePW.toCharArray();
        clientKeyStore.load(null, null); // TODO : null ??
        clientKeyStore.setKeyEntry(appName, key, pwdChars, chain.toArray(new Certificate[0]));

        // init key manager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(alg);
        keyManagerFactory.init(clientKeyStore, pwdChars);

        // populate SSLContext with key manager
        SSLContext sslCtx = SSLContext.getInstance(protocolType);
        sslCtx.init(keyManagerFactory.getKeyManagers(), null, null); // TODO : null ??
        return sslCtx;
    }
}