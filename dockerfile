# Stage 1: Install JAR dependencies
FROM maven:3.8.5-openjdk-17 AS install-jars
WORKDIR /app

COPY . /app

RUN mvn dependency:purge-local-repository && \
    mvn dependency:resolve-plugins && \
    mvn dependency:copy-dependencies -DincludeScope=runtime

# Stage 2: Build the application

FROM maven:3.8.5-openjdk-17 AS build

COPY src /pfe/hos/OpenHR/src
COPY lib /pfe/hos/OpenHR/lib
COPY pom.xml /pfe/hos/OpenHR
RUN mvn -f /pfe/hos/OpenHR/pom.xml clean package -DskipTests

EXPOSE 9090
ENTRYPOINT ["java","-jar","/pfe/hos/OpenHR/target/OpenHR-0.0.1-SNAPSHOT.jar"]