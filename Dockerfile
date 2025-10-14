FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw -ntp dependency:go-offline

COPY src src
RUN ./mvnw -ntp package -DskipTests

FROM registry.access.redhat.com/ubi8/openjdk-21:1.23
ENV LANGUAGE='en_US:en'
WORKDIR /deployments

COPY --from=build --chown=185:root /workspace/target/quarkus-app/lib/ ./lib/
COPY --from=build --chown=185:root /workspace/target/quarkus-app/*.jar ./
COPY --from=build --chown=185:root /workspace/target/quarkus-app/app/ ./app/
COPY --from=build --chown=185:root /workspace/target/quarkus-app/quarkus/ ./quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT ["/opt/jboss/container/java/run/run-java.sh"]
