# 应用部署手册（基于Linux的Jar包部署）

---
## 目录
1. [环境准备](#环境准备)
2. [克隆项目](#克隆项目)
3. [项目编译](#项目编译)
4. [数据库配置](#数据库配置)
5. [应用配置](#应用配置)
6. [启动与验证](#启动与验证)
7. [服务自启动（可选）](#服务自启动)
8. [注意事项与FAQ](#注意事项与faq)
---

## 环境准备
### 环境要求

- Git

- JDK 11+（推荐JDK 17）

- Maven 3.6+

- MySQL 8.0.36（**其他版本未进行测试，不保证兼容性**）

- Redis 6.2.16（项目需要Redis至少有**8**个库，项目只对Redis 6.2.16有进行测试，**其他版本未进行测试，不保证兼容性**）

### 安装基础工具（若无）

以下命令适用于 **Debian/Ubuntu** 系统。  

若使用其他发行版（如CentOS/RHEL），请替换包管理命令（如 `yum` 或 `dnf`）。

1. 安装Git
   ```shell
   sudo apt-get install git
   ```
2. 安装JDK
   ```shell
   sudo apt-get install openjdk-17-jdk
   ```
3. 安装Maven
   ```shell
   sudo apt-get install maven
   ```
4. 安装Redis
   ```shell
   sudo apt-get install -y redis-server
   ```
5. 安装MySQL
   ```shell
   sudo apt-get install mysql-server
   ```
## 克隆项目
1. 下载源码
    ```shell
    git clone https://github.com/ab1213home/mall.git
    ```
2. 进入项目目录
    ```shell
    cd mall
    ```
## 项目编译

在项目根目录执行以下命令（跳过测试）：
```shell
   mvn clean package -DskipTests
```
输出路径：./mall-admin/target/mall-2.0.1.jar（相对于项目根目录）。

若依赖解析失败，可先运行：
```shell
   mvn dependency:resolve
```
## 数据库配置
1. 登录MySQL（按需替换用户名和密码）
   ```shell
   mysql -u root -p
   ```
2. 创建数据库（字符集建议utf8mb4）
   ```sql
   CREATE DATABASE `<数据库名>` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
   ```
3. 创建用户（按需调整访问权限）
   ```sql
   CREATE USER '<用户名>'@'<允许访问的IP>' IDENTIFIED BY '<密码>';
   ```
4. 授权
   ```sql
   GRANT ALL PRIVILEGES ON `<数据库名>`.* TO '<用户名>'@'<允许访问的IP>';
   FLUSH PRIVILEGES;
   ```
注意事项：
- 生产环境中，建议将 IP 限制为应用服务器地址（如 192.168.1.100），避免使用 %。
- 建议为应用创建专用账号，仅授予最小必要权限。
## 应用配置
1. 配置文件准备
   ```shell
   cp application-template.properties application.properties
   ```
   若模板不存在，需手动[下载](https://raw.githubusercontent.com/ab1213home/mall/refs/heads/develop/application-example.properties)。
   ```shell
   wget https://raw.githubusercontent.com/ab1213home/mall/refs/heads/develop/application-example.properties -O application.properties
   ```
2. 使用nano编辑器编辑application.properties文件
   ```shell
   nano application.properties
   ```
3. 修改mysql数据库配置
   ```properties
   spring.datasource.username= <用户名>
   spring.datasource.password= <密码>
   spring.datasource.url= jdbc:mysql://<IP>:3306/<数据库名>?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
   ```
4. 修改redis配置
   ```properties
   spring.data.redis.host=<IP>
   spring.data.redis.port=6379
   spring.data.redis.password=<密码>  # 若无密码则留空
   ```
   
## 启动与验证
1. 启动应用
   ```shell
   java -jar ./mall-admin/target/mall-2.0.1.jar \
      --spring.config.location=file:$(pwd)/application.properties
   ```
2. 验证服务状态
   ```shell
   curl http://localhost:8080/actuator/health
   ```
   预期输出：{"status":"UP"}
## 服务自启动
1. 创建用户（若已存在则跳过）
   ```shell
   sudo useradd -r -s /bin/false mall
   sudo chown -R mall:mall ./mall-admin/target/mall-2.0.1.jar
   sudo chown -R mall:mall application.properties
   ```
2. 创建服务文件
   ```shell
   sudo nano /etc/systemd/system/mall.service
   ```
   ```ini
   [Unit]
   Description=Mall Application
   After=network.target
   
   [Service]
   User=mall
   ExecStart=/usr/bin/java -jar /path/to/mall/mall-admin/target/mall-2.0.1.jar \
     --spring.config.location=file:/path/to/application.properties
   Restart=always
   RestartSec=5
   
   [Install]
   WantedBy=multi-user.target
   ```
3. 启用服务
   ```shell
   sudo systemctl daemon-reload
   sudo systemctl enable mall
   sudo systemctl start mall
   ```
### 注意事项与FAQ
1. 路径问题

   确保 spring.config.location 使用绝对路径（如 file:/opt/app/application.properties）。

2. 权限问题

   若服务启动失败，检查日志：
   ```shell
   journalctl -u mall.service -f
   ```

3. 防火墙配置

   开放应用端口（8080）及MySQL/Redis端口。