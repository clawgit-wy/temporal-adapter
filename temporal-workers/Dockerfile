FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/temporal-workers-1.0.0.jar app.jar
EXPOSE 18090
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TEMPORAL_ADDRESS="temporal:7233"
ENV HIK_API_URL="http://mokapi:19090/api/hikvision/verify"
ENV WEBHOOK_CALLBACK_URL="http://mokapi:19090/api/webhook/callback"
ENV WORKER_SCENARIOS="all"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
