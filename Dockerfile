FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/hackanet-backend.jar
ADD ${JAR_FILE} hackanet-backend.jar

CMD ["java","-jar","/hackanet-backend.jar"]