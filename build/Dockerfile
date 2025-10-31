FROM eclipse-temurin:17-jre 
ARG JAR=eromoro-0.0.1-SNAPSHOT.jar 
WORKDIR /app 
COPY build/libs/${JAR} app.jar 

EXPOSE 8080 
ENTRYPOINT ["sh","-c","java -jar /app/app.jar"]
