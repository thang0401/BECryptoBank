FROM eclipse-temurin:21-jdk-alpine
EXPOSE 8080
WORKDIR /app
COPY target/BECryptoBank.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]