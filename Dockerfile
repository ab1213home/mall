# 使用官方的Java运行时作为父镜像
FROM openjdk:17

LABEL maintainer="ab1213home<jiangrongjun2004@163.com>"
LABEL version="2.0.1"
LABEL description="mall"
LABEL license="Mulan PSL v2"
LABEL source="https://github.com/ab1213home/mall"

# 设置时区环境变量
ENV TZ=Asia/Shanghai

# 设置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 复制应用程序JAR包
COPY /mall-admin/target/mall-2.0.1.jar /mall.jar

# 指定工作目录
VOLUME /home
VOLUME /application.properties

# 声明运行时容器提供服务时使用的端口
EXPOSE 8080

# 指定容器启动时运行jar包
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.config.location=file:./application.properties","-jar","/mall.jar"]