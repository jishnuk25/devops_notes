# first stage using ubuntu image for git clone
FROM ubuntu:latest As Git

# install git
RUN apt-get update && \
         apt-get install -y git

WORKDIR /app

# cloning the application from github to the work directory(/app)
RUN git clone https://github.com/jishnuk25/simple-java-maven-app.git

# the second stage of our build will use a maven 3.6.1 parent image
FROM maven:3.6.1-jdk-8-alpine AS MAVEN_BUILD

WORKDIR /app

# copying the source code from first stage to here
COPY --from=Git /app/simple-java-maven-app /app

# package our application code
RUN mvn clean package

# the third stage of our build will use open jdk 8 on alpine 3.9
FROM openjdk:8-jre-alpine3.9

WORKDIR /app

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /app/target/my-app-1.0-SNAPSHOT.jar /app

# exposing the 8080 port for application access
EXPOSE 8080

# set the startup command to execute the jar
CMD ["java", "-jar", "my-app-1.0-SNAPSHOT.jar"]
