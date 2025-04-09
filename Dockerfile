FROM eclipse-temurin:21-jre AS builder
COPY ./target/BECryptoBank.jar ./app.jar
RUN java -Djarmode=tools -jar ./app.jar extract --layers --launcher

FROM eclipse-temurin:21-jre
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]