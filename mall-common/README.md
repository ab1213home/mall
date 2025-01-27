# Jiang Mall Common Module

## 介绍
公共模块

## 开发工具
IntelliJ IDEA 2024.2.3 (Ultimate Edition)

## 技术栈

- **后端**：Spring Boot 3.3.4 (Oracle OpenJDK 17.0.11)
- **缓存**：Redis 6.2.16
- **构建工具**：Maven 3.8.4
- **版本控制**：Git

#### 后端

1. 使用[annotations](https://github.com/JetBrains/java-annotations "v26.0.0")实现注解

2. 使用[javax.servlet-api](https://github.com/javax-servlet/servlet-api "v4.0.1")实现Servlet

3. 使用[sitemapgen4j](https://github.com/dfabulich/sitemapgen4j "v1.1.1")实现对象复制

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

### 接口类表: CommonController

| 方法名     | 请求类型 | URL 路径       | 返回类型               | 参数列表                          | 描述                                                                 |
|------------|----------|----------------|------------------------|-----------------------------------|----------------------------------------------------------------------|
| getSalt    | GET      | /common/getSalt| ResponseResult<Object> | HttpSession session               | 获取随机盐值。返回一个包含状态码和盐值的 `ResponseResult` 对象。   |
| getFooter  | GET      | /common/getFooter| ResponseResult<Object> | HttpSession session               | 获取页脚信息。返回一个包含状态码和页脚信息的 `ResponseResult` 对象。 |

#### 详细描述

1. **getSalt**
   - **请求类型**: `GET`
   - **URL 路径**: `/common/getSalt`
   - **返回类型**: `ResponseResult<Object>`
   - **参数列表**: `HttpSession session`
     - **描述**: `HttpSession` 对象，用于管理用户会话，但本方法中未使用该参数。
   - **描述**: 获取随机盐值。返回一个包含状态码和盐值的 `ResponseResult` 对象。盐值在加密过程中与密码结合使用，增加加密的安全性。

2. **getFooter**
   - **请求类型**: `GET`
   - **URL 路径**: `/common/getFooter`
   - **返回类型**: `ResponseResult<Object>`
   - **参数列表**: `HttpSession session`
     - **描述**: `HttpSession` 对象，用于管理用户会话，但本方法中未使用该参数。
   - **描述**: 获取页脚信息。返回一个包含状态码和页脚信息的 `ResponseResult` 对象。页脚信息包括电话号码和电子邮件地址。

### 接口类表: II18nService

| 方法名              | 返回类型 | 参数列表                          | 描述                                                                 |
|---------------------|----------|-----------------------------------|----------------------------------------------------------------------|
| getMessage          | String   | String key                        | 根据键获取对应的国际化消息。如果键不存在，返回 `null` 或空字符串。 |
| checkId             | Boolean  | Long id                           | 校验传入的 ID 是否合法。                                             |
| checkString         | Boolean  | String string                     | 校验传入的字符串是否合法。                                           |
| checkString         | Boolean  | String string, int l              | 校验传入的字符串是否合法，带有长度参数。                             |
| isValidIPv4         | Boolean  | String ip                         | 校验传入的字符串是否为合法的 IPv4 地址。                             |
| isValidIPv6         | Boolean  | String ip                         | 校验传入的字符串是否为合法的 IPv6 地址。                             |
| isValidEmail        | Boolean  | String email                      | 校验传入的字符串是否为合法的电子邮件地址。                           |
| isValidPassword     | Boolean  | String password                   | 校验传入的字符串是否为合法的密码。                                   |
| isValidPhone        | Boolean  | String phone                      | 校验传入的字符串是否为合法的电话号码。                               |

#### 详细描述

1. **getMessage**
   - **返回类型**: `String`
   - **参数**: `String key`
   - **描述**: 根据传入的键获取对应的国际化消息。如果键不存在，返回 `null` 或空字符串。

2. **checkId**
   - **返回类型**: `Boolean`
   - **参数**: `Long id`
   - **描述**: 校验传入的 ID 是否合法。

3. **checkString**
   - **返回类型**: `Boolean`
   - **参数**: `String string`
   - **描述**: 校验传入的字符串是否合法。

4. **checkString**
   - **返回类型**: `Boolean`
   - **参数**: `String string, int l`
   - **描述**: 校验传入的字符串是否合法，带有长度参数。

5. **isValidIPv4**
   - **返回类型**: `Boolean`
   - **参数**: `String ip`
   - **描述**: 校验传入的字符串是否为合法的 IPv4 地址。

6. **isValidIPv6**
   - **返回类型**: `Boolean`
   - **参数**: `String ip`
   - **描述**: 校验传入的字符串是否为合法的 IPv6 地址。

7. **isValidEmail**
   - **返回类型**: `Boolean`
   - **参数**: `String email`
   - **描述**: 校验传入的字符串是否为合法的电子邮件地址。

8. **isValidPassword**
   - **返回类型**: `Boolean`
   - **参数**: `String password`
   - **描述**: 校验传入的字符串是否为合法的密码。

9. **isValidPhone**
   - **返回类型**: `Boolean`
   - **参数**: `String phone`
   - **描述**: 校验传入的字符串是否为合法的电话号码。
