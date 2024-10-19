# 使用官方的Java运行时作为父镜像
FROM openjdk:17

# 设置时区环境变量
ENV TZ=Asia/Shanghai

# 设置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装 dmidecode

# 将本地文件复制（或添加）到容器中
COPY target/mall-1.6.4_reconfiguration.jar  mall.jar

# 声明运行时容器提供服务时使用的端口
EXPOSE 8080

# 指定容器启动时运行jar包
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.config.location=file:./application.properties","-jar","/mall.jar"]