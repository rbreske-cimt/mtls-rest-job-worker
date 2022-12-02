# mTLS-REST-Job-Worker
Camunda 8 Example for a job worker, communicating with a REST service, using mtls. Written in plain java, with maven as
the build tool.

## Preparation 

### Deploy a Workflow

Either you deploy `process.bpmn` or you design your own process with a service task with the `mtls` job type.
Which server to send the request to can be configured via custom headers :
- host
- path
- port

### Set the env variables for the cluster connection

Depending on your use case choose one of the following

##### 1. Connect to Camunda Platform 8 SaaS Cluster

Use the client credentials to fill the following environment variables (can be with .env file as explained in **Execution** below) :

| key                                     | val                                                                |
|-----------------------------------------|--------------------------------------------------------------------|
| `ZEEBE_ADDRESS`                         |Address where your cluster can be reached.                          |
| `ZEEBE_CLIENT_ID`, `ZEEBE_CLIENT_SECRET`|Credentials to request a new access token.                          |
| `ZEEBE_AUTHORIZATION_SERVER_URL`        |A new token can be requested at this address, using the credentials.|

##### 2. Connect to local Installation

For a local installation (without authentication) you only need to set `ZEEBE_ADDRESS`

### mTLS setup

If mTLS (in java) is new to you check out [this repo](https://github.com/englaender/mTLS-Example-Java).

Place your Certificate Authoritys public certificate in cacerts :
```bash
keytool -cacerts -importcert -trustcacerts -alias ca -file <your cert>
```

and your keystore (which holds the ca cert and the signed client cert, aswell as key)
in the root of this project. The latter should be named `client_keystore.pkcs12`

## Execution

### locally
From the project root and execute :
```bash
mvn compile
mvn exec:java
```

or to package to .jar and execute run :

```bash
mvn clean compile assembly:single
java -jar target/mtls-worker-1.0-jar-with-dependencies.jar
```

Because I couldn't find out how to supply `.env` files to those, I wrote a
short sh script, which iterates through the `.env` and sets variables, executes
all commands supplied as string arguments to the script and lastly unsets the 
variables.

So i.e. :
```bash
./run.sh "java -jar target/mtls-worker-1.0-jar-with-dependencies.jar"
```

### In docker 

You might want to check out [zeebe in docker](https://docs.camunda.io/docs/self-managed/platform-deployment/docker/) aswell.

Additionaly to the keystore you'll have to put the CA cert that we need to trust in the root directory by the name `ca.pem`.

To build the docker image execute :
```bash
docker build -t mtls-job-worker .
```

To run it :
```bash
docker run mtls-job-worker
```

### In Kubernetes using helm

The project is wrapped using helm.
One can add it from this projects github pages
```bash
helm repo add mTLS-job-worker https://englaender.github.io/mtls-rest-job-worker/
```

and after you configured kube to point to the correct cluster, you can simply run
```bash
helm install mtls-job-worker /mTLS-job-worker
```

Until this process is automized, update the gh-pages branch by, deleting everything there, copying the helm-chart directory over and running
```bash
helm package ./helm-chart
helm repo indnex .
```

### Testing

Since I've not gotten around to writing actual tests the only way to do this is by hand.
To get a fully running setup you'll obviously need to have some mTLS server running.
I've mentioned it in the mTLS section but in case you didn't read that :
[This repo](https://github.com/englaender/mTLS-Example-Java) contains a working client/server mTLS Example.
One can simply use the script there to generate keys and certificates and plug them into the job worker.