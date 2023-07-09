FROM eclipse-temurin:17.0.5_8-jre-alpine
WORKDIR /app
COPY ./web/target/doc-file-service.jar /app/doc-file-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "doc-file-service.jar", "--spring.profiles.active=prod"]
