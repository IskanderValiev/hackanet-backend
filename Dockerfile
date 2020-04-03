FROM openjdk:8
ADD target/hackanet.jar hackanet.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "hackanet.jar"]

#ARG JAR_FILE=target/hackanet-backend.jar
#ADD ${JAR_FILE} hackanet-backend.jar
#CMD ["java","-jar","/hackanet-backend.jar"]