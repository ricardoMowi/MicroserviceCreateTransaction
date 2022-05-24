FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/*.jar 
COPY ${JAR_FILE} createtransaction.jar
ENTRYPOINT ["java", "-jar", "/createtransaction.jar"]