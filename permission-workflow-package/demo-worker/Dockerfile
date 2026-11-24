# 构建阶段
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B -Pdev

# 运行阶段
FROM openjdk:8-jre-slim

WORKDIR /app
COPY --from=builder /build/target/demo-worker-1.0.0.jar app.jar

EXPOSE 18093

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TEMPORAL_ADDRESS=host.docker.internal:7233
ENV MOKAPI_URL=http://host.docker.internal:19090

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
