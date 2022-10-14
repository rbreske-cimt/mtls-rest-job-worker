import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

public class mTLSClient {

    private CloseableHttpClient client = null;

    // TODO : outsource file locations and pws to config files, dotenv etc.
    public mTLSClient() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            UnrecoverableKeyException, KeyManagementException {

        // load stores from files
        KeyStore ks = keyStore("client_keystore.jks", "changeit".toCharArray());

        // create client with loaded stores (so the server can act as a client and verify the actual client)
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(ks, "changeit".toCharArray())
                .build();

        this.client = HttpClients.custom().setSSLContext(sslContext).build();
    }

    public CloseableHttpResponse executeReq(HttpUriRequest req) throws IOException {
        return client.execute(req);
    }

    public void closeClient() throws IOException {
        this.client.close();
    }

    private KeyStore keyStore(String file, char[] password)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream in = new FileInputStream(file);
        keyStore.load(in, password);
        return keyStore;
    }
}