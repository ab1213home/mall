# 使用官方的Java运行时作为父镜像
FROM openjdk:17

# 将本地文件复制（或添加）到容器中
COPY /target/mall-v1.5_reconfiguration.jar  app.jar

# 声明运行时容器提供服务时使用的端口
EXPOSE 8080

# 指定容器启动时运行jar包
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]