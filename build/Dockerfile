# --- build stage: JDK 포함 ---
FROM gradle:8.8-jdk17 AS build
WORKDIR /src
COPY . .
RUN chmod +x ./gradlew && ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre
ARG JAR=eromoro-0.0.1-SNAPSHOT.jar 
WORKDIR /app 
COPY build/libs/${JAR} app.jar 

EXPOSE 8080 
ENTRYPOINT ["sh","-c","java -jar /app/app.jar"]
