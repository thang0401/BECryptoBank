FROM amazoncorretto:21-alpine3.21-jdk
WORKDIR /app
COPY target/BECryptoBank.jar app.jar
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -XX:+UseG1GC"
ENTRYPOINT ["java", "$JAVA_OPTS", "-jar", "app.jar"]