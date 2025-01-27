# Jiang Mall Captcha Module

## 介绍
验证码模块

## 开发工具
IntelliJ IDEA 2024.2.3 (Ultimate Edition)

## 技术栈

- **后端**：Spring Boot 3.3.4 (Oracle OpenJDK 17.0.11)
- **缓存**：Redis 6.2.16
- **构建工具**：Maven 3.8.4
- **版本控制**：Git

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

### 接口类表: CaptchaController

| 方法名            | HTTP 方法 | URL                    | 请求参数 | 响应内容 | 描述                                       |
|-------------------|-----------|------------------------|----------|----------|--------------------------------------------|
| generateCaptcha   | GET       | /common/captcha        | 无       | 验证码图像 (PNG) | 生成验证码并作为响应返回。验证码文本被存储在用户会话中，以便后续验证使用。 |

#### 详细说明

1. **generateCaptcha**
   - **HTTP 方法**: `GET`
   - **URL**: `/common/captcha`
   - **请求参数**: 无
   - **响应内容**: 验证码图像 (PNG)
   - **描述**: 生成验证码并作为响应返回。该方法通过 `HttpServletRequest` 和 `HttpServletResponse` 对象进行操作，生成并返回一个验证码图像。验证码文本被存储在用户会话中，以便后续验证使用。方法中设置了响应头以防止缓存，并将验证码图像输出到 HTTP 响应中。如果在写入响应体过程中发生 I/O 错误，将记录错误日志并返回内部服务器错误状态码。


### 接口类表: ICaptchaService

| 方法名            | 返回类型     | 参数列表                           | 描述                                       |
|-------------------|--------------|------------------------------------|--------------------------------------------|
| generateCaptcha   | SpecCaptcha  | String sessionId                   | 生成验证码                                 |
| validateCaptcha   | Boolean      | String sessionId, String captcha   | 验证用户输入的验证码是否正确               |

#### 方法详细说明

1. **generateCaptcha**
   - **返回类型**: `SpecCaptcha`
   - **参数列表**: `String sessionId`
   - **描述**: 根据用户会话ID生成验证码。

2. **validateCaptcha**
   - **返回类型**: `Boolean`
   - **参数列表**: `String sessionId, String captcha`
   - **描述**: 验证用户输入的验证码是否正确。返回 `true` 表示验证成功，`false` 表示验证失败，`null` 表示验证码已过期。