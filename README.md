# mtls-rest-job-worker
Camunda 8 Example for a job worker using mtls

### Connection Setup

#### Connect to Camunda Platform 8 SaaS Cluster

1. Follow the [Getting Started Guide](https://docs.camunda.io/docs/guides/getting-started/) to create an account, a
   cluster and client credentials
2. Use the client credentials to fill the following environment variables:
    * `ZEEBE_ADDRESS`: Address where your cluster can be reached.
    * `ZEEBE_CLIENT_ID` and `ZEEBE_CLIENT_SECRET`: Credentials to request a new access token.
    * `ZEEBE_AUTHORIZATION_SERVER_URL`: A new token can be requested at this address, using the credentials.
3. Run `Worker`

#### Connect to local Installation

For a local installation (without authentication) you only need to set `ZEEBE_ADDRESS`

### Workflow

Either you deploy `process.bpmn` or you design your own process with a service task with the `greet` job type.


