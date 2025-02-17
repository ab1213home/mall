## 应用部署手册（基于Linux的Jar包部署）
### 前置条件
#### 环境要求：
- JDK 11+（推荐JDK 17）

- Maven 3.6+

- MySQL 8.0.26（其他版本未验证兼容性）

- Redis 6.2.16（其他版本未验证兼容性）

#### 安装基础工具（若无）：

```shell
# 安装JDK（以OpenJDK 11为例）
sudo apt-get update && sudo apt-get install openjdk-17-jdk

# 安装Maven
sudo apt-get install maven
```
### 部署步骤
1. 编译项目
   1. 解析依赖
      ```shell
      mvn dependency:resolve
      ```
   2. 打包Jar（跳过测试）
      ```shell
      mvn clean package -DskipTests
      ```
      说明： 输出路径为 /mall-admin/target/，确认生成文件如 mall-2.0.1.jar。
2. 配置数据库
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
      CREATE USER '<用户名>'@'%' IDENTIFIED BY '<密码>';
      ```
   4. 授权
      ```sql
      GRANT ALL PRIVILEGES ON `<数据库名>`.* TO '<用户名>'@'%';
      FLUSH PRIVILEGES;
      ```
   注意事项：
   - 若需限制访问IP，将%替换为具体IP（如localhost）。
   - 生产环境建议使用专用账号并限制权限。

3. 安装Redis（网络上有很多教程，这里只介绍一种安装Redis的方式，使用宝塔面板安装Redis。）

   登录宝塔面板 → 进入“软件商店” → 搜索安装Redis。

   确保版本为 6.2.16（项目仅测试此版本）。
4. 应用配置（可用用你熟悉的文本编辑器，以nano编辑器为例）
   1. 复制配置文件 
      ```shell
      cp application-template.properties application.properties
      ```
      检查：确保模板文件已存在，否则需手动创建。
   2. 使用nano编辑器编辑application.properties文件
      ```shell
      nano application.properties
      ```
   3. 修改mysql数据库配置
      ```properties
      spring.datasource.username= <用户名>
      spring.datasource.password= <密码>
      spring.datasource.url= jdbc:mysql://<IP>:<端口>/<数据库名>?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
      ```
   4. 修改redis配置
      ```properties
      spring.data.redis.host=localhost
      spring.data.redis.port=6379
      spring.data.redis.password=<密码，默认无>
      ```

5. 启动应用
```shell
   java -jar /mall-admin/target/mall-2.0.1.jar --spring.config.location=classpath:application.properties
   ```
验证服务：

访问健康检查接口：

```shell
curl http://localhost:8080/actuator/health
```

服务自启动（可选）
使用Systemd管理服务：

```shell
# 创建服务文件
sudo nano /etc/systemd/system/mall.service

# 内容示例：
[Unit]
Description=Mall Application
After=network.target

[Service]
ExecStart=/usr/bin/java -jar /path/to/mall-2.0.1.jar
User=appuser
Restart=always

[Install]
WantedBy=multi-user.target
```