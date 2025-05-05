FROM eclipse-temurin:21-jre
COPY ./target/BECryptoBank.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]