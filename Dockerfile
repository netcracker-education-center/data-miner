FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","/app.jar"]