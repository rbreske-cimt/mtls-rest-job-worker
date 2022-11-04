# mTLS-REST-Job-Worker
Camunda 8 Example for a job worker, communicating with a REST service, using mtls.

## Preparation 

### Deploy a Workflow

Either you deploy `process.bpmn` or you design your own process with a service task with the `mtls` job type.

### Set the env variables for the cluster connection

Depending on your use case choose one of the following

##### 1. Connect to Camunda Platform 8 SaaS Cluster

Use the client credentials to fill the following environment variables (can be with .env file as explained in **Execution** below) :
    * `ZEEBE_ADDRESS`: Address where your cluster can be reached.
    * `ZEEBE_CLIENT_ID` and `ZEEBE_CLIENT_SECRET`: Credentials to request a new access token.
    * `ZEEBE_AUTHORIZATION_SERVER_URL`: A new token can be requested at this address, using the credentials.

##### 2. Connect to local Installation or Self Managed

For a local installation (without authentication) you only need to set `ZEEBE_ADDRESS`

### mTLS setup

Place your Certificate Authority public certificate in cacerts :
```bash
keytool -cacerts -importcert -trustcacerts -alias ca -file <your cert>
```

and your keystore (which holds the ca cert and the signed client cert, aswell as key)
in the root of this project.

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

To build the docker image execute :
```bash
docker build -t mtls-job-worker .
```

To run it :
```bash
docker run mtls-job-worker
```

### In Kubernetes

In the deployment.yaml you can configure what image should be used.
Per default it expects it locally under mtls-job-worker.
Feel free to push your image to dockerhub and reference that one though.

To deploy in Kubernetes
```bash
kubectl create namespace mtls-example # if you don't want this namespace, change it in the deployment yaml
kubectl create -f deployment.yaml
```
