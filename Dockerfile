FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/ignite-test-1.0.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080