FROM amazoncorretto:21.0.1-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} .
ENTRYPOINT ["java","-jar","customer-feedback-api.jar"]
