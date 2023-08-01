# doc-file-service

The doc-file-service which plays the role upload and download files in the Document Publication and Approval System;
doc-file-service is a part of DOC microservices architecture.

## Prerequisites

Before getting started, make sure you have the following tools installed:

1. [Docker](https://www.docker.com/)
2. [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/) (Please use IntelliJ as some configurations are specific to this IDE)
3. [Eclipse Temurin JDK 17.0.5](https://adoptium.net/releases.html?variant=openjdk17&jvmVariant=hotspot) (or any 17 version, but it is recommended to use the proposed version)
4. [Maven](https://maven.apache.org/)

## Getting Started

Follow the steps below to set up and use the source code:

1. Start `docker-compose.yaml` file located in `etc/docker` using the following command:
   ```
   docker compose up -d
   ```

Note:
- If you are using an older version of Docker, use:
  ```
  docker-compose up -d
  ```
- To stop the Docker containers defined in `docker-compose.yaml`, you can use:
  ```
  docker compose stop
  ```
- If you want to remove the containers along with the volumes, you can use:
  ```
  docker compose down -v
  ```
**Be careful when using this command as it will permanently remove persisted volumes**!!!

2. Before using the source code, you need to build it first (this is a required step, not optional). Run the following Maven command:
    ```
    mvn clean install -DskipTests=true
    ```
Note:
- If you want to run the unit tests, remove the `-DskipTests=true` flag.
- If you want to skip the TypeScript generator, add the `-DskipTypescriptGenerator=true` flag.

4. To start the project, you will need to set up RabbitMQ, and Keycloak (our customized Keycloak, [refer](https://github.com/hcmus-k19-doc/doc-keycloak) for more information). After that configure the project to integrate with these services. You can set up the properties in the `application-dev.yaml` file located in the `module service`, package `src\main\resources`.

You should now be able to run the project.

## Run the Project on Docker

Follow these steps to run the project using Docker:

1. Run the `rebuild-image.bat` file in the root of the project to build the image. If the file cannot run completely, please copy and paste each command in `rebuild-image.bat` to your terminal to build the image.

2. Start the `docker-compose.yaml` file located in `etc/docker` using the following command:
```
docker compose --profile local-deployment up -d
```

**Note: Remember to configure RabbitMQ, and Keycloak (our customized Keycloak, [refer](https://github.com/hcmus-k19-doc/doc-keycloak) for more information) before starting the project.**

## Run the Project on Kubernetes

Before running the project on Kubernetes, make sure you have `minikube` or `Kubernetes` on Docker installed.

Follow these steps to run the project on Kubernetes:

1. Run the `up-doc-services.bat` file.

## Contact
- [Nguyen Duc Nam](https://github.com/namworkmc)
- [Le Ngoc Minh Nhat](https://github.com/minhnhat02122001)
- [Hoang Huu Giap](https://github.com/hhgiap241)
