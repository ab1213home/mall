# Jiang Mall

## 介绍
一款基于Spring Boot 3开发的商城系统

## 重要提示

**第一次初始化项目时，由于数据库量过大，可能需要耗时较长（由于有大量地理数据，预计10分钟左右），请耐心等待。**

![第一次初始化项目过程截图](/doc/picture/img01.png)

如果初始化失败，请手动导入数据库脚本(位于/data目录下)，并把修改配置文件application.properties。
```properties
spring.flyway.enabled = false
```

**docker部署支持本地文件上传需要修改路径为"/home/upload/"，正常使用可以自定义，但需要有对应权限。**

## 开发工具
IntelliJ IDEA 2024.2.3 (Ultimate Edition)

Navicat Premium Lite 版本17.1.5(简体中文)

## 技术栈

- **前端**：JavaScript
- **后端**：Spring Boot 3.3.4 (Oracle OpenJDK 17.0.11)
- **数据库**：MySQL 8.0.36 (MySQL Community Server) 
- **缓存**：Redis 6.2.16
- **构建工具**：Maven 3.8.4
- **版本控制**：Git

#### 第三方API
1. 使用[ipify](https://www.ipify.org/)实现公网ip获取
    ```javascript
    <script type="application/javascript">
        let ip = "";
        function getIP(json) {
            ip = json.ip
        }
    </script>
    <script type="application/javascript" src="https://api64.ipify.org?format=jsonp&callback=getIP"></script>
   ```

#### 后端

1. 使用[easy-captcha](https://github.com/ele-admin/EasyCaptcha "v1.6.2")实现验证码部分

2. 中国地址行政区划数据库数据来源：https://github.com/kakuilan/china_area_mysql
（经过裁剪）

3. 使用[mybatis-plus](https://github.com/baomidou/mybatis-plus "v3.5.8")实现数据库操作

4. 使用[sitemapgen4j](https://github.com/dfabulich/sitemapgen4j "v1.1.1")生成seo地图

5. 使用[mysql-connector-j](https://github.com/mysql/mysql-connector-j "v8.3.0")实现数据库连接

6. 使用[jakarta-mail](https://github.com/jakartaee/mail-api "v2.0.1")实现邮件发送

7. 使用[annotations](https://github.com/JetBrains/java-annotations "v26.0.0")实现注解

8. 使用[fastjson](https://github.com/stleary/JSON-java "v2.0.54")实现JSON解析

#### 前端

1. bootstrap 5.3.3

2. jquery 3.7.1

3. adminlte 4.0.0

4. bootstrap-icons 1.11.3

5. crypto-js 4.2.0

6. apexcharts 3.50.0

7. jsvectormap 1.5.3

8. popper 2.11.8

9. sortable 1.15.3

10. source-sans 3_5.1.0

11. overlayscrollbars 2.10.0

12. wangeditor 5.1.23

## 安装教程

1. 下载源码
    ```shell
    git clone https://github.com/ab1213home/mall.git
    ```
2. 进入项目目录
    ```shell
    cd mall
    ```
3. 部署项目
   1. 一键部署（直接拉起公共镜像部署，**注意：公共镜像可能不是最新版本**）
      ```shell
      docker-compose up -d
      ```
   2. 编译部署（使用Docker部署）
      ```shell
      sh run.sh
      ```
   3. 编译部署（使用Jar包部署）
      1. 安装依赖
         ```shell
         mvn dependency:resolve
         ```
      2. 编译Jar包
         ```shell
         mvn clean package -DskipTests
         ```
      3. 复制配置文件
         ```shell
         cp application-template.properties application.properties
         ```
      4. 创建数据库（可以使用数据库工具，如Navicat Premium Lite创建数据库）
         1. 登录数据库
            ```shell
            mysql -u root -p
            ```
         2. 创建数据库
            ```sql
            CREATE DATABASE mall CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
            ```
         3. 创建用户（如果数据库和目标服务器在同一个服务器，可以使用'mall'@'localhost'）
            ```sql
            CREATE USER 'mall'@'%' IDENTIFIED BY 'mall';
            ```
         4. 授予权限
            ```sql
            GRANT ALL PRIVILEGES ON mall.* TO 'mall'@'%';
            FLUSH PRIVILEGES;
            ```
      5. 编辑配置文件（可用用你熟悉的文本编辑器，以nano编辑器为例）
         1. 使用nano编辑器编辑application.properties文件
            ```shell
            nano application.properties
            ```
         2. 修改mysql数据库配置
            ```properties
            spring.datasource.username= mall
            spring.datasource.password= mall
            spring.datasource.url= jdbc:mysql://localhost:3306/mall?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
            ```
         3. 修改redis配置
            ```properties
            spring.data.redis.host=localhost
            spring.data.redis.port=6379
            spring.data.redis.password=mall
            ```
      6. 运行jar包
      ```shell
      java -jar /mall-admin/target/mall-2.0.1.jar --spring.config.location=classpath:application.properties
      ```
## 测试环境

1. 操作系统：CentOS Stream 9 x86_64
2. 内核版本：5.14.0-513.el9.x86_64
3. MySQL版本：8.0.36
4. Docker版本：27.3.1
5. Docker-compose版本：v2.29.7

## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request