# Jiang Mall File Module

## 介绍
文件模块

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
            ip = json.ip;
        }
    </script>
    <script type="application/javascript" src="https://api64.ipify.org?format=jsonp&callback=getIP"></script>
   ```

#### 后端

1. **MinIO 对象存储支持**  
   使用 [MinIO Java SDK](https://github.com/minio/minio-java)  
   - 功能：提供对象存储服务，支持文件的上传、下载、删除和管理，适用于分布式存储需求。

2. **公共模块支持**  
   使用 [mall-common](../mall-common/README.md)  
   - 功能：集成项目的公共模块，提供通用工具类、配置和基础功能支持，确保代码复用性和一致性。

3. **用户模块支持**  
   使用 [mall-user](../mall-user/README.md)  
   - 功能：集成项目的用户模块，提供用户相关的数据模型、服务和接口支持，适用于文件模块中涉及用户信息的场景。

4. **FTP 文件传输支持**  
   使用 [Apache Commons Net](https://commons.apache.org/proper/commons-net/)  
   - 功能：提供 FTP 协议的文件传输支持，支持文件的上传、下载和管理。

5. **SFTP 文件传输支持**  
   使用 [JSch](https://github.com/mwiede/jsch)  
   - 功能：提供 SFTP 协议的文件传输支持，支持安全的文件上传、下载和管理。

6. **横幅模块支持**  
   使用 [mall-banner](../mall-banner/README.md)  
   - 功能：集成项目的横幅模块，提供横幅相关的数据模型、服务和接口支持，适用于文件模块中涉及横幅信息的场景。

7. **压缩工具支持**  
   使用 [Apache Commons Compress](https://commons.apache.org/proper/commons-compress/)  
   - 功能：提供多种压缩格式的支持，包括 ZIP、GZIP 等，适用于文件的压缩和解压缩操作。

8. **核心模块支持**  
   使用 [mall-core](../mall-core/README.md)  
   - 功能：集成项目的核心模块，提供核心功能和基础服务支持，确保系统稳定性和可扩展性。

9. **商品模块支持**  
   使用 [mall-product](../mall-product/README.md)  
   - 功能：集成项目的商品模块，提供商品相关的数据模型、服务和接口支持，适用于文件模块中涉及商品信息的场景。

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

1.  下载源码
    ```shell
    git clone https://github.com/ab1213home/mall.git
    ```
2.  进入项目目录
    ```shell
    cd mall
    ```
3.  复制配置文件
    ```shell
    cp application-example.properties application.properties
    ```
    修改application.properties中的数据库配置
4. 导入数据库脚本
    ```shell
    tar -xzvf data/mall.zip -C data
    mysql -h127.0.0.1 -uroot -p -D your_db < data/mall.sql
    ```
   输入数据库root账户密码

   -h127.0.0.1:表示连接到本地的MySQL服务器，如果需要连接到其他服务器，则修改为对应服务器的IP地址。
   
    your_db:是你要导入数据的数据库名称。
5. 部署项目
   1. 使用Docker部署项目
      ```shell
      ./run.sh
      ``` 
      或者
      ```shell
      docker-compose up
      ```
   2. 使用tomcat部署项目
      ```shell
      mvn clean && mvn compile && mvn package
      java -jar target/mall-1.6.4_reconfiguration.jar --spring.config.location=classpath:application.properties
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