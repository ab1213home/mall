# Jiang Mall Weapp Module

## 介绍
微信小程序支持模块

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

2. 使用[annotations](https://github.com/JetBrains/java-annotations "v26.0.0")实现注解

3. 使用[redis](https://github.com/redis/redis "v6.2.16")]实现缓存

4. 使用[pool2](https://github.com/j-easy/easy-pool "v2.12.0")实现线程池

5. 使用[javax.servlet-api](https://github.com/javax-servlet/servlet-api "v4.0.1")实现Servlet

## 测试环境

1. 操作系统：Windows 11
2. MySQL版本：8.0.36
3. Java版本：17.0.11
4. Redis版本：6.2.16
5. 浏览器版本：Microsoft Edge 132.0.2957.115 (64 位)

## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

## 接口列表

微信小程序所需的API接口汇总：

1. 用户认证相关：

   - 微信登录
   POST /weixin/login
   参数：code（微信登录code）
   返回：{token, userInfo, state}

   - 账号绑定
   POST /weixin/bind
   参数：{username, password, openid}
   返回：{token, userInfo}

2. 商品相关：

   - 商品列表（首页、搜索）
   GET /product/getList
   参数：{name?, pageNum, pageSize}

   - 商品详情
   GET /product/getInfo/{id}

3. 收藏相关：

   - 收藏列表
   GET /collection/getList
   参数：{pageNum, pageSize}

   - 添加收藏
   POST /favorite/add/{productId}

   - 取消收藏
   POST /favorite/cancel/{productId}

   - 检查收藏状态
   GET /favorite/check/{productId}
4. 地址管理：

   // 地址列表
   GET /address/getList
   参数：{pageNum, pageSize}
   
   // 地址详情
   GET /address/detail/{id}
   
   // 新增地址
   POST /address/add
   参数：{firstName, lastName, phone, areaCode, addressDetail, postalCode, isDefault}
   
   // 修改地址
   POST /address/update
   参数：{id, firstName, lastName, phone, areaCode, addressDetail, postalCode, isDefault}
   
   // 删除地址
   GET /address/delete
   参数：{id}