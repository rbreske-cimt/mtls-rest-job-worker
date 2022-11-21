import org.camunda.community.zeebe.testutils.stubs.ActivatedJobStub;
import org.camunda.community.zeebe.testutils.stubs.JobClientStub;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.data.MapEntry.entry;
import static org.camunda.community.zeebe.testutils.ZeebeWorkerAssertions.assertThat;

class WorkerJobHandlerTest {

    private final WorkerJobHandler sutJubHandler = new WorkerJobHandler();

    @Test
    public void testJobTermination() {
        final JobClientStub jobClient = new JobClientStub();
        final ActivatedJobStub activatedJob = jobClient.createActivatedJob();

        // since we don't have a Server running we cannot test these parameters
        activatedJob.setCustomHeaders(Collections.singletonMap("host", "this"));
        activatedJob.setCustomHeaders(Collections.singletonMap("port", "15"));
        activatedJob.setCustomHeaders(Collections.singletonMap("path", "/ignored"));

        // run job
        sutJubHandler.handle(jobClient, activatedJob);

        // assert that the job terminates
        assertThat(activatedJob).completed();
    }

    @Test
    public void testmTLSClient() {
        Exception e = null;
        try {
            final mTLSClient client = new mTLSClient();
        }
        catch(Exception caught) {
            e = caught;
            System.out.print(e.getMessage());
        }

        // make sure nothing went wrong on mTLS client initialization
        assert e == null;
    }
}