##############################################################################
# 多阶段构建: Maven编译 -> JRE运行
# 镜像产物约 400MB（JRE 8 + Spring Boot Fat Jar）
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

COPY --from=builder /build/target/hik-sync-worker-1.0.0.jar app.jar

EXPOSE 18092

# 默认环境变量（可通过 docker-compose / docker run 覆盖）
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    TEMPORAL_ADDRESS="temporal:7233" \
    WORKER_SCENARIOS="hikSync" \
    MYSQL_URL="jdbc:mysql://10.66.139.220:33306/safety?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false" \
    MYSQL_USERNAME="BP_QUERY" \
    MYSQL_PASSWORD="SdkWCNQ9" \
    PG_URL="jdbc:postgresql://10.40.0.1:7092/ctm02brss_ctm02brssdb" \
    PG_USERNAME="hikface" \
    PG_PASSWORD="zjk@111++"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar"]
