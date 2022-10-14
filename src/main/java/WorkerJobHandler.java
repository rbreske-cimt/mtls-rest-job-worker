import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Collections;

public class WorkerJobHandler implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        System.out.println(job);

        final String path = job.getCustomHeaders().getOrDefault("path", "/");
        final String host = job.getCustomHeaders().getOrDefault("host", "127.0.0.1");
        final String port = job.getCustomHeaders().getOrDefault("port", "8080");

        final String uriStr = "https://" + host + ":" + port + path;
        String message = "";
        try {
            mTLSClient c = new mTLSClient();

            // TODO : support other req types : post, put, delete
            HttpGet req = new HttpGet();
            req.setURI(new URI(uriStr));
            CloseableHttpResponse res = c.executeReq(req);
            message = EntityUtils.toString(res.getEntity());
            c.closeClient();
        }
        catch (Exception e) {
            // TODO : handle exception
            System.out.println("Error caught : " + e.getMessage());
        }

        client.newCompleteCommand(job.getKey()).variables(Collections.singletonMap("message", message)).send().join();
    }
}
