##############################################################################
# 多阶段构建: Maven编译 -> JRE运行
# 镜像产物约 400MB（JRE 17 + Spring Boot Fat Jar）
##############################################################################
FROM maven:3.9-eclipse-temurin-8 AS builder
WORKDIR /build

# 先拷贝 pom.xml，利用 Docker 缓存加速依赖下载
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 拷贝源码并编译打包
COPY src ./src
RUN mvn package -DskipTests -B -Pprod

##############################################################################
# 运行阶段: 轻量 JRE 镜像
##############################################################################
FROM eclipse-temurin:8-jre
WORKDIR /app

COPY --from=builder /build/target/device-access-worker-1.0.0.jar app.jar

EXPOSE 18091

# 默认环境变量（可通过 docker-compose / docker run 覆盖）
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    TEMPORAL_ADDRESS="temporal:7233" \
    FUJI_API_BASE_URL="http://10.66.32.54:60009" \
    XINKAIPU_API_BASE_URL="http://10.66.32.110" \
    WEBHOOK_CALLBACK_URL="https://your-business-system.com/api/webhook/callback" \
    WORKER_SCENARIOS="all"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar"]
