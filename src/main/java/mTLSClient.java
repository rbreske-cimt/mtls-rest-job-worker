import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

public class mTLSClient {

    private CloseableHttpClient client = null;
    private String keyPW;
    private String keystorePath;
    private String keystorePW;

    // TODO : outsource file locations and pws to config files, dotenv etc.
    public mTLSClient() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            UnrecoverableKeyException, KeyManagementException {

        // load ssl configuration from properties file
        this.loadSSLProps("application.properties");

        // load stores from files
        KeyStore ks = keyStore(keystorePath, keystorePW.toCharArray());

        // create client with loaded stores (so the server can act as a client and verify the actual client)
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(ks, keyPW.toCharArray())
                .build();

        this.client = HttpClients.custom().setSSLContext(sslContext).build();
    }

    // load application properties for ssl configuration
    private void loadSSLProps(String filename) throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + filename;

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));

        this.keystorePath = rootPath + appProps.getProperty("worker.ssl.keystore");
        this.keystorePW = appProps.getProperty("worker.ssl.keystorePW");
        this.keyPW = appProps.getProperty("worker.ssl.keyPW");
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