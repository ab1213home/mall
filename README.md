# mall

## 介绍
网站开发与设计课程设计

## 软件架构

基于JDK17开发，使用spring boot3.3.4+mybatisplus3.5.8+mysql8.0.36 构建

yml配置文件在resources目录下

#### 后端

使用easy-captcha实现验证码部分，github地址：https://github.com/ele-admin/EasyCaptcha

中国地址行政区划数据库数据来源：https://github.com/kakuilan/china_area_mysql
（经过裁剪）

使用mybatis-plus(v3.5.8)实现数据库操作，github地址：https://github.com/baomidou/mybatis-plus

使用sitemapgen4j(v1.1.1)生成sitemap,github地址：https://github.com/dfabulich/sitemapgen4j

使用mysql-connector-j(v8.3.0)实现数据库连接，github地址：https://github.com/mysql/mysql-connector-j

使用jakarta-mail(v2.0.1)实现邮件发送，github地址：https://github.com/jakartaee/mail-api

使用annotations(v26.0.0)实现注解，github地址：https://github.com/JetBrains/java-annotations

#### 前端

1.bootstrap 5.3.3

2.jquery 3.7.1

3.adminlte 4.0.0

4.bootstrap-icons 1.11.3

5.crypto-js 4.2.0

6.apexcharts 3.50.0

7.jsvectormap 1.5.3

8.popper 2.11.8

9.sortable 1.15.3

10.source-sans 3_5.1.0

11.overlayscrollbars 2.10.0

12.wangeditor 5.1.23

## 安装教程

1.  使用Docker部署项目
2.  使用tomcat部署项目

## 使用说明

1. git源码
2. 修改数据库配置文件
3. maven编译项目
4. docker部署或者tomcat部署

## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request