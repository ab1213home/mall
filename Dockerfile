# 使用官方的Java运行时作为父镜像
FROM openjdk:17

LABEL maintainer="ab1213home<jiangrongjun2004@163.com>"
LABEL version="1.6.6"
LABEL description="mall-reconfiguration"

#maintainer 	AdGuard Team <devteam@adguard.com>
#org.opencontainers.image.authors 	AdGuard Team <devteam@adguard.com>
#org.opencontainers.image.created 	2024-07-04T15:46:33Z
#org.opencontainers.image.description 	Network-wide ads & trackers blocking DNS server
#org.opencontainers.image.documentation 	https://github.com/AdguardTeam/AdGuardHome/wiki/
#org.opencontainers.image.licenses 	GPL-3.0
#org.opencontainers.image.revision 	c7d8b9ede1eaf8507e406875c679c0a1c21e9cca
#org.opencontainers.image.source 	https://github.com/AdguardTeam/AdGuardHome
#org.opencontainers.image.title 	AdGuard Home
#org.opencontainers.image.url 	https://adguard.com/en/adguard-home/overview.html
#org.opencontainers.image.vendor 	AdGuard
#org.opencontainers.image.version 	v0.107.52

# 设置时区环境变量
ENV TZ=Asia/Shanghai

# VOLUME ["/home"]

# 设置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装 dmidecode
RUN microdnf install -y dmidecode

# 将本地文件复制（或添加）到容器中
COPY target/mall-1.6.6_reconfiguration.jar  mall.jar

# 声明运行时容器提供服务时使用的端口
EXPOSE 8080

# 指定容器启动时运行jar包
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.config.location=file:./application.properties","-jar","/mall.jar"]