# mall

## 介绍
网站开发与设计课程设计

## 开发工具
IntelliJ IDEA 2024.2.3 (Ultimate Edition)

Navicat Premium Lite 版本17.1.5(简体中文)

## 软件架构

基于JDK17开发，使用spring boot3.3.4+mybatisplus3.5.8+mysql8.0.36构建

#### 后端

1. 使用easy-captcha实现验证码部分，github地址：https://github.com/ele-admin/EasyCaptcha

2. 中国地址行政区划数据库数据来源：https://github.com/kakuilan/china_area_mysql
（经过裁剪）

3. 使用mybatis-plus(v3.5.8)实现数据库操作，github地址：https://github.com/baomidou/mybatis-plus

4. 使用sitemapgen4j(v1.1.1)生成sitemap,github地址：https://github.com/dfabulich/sitemapgen4j

5. 使用mysql-connector-j(v8.3.0)实现数据库连接，github地址：https://github.com/mysql/mysql-connector-j

6. 使用jakarta-mail(v2.0.1)实现邮件发送，github地址：https://github.com/jakartaee/mail-api

7. 使用annotations(v26.0.0)实现注解，github地址：https://github.com/JetBrains/java-annotations

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

   -h127.0.0.1表示连接到本地的 MySQL 服务器，如果需要连接到其他服务器，则修改为对应服务器的 IP 地址。
   
    your_db 是你要导入数据的数据库名称。
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