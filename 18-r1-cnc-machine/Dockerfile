FROM openjdk:11.0-jdk-slim
VOLUME /tmp
COPY build/libs/cnc-machine-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]