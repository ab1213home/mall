---
title: mall
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.23"

---

# mall

Base URLs:

* <a href="localhost:8080/">开发环境: localhost:8080/</a>

# Authentication

# TextController

## GET time

GET /text/time

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|time|query|number| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407244177,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET error

GET /text/session

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407244473,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getSessionId

GET /text/session-id

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407244763,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getUserInfo

GET /text/get-user-info

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|X-Forwarded-For|header|string| 否 |none|
|X-Real-IP|header|string| 否 |none|

> 返回示例

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityObject](#schemaresponseentityobject)|

## GET getIp

GET /text/getIp

> 返回示例

```json
null
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|string|

## GET getIp2

GET /text/getIp2

> 返回示例

```json
null
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|string|

## GET getIp3

GET /text/getIp3

> 返回示例

```json
null
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|string|

# 邮箱验证码控制器

## GET getSetting

GET /email/getSetting

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407284465,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getChecking

GET /email/getChecking

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407290467,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 发送重置密码验证码

POST /email/sendResetPassword

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|username|query|string| 是 |用户名或者邮箱|
|captcha|query|string| 是 |验证码|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407296899,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 处理注册请求

POST /email/sendRegister

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|username|query|string| 是 |用户名|
|email|query|string| 是 |邮箱|
|password|query|string| 是 |密码|
|confirmPassword|query|string| 是 |确认密码|
|captcha|query|string| 是 |验证码|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407301040,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST setSetting

POST /email/setSetting

> Body 请求参数

```json
{
  "host": "string",
  "port": "string",
  "username": "string",
  "sender_end": "string",
  "nickname": "string",
  "password": "string",
  "expiration_time": 0,
  "max_request_num": 0,
  "min_request_num": 0,
  "max_fail_rate": 0,
  "AllowSendEmail": true
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[EmailSettingVo](#schemaemailsettingvo)| 否 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407284902,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST sendChangeEmail

POST /email/sendChangeEmail

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|password|query|string| 是 |none|
|email|query|string| 是 |none|
|captcha|query|string| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407290730,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getList

GET /email/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407285174,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getNum

GET /email/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407285363,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 公共控制器

## POST 文件上传处理方法

POST /common/uploadFile

该方法用于处理文件上传请求，接收上传的文件并将其保存到指定路径

> Body 请求参数

```yaml
file: string

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 否 |none|
|» file|body|string(binary)| 是 |用户上传的文件，类型为MultipartFile|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407305615,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 生成验证码并作为响应返回

GET /common/captcha

该方法通过HttpServletRequest和HttpServletResponse对象进行操作，生成并返回一个验证码图像
验证码文本被存储在用户会话中，以便后续验证使用

> 返回示例

> 200 Response

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## POST 处理用户头像上传请求

POST /common/uploadFaces

> Body 请求参数

```yaml
file: string

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 否 |none|
|» file|body|string(binary)| 是 |用户上传的文件|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407306228,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取随机盐值

GET /common/getSalt

<p>
本方法主要用于向客户端返回一个用于加密的随机盐值（AES_SALT），
盐值在加密过程中与密码结合使用，增加加密的安全性。
方法通过HttpSession参数接收会话信息，但实际上并未使用该参数，
因为盐值是固定配置好的，并不需要会话状态来决定。

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407382048,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getFooter

GET /common/getFooter

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407382192,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 用户控制器

## GET getUserList

GET /user/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407310129,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取距离下一次生日的天数

GET /user/getDays

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407315495,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 处理用户忘记密码后的第二步操作，包括验证邮箱、验证码、新密码及其确认，并修改密码

POST /user/forgot

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|code|query|string| 是 |验证码，用于验证用户身份|
|password|query|string| 是 |新密码，用户希望设置的新密码|
|confirmPassword|query|string| 是 |确认密码，用于确认新密码输入无误|
|X-Real-IP|header|string| 是 |none|
|X-Real-FINGERPRINT|header|string| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407321248,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 处理用户登录请求

POST /user/login

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|username|query|string| 是 |用户名|
|password|query|string| 是 |密码(前端加密)|
|captcha|query|string| 是 |验证码|
|X-Real-IP|header|string| 是 |none|
|X-Real-FINGERPRINT|header|string| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407324757,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 修改用户邮箱

POST /user/modify/email

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|email|query|string| 是 |用户的新邮箱|
|code|query|string| 是 |验证码|
|X-Real-IP|header|string| 是 |none|
|X-Real-FINGERPRINT|header|string| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407328337,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 处理用户注册第二步的请求

POST /user/registerStep1

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|code|query|string| 是 |验证码|
|X-Real-IP|header|string| 是 |none|
|X-Real-FINGERPRINT|header|string| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407340840,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getUserNum

GET /user/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407310394,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 处理用户登出请求

GET /user/logout

该方法通过移除会话中所有的用户相关属性来实现登出功能

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407325698,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 修改密码的处理方法

POST /user/modify/password

<p>
该方法负责处理用户修改密码的请求它包括验证新旧密码、确认密码的一致性，
以及在密码修改后清除相关的会话属性

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|oldPassword|query|string| 是 |旧密码|
|newPassword|query|string| 是 |新密码|
|confirmPassword|query|string| 是 |确认密码|
|X-Real-IP|header|string| 是 |none|
|X-Real-FINGERPRINT|header|string| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407328831,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 完成注册过程的第二步

POST /user/registerStep2

<p>
这个方法用于接收用户的基本个人信息，如手机号、姓名和生日，并在系统中进行记录
只有在第一步注册已完成的情况下，用户才能访问此端点

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|phone|query|string| 是 |用户的手机号码|
|firstName|query|string| 是 |用户的名字|
|lastName|query|string| 是 |用户的姓氏|
|birthday|query|string| 是 |用户的出生日期，格式为yyyy-MM-dd|
|img|query|string| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407341384,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 检查用户是否登录

GET /user/isLogin

通过检查会话（session）中的用户信息来判断用户是否已登录
如果用户已登录，则返回用户的详细信息

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407326021,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 修改用户信息

POST /user/modify/info

<p>
该方法允许用户修改自己的信息，同时也允许管理员修改其他用户的信息。
它接收一个HTTP会话对象来验证用户是否登录，并根据会话信息执行相应的权限检查。
对于管理员来说，该方法还支持修改用户的角色权限，但需要确保权限的正确性。

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 否 |用户ID，可选参数|
|phone|query|string| 否 |手机号，必填参数|
|firstName|query|string| 否 |名字，必填参数|
|lastName|query|string| 否 |姓氏，必填参数|
|birthDate|query|string| 否 |生日日期，必填参数，格式为yyyy-MM-dd|
|email|query|string| 否 |电子邮件，必填参数|
|img|query|string| 否 |none|
|isAdmin|query|boolean| 否 |是否是管理员，可选参数|
|roleId|query|integer| 否 |角色ID，可选参数|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407329281,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 检查当前用户是否为管理员用户

GET /user/isAdminUser

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407326415,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 处理用户锁定请求的函数

POST /user/modify/self-lock

主要功能是基于当前会话判断用户是否已登录，然后尝试锁定该用户

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407329999,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 管理员锁定账户功能

POST /user/modify/lock

该方法允许管理员锁定其账户

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|userId|query|integer| 是 |用户ID，用于标识需要锁定的用户|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407330191,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 处理管理员解锁用户账户的函数

POST /user/modify/unlock

该函数仅通过POST请求的'/modify/unlock'路径访问
主要功能是基于当前会话判断用户是否已登录，并且具有管理员权限，然后尝试解锁指定用户

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|userId|query|integer| 是 |要解锁的用户ID|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407330505,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 收货地址管理

## GET 获取收货地址列表

GET /address/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |当前页码，默认为1|
|pageSize|query|integer| 是 |每页大小，默认为10|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407345909,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 根据用户登录状态获取地址数据数量

GET /address/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407346244,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 添加地址信息

POST /address/add

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|firstName|query|string| 是 |名称，用于标识地址的所有者|
|lastName|query|string| 是 |姓氏，用于进一步标识地址的所有者|
|phone|query|string| 是 |联系电话，用于物流配送时的联系|
|areaCode|query|integer| 是 |省市区代码，用于标识地址所在的省市区|
|addressDetail|query|string| 是 |详细地址，精确到门牌号的地址信息|
|postalCode|query|string| 是 |邮政编码，用于邮件配送的邮政编码|
|isDefault|query|boolean| 是 |是否设为默认地址，标识该地址是否是用户的默认配送地址|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407346438,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 更新地址信息

POST /address/update

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |地址ID|
|firstName|query|string| 是 |收件人名|
|lastName|query|string| 是 |收件姓氏|
|phone|query|string| 是 |电话号码|
|areaCode|query|integer| 是 |地区代码|
|addressDetail|query|string| 是 |详细地址|
|postalCode|query|string| 是 |邮政编码|
|isDefault|query|boolean| 是 |是否设为默认地址|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407347019,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 处理删除地址的请求

GET /address/delete

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |地址的唯一标识符|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407347563,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 管理类控制器

## GET setAllowRegistration

GET /admin/admin

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|AllowRegistration|query|boolean| 否 |none|
|AllowUploadFile|query|boolean| 否 |none|
|AllowModify|query|boolean| 否 |none|
|AllowDelete|query|boolean| 否 |none|
|AllowLock|query|boolean| 否 |none|
|AllowUnlock|query|boolean| 否 |none|
|AllowUnlockUser|query|boolean| 否 |none|
|AllowModifyUser|query|boolean| 否 |none|
|AllowModifyAdmin|query|boolean| 否 |none|
|AllowModifyRole|query|boolean| 否 |none|
|AllowModifyCategory|query|boolean| 否 |none|
|AllowModifyProduct|query|boolean| 否 |none|
|AllowModifyOrder|query|boolean| 否 |none|
|AllowModifyAddress|query|boolean| 否 |none|
|AllowModifyPayment|query|boolean| 否 |none|
|AllowModifyShipping|query|boolean| 否 |none|
|AllowModifyCoupon|query|boolean| 否 |none|
|AllowModifyCouponCategory|query|boolean| 否 |none|
|AllowModifyCouponProduct|query|boolean| 否 |none|
|AllowModifyCouponOrder|query|boolean| 否 |none|
|AllowModifyCouponUser|query|boolean| 否 |none|
|AllowModifyCouponOrderItem|query|boolean| 否 |none|
|HOST|query|string| 否 |none|
|PORT|query|string| 否 |none|
|USERNAME|query|boolean| 否 |none|
|EMAIL_PASSWORD|query|string| 否 |none|
|NICKNAME|query|string| 否 |none|
|AllowSendEmail|query|boolean| 否 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407351581,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET save

GET /admin/save

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407351799,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET get

GET /admin/get

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407351970,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getRequestNum

GET /admin/getRequestNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407352163,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 获取行政区划数据

## GET 获取行政区划列表

GET /administrativeDivision/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|level|query|integer| 是 |行政级别，用于筛选行政区划的层级|
|parentCode|query|integer| 是 |父级代码，用于筛选属于哪个上级行政区划的下级行政区划|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407356328,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取邮政编码

GET /administrativeDivision/getPostalCode

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|areaCode|query|integer| 是 |地区代码|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407356868,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 轮播图控制器

## GET 获取轮播图列表

GET /banner/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |当前页码，默认为1|
|pageSize|query|integer| 是 |每页大小，默认为5|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407361009,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取轮播图数量

GET /banner/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407361219,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 添加轮播图

POST /banner/add

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|img|query|string| 是 |图片链接|
|url|query|string| 是 |轮播图链接|
|description|query|string| 是 |描述|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407361398,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 更新轮播图信息

POST /banner/update

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |轮播图ID|
|img|query|string| 是 |新的图片链接|
|url|query|string| 是 |新的轮播图链接|
|description|query|string| 是 |新的描述|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407362210,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 删除轮播图

GET /banner/delete

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |轮播图ID|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407362840,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 购物车控制器

## GET 根据用户ID获取购物车列表

GET /cart/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |当前页码，默认为1|
|pageSize|query|integer| 是 |每页大小，默认为5|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407365332,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 通过HTTP GET请求获取购物车商品数量

GET /cart/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407365504,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 添加商品到购物车

POST /cart/add

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|productId|query|integer| 是 |商品ID|
|num|query|integer| 是 |商品数量|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407365683,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 更新购物车数量

POST /cart/update

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |购物车项的ID|
|num|query|integer| 是 |购物车项的数量|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407366071,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 处理删除购物车项的请求

GET /cart/delete

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |购物车项的ID|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407366452,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 商品控制器

## GET getSnapshotInfo

GET /product/getSnapshotInfo

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407400382,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取分类列表

GET /category/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |当前页码，默认为1|
|pageSize|query|integer| 是 |每页大小，默认为5|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407370498,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取产品列表

GET /product/getList

<p>
说明：
- 该方法是一个处理HTTP GET请求的处理器方法，用于根据不同的筛选条件获取产品列表。
- 支持根据产品名称和产品类别进行筛选，同时提供了分页查询的功能。
- 参数pageNum和pageSize用于指定查询的页码和每页显示数量，以便于处理大量数据。
- 方法将调用productService中的getProductList方法来执行实际的查询逻辑。

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|name|query|string| 否 |产品名称的模糊搜索字符串，可选参数|
|categoryId|query|integer| 否 |产品的类别ID，用于筛选特定类别的产品，可选参数|
|pageNum|query|integer| 是 |当前页码，默认值为1，用于分页查询|
|pageSize|query|integer| 是 |每页显示的结果数量，默认值为5，用于分页查询|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407400793,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 通过GET请求获取分类数量

GET /category/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407370722,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 通过GET请求方式获取产品信息

GET /product/getInfo

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|productId|query|integer| 是 |从请求参数中获取的产品ID|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407401083,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 添加分类信息

POST /category/add

<p>
通过POST请求接收前端传来的分类代码和名称，验证用户是否登录并有管理员权限后，插入数据库

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|code|query|string| 是 |分类的代码|
|name|query|string| 是 |分类的名称|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407370933,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 添加产品信息

POST /product/add

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|code|query|string| 是 |产品编码|
|title|query|string| 是 |产品标题|
|categoryId|query|integer| 是 |类别ID|
|img|query|string| 是 |图片链接|
|price|query|number| 是 |价格|
|stocks|query|integer| 是 |库存数量|
|description|query|string| 是 |产品描述|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407401432,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 更新分类信息

POST /category/update

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |分类的ID|
|code|query|string| 是 |分类的代码|
|name|query|string| 是 |分类的名称|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407371449,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 更新产品信息的接口

POST /product/update

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |产品的ID|
|code|query|string| 是 |产品代码|
|title|query|string| 是 |产品标题|
|categoryId|query|integer| 是 |产品类别ID|
|img|query|string| 是 |产品图片路径|
|price|query|number| 是 |产品价格|
|stocks|query|integer| 是 |产品库存|
|description|query|string| 是 |产品描述|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407402475,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 处理删除分类的请求

GET /category/delete

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |分类的ID|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407372017,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 处理删除请求

GET /product/delete

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |删除资源的ID|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407403168,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getProductNum

GET /product/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407403614,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 收藏控制器

## POST insertCollection

POST /collection/add

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|productId|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407376543,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET deleteCollection

GET /collection/delete

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|productId|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407376917,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET deleteByIdCollection

GET /collection/deleteById

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407377304,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getCollectionList

GET /collection/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407377736,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getCollectionNum

GET /collection/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407377956,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET isCollected

GET /collection/isCollected

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|productId|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407378124,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 文件控制器

## GET 获取上传的文件

GET /upload/{filename}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|filename|path|string| 是 |文件名，包括扩展名|

> 返回示例

```json
{
  "path": "",
  "file": {
    "path": ""
  },
  "filePath": [
    [
      []
    ]
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityFileSystemResource](#schemaresponseentityfilesystemresource)|

## GET getFace

GET /faces/{filename}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|filename|path|string| 是 |none|

> 返回示例

```json
{
  "path": "",
  "file": {
    "path": ""
  },
  "filePath": [
    [
      []
    ]
  ]
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseEntityFileSystemResource](#schemaresponseentityfilesystemresource)|

## GET 获取文件夹大小和文件数量

GET /file/getFileSize

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407386244,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getFaceTemplateList

GET /getFaceTemplateList

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407386454,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getAllList

GET /file/getAllList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|path|query|string| 否 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407386660,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getList

GET /file/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|path|query|string| 否 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407386921,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取文件设置信息

GET /file/getSetting

<p>
本方法主要用于获取文件上传的相关设置，包括是否允许上传文件、文件上传路径、支持的图片后缀等
仅在用户通过管理员身份验证后才可访问这些设置信息

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407387274,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 处理文件设置的保存请求

POST /file/saveSetting

此方法接收文件设置信息，验证图片后缀的合法性，并更新系统配置

> Body 请求参数

```json
{
  "allowUploadFile": true,
  "fileUploadPath": "string",
  "imageSuffix": [
    {
      "key": "string",
      "value": {}
    }
  ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[FileSettingVo](#schemafilesettingvo)| 否 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407387449,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 处理获取文件用途的GET请求

GET /file/getPurpose

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|path|query|string| 是 |文件路径，用于定位文件|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407387669,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 订单控制器

## POST 处理结账请求

POST /order/checkout

> Body 请求参数

```json
[
  {
    "id": 0,
    "product": {
      "id": 0,
      "code": "string",
      "title": "string",
      "category": {
        "id": 0,
        "code": "string",
        "name": "string"
      },
      "img": "string",
      "price": 0,
      "stocks": 0,
      "description": "string"
    },
    "ischecked": true,
    "num": 0
  }
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|[CheckoutVo](#schemacheckoutvo)| 否 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407393504,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取临时订单列表

GET /order/getTemporaryList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |当前页码，默认为1|
|pageSize|query|integer| 是 |每页大小，默认为5|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407393973,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取临时购物车数量

GET /order/getTemporaryNum

该方法用于获取当前会话中临时购物车内的商品数量
需要确保用户已登录，并且会话中存在有效的商品列表

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407394288,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## POST 处理订单插入请求

POST /order/insert

> Body 请求参数

```json
[
  {
    "id": 0,
    "product": {
      "id": 0,
      "code": "string",
      "title": "string",
      "category": {
        "id": 0,
        "code": "string",
        "name": "string"
      },
      "img": "string",
      "price": 0,
      "stocks": 0,
      "description": "string"
    },
    "ischecked": true,
    "num": 0
  }
]
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|addressId|query|integer| 是 |地址ID，用于确定送货地址|
|paymentMethod|query|string| 是 |支付方式，用于订单支付|
|status|query|string| 是 |订单状态，用于标记订单的情况|
|body|body|[CheckoutVo](#schemacheckoutvo)| 否 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407394550,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET 获取订单列表

GET /order/getList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |当前页码，默认为1|
|pageSize|query|integer| 是 |每页显示的数量，默认为5|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407394969,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getOrderNum

GET /order/getNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407395308,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getAllOrderList

GET /order/getAllList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|pageNum|query|integer| 是 |none|
|pageSize|query|integer| 是 |none|

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407395515,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getAllOrderNum

GET /order/getAllNum

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407395943,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

## GET getAmount

GET /order/getAmount

> 返回示例

```json
{
  "code": 0,
  "message": "",
  "data": {},
  "timestamp": 1733407396102,
  "reqid": "",
  "i18nService": {},
  "success": false,
  "RequestCount": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|[ResponseResultObject](#schemaresponseresultobject)|

# 处理网站搜索引擎优化（SEO）相关的请求

## GET 处理 /robots.txt 路径的 GET 请求

GET /robots.txt

该方法生成一个 robots.txt 文件，指导搜索引擎如何爬取网站内容

> 返回示例

> 200 Response

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 处理 /sitemap.xml 路径的 GET 请求，生成并返回网站的 sitemap 文件

GET /sitemap.xml

> 返回示例

> 200 Response

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

# StatsController

## GET track

GET /track

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|User-Agent|header|string| 是 |none|
|X-Forwarded-For|header|string| 是 |none|

> 返回示例

```json
null
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|string|

## GET getStats

GET /stats

> 返回示例

```json
null
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|string|

# 数据模型

<h2 id="tocS_Object">Object</h2>

<a id="schemaobject"></a>
<a id="schema_Object"></a>
<a id="tocSobject"></a>
<a id="tocsobject"></a>

```json
{}

```

### 属性

*None*

<h2 id="tocS_File">File</h2>

<a id="schemafile"></a>
<a id="schema_File"></a>
<a id="tocSfile"></a>
<a id="tocsfile"></a>

```json
{
  "path": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|path|string|false|none||This abstract pathname's normalized pathname string. A normalized<br />pathname string uses the default name-separator character and does not<br />contain any duplicate or redundant separators.|

<h2 id="tocS_II18nService">II18nService</h2>

<a id="schemaii18nservice"></a>
<a id="schema_II18nService"></a>
<a id="tocSii18nservice"></a>
<a id="tocsii18nservice"></a>

```json
{}

```

### 属性

*None*

<h2 id="tocS_ResponseEntityFileSystemResource">ResponseEntityFileSystemResource</h2>

<a id="schemaresponseentityfilesystemresource"></a>
<a id="schema_ResponseEntityFileSystemResource"></a>
<a id="tocSresponseentityfilesystemresource"></a>
<a id="tocsresponseentityfilesystemresource"></a>

```json
{
  "path": "string",
  "file": {
    "path": "string"
  },
  "filePath": [
    [
      [
        {}
      ]
    ]
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|path|string|false|none||none|
|file|any|false|none||none|

anyOf

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» *anonymous*|[File](#schemafile)|false|none||none|

or

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» *anonymous*|null|false|none||none|

continued

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|filePath|[array]|false|none||none|

<h2 id="tocS_ResponseResultObject">ResponseResultObject</h2>

<a id="schemaresponseresultobject"></a>
<a id="schema_ResponseResultObject"></a>
<a id="tocSresponseresultobject"></a>
<a id="tocsresponseresultobject"></a>

```json
{
  "code": 0,
  "message": "string",
  "data": {},
  "timestamp": 0,
  "reqid": "string",
  "i18nService": {},
  "success": true,
  "RequestCount": null
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|code|integer|false|none||状态码，用于标识操作的结果|
|message|string|false|none||消息，用于描述操作的结果|
|data|[Object](#schemaobject)|false|none||数据，用于返回操作的结果数据|
|timestamp|integer|false|none||响应时间戳，用于记录响应的时间，以毫秒为单位|
|reqid|string|false|none||none|
|i18nService|[II18nService](#schemaii18nservice)|false|none||com.jiang.mall.service.II18nService|
|success|boolean|false|none||判断当前响应结果是否表示成功状态|
|RequestCount|null|false|none||none|

<h2 id="tocS_ResponseEntityObject">ResponseEntityObject</h2>

<a id="schemaresponseentityobject"></a>
<a id="schema_ResponseEntityObject"></a>
<a id="tocSresponseentityobject"></a>
<a id="tocsresponseentityobject"></a>

```json
{}

```

### 属性

*None*

<h2 id="tocS_EmailSettingVo">EmailSettingVo</h2>

<a id="schemaemailsettingvo"></a>
<a id="schema_EmailSettingVo"></a>
<a id="tocSemailsettingvo"></a>
<a id="tocsemailsettingvo"></a>

```json
{
  "host": "string",
  "port": "string",
  "username": "string",
  "sender_end": "string",
  "nickname": "string",
  "password": "string",
  "expiration_time": 0,
  "max_request_num": 0,
  "min_request_num": 0,
  "max_fail_rate": 0,
  "AllowSendEmail": true
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|host|string|false|none||none|
|port|string|false|none||none|
|username|string|false|none||none|
|sender_end|string|false|none||none|
|nickname|string|false|none||none|
|password|string|false|none||none|
|expiration_time|integer|false|none||none|
|max_request_num|integer|false|none||none|
|min_request_num|integer|false|none||none|
|max_fail_rate|number|false|none||none|
|AllowSendEmail|boolean|false|none||none|

<h2 id="tocS_CategoryVo">CategoryVo</h2>

<a id="schemacategoryvo"></a>
<a id="schema_CategoryVo"></a>
<a id="tocScategoryvo"></a>
<a id="tocscategoryvo"></a>

```json
{
  "id": 0,
  "code": "string",
  "name": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|false|none||分类ID|
|code|string|false|none||分类编码|
|name|string|false|none||分类名称|

<h2 id="tocS_ProductVo">ProductVo</h2>

<a id="schemaproductvo"></a>
<a id="schema_ProductVo"></a>
<a id="tocSproductvo"></a>
<a id="tocsproductvo"></a>

```json
{
  "id": 0,
  "code": "string",
  "title": "string",
  "category": {
    "id": 0,
    "code": "string",
    "name": "string"
  },
  "img": "string",
  "price": 0,
  "stocks": 0,
  "description": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|false|none||商品ID|
|code|string|false|none||商品编码|
|title|string|false|none||商品标题|
|category|[CategoryVo](#schemacategoryvo)|false|none||商品分类|
|img|string|false|none||商品图片地址|
|price|number|false|none||商品价格|
|stocks|integer|false|none||库存数量|
|description|string|false|none||商品描述|

<h2 id="tocS_MapVo">MapVo</h2>

<a id="schemamapvo"></a>
<a id="schema_MapVo"></a>
<a id="tocSmapvo"></a>
<a id="tocsmapvo"></a>

```json
{
  "key": "string",
  "value": {}
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|key|string|false|none||key|
|value|[Object](#schemaobject)|false|none||数据，用于返回操作的结果数据|

<h2 id="tocS_CheckoutVo">CheckoutVo</h2>

<a id="schemacheckoutvo"></a>
<a id="schema_CheckoutVo"></a>
<a id="tocScheckoutvo"></a>
<a id="tocscheckoutvo"></a>

```json
{
  "id": 0,
  "product": {
    "id": 0,
    "code": "string",
    "title": "string",
    "category": {
      "id": 0,
      "code": "string",
      "name": "string"
    },
    "img": "string",
    "price": 0,
    "stocks": 0,
    "description": "string"
  },
  "ischecked": true,
  "num": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|false|none||结算项ID|
|product|[ProductVo](#schemaproductvo)|false|none||商品信息|
|ischecked|boolean|false|none||是否选中|
|num|integer|false|none||商品数量|

<h2 id="tocS_FileSettingVo">FileSettingVo</h2>

<a id="schemafilesettingvo"></a>
<a id="schema_FileSettingVo"></a>
<a id="tocSfilesettingvo"></a>
<a id="tocsfilesettingvo"></a>

```json
{
  "allowUploadFile": true,
  "fileUploadPath": "string",
  "imageSuffix": [
    {
      "key": "string",
      "value": {}
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|allowUploadFile|boolean|false|none||none|
|fileUploadPath|string|false|none||none|
|imageSuffix|[[MapVo](#schemamapvo)]|false|none||none|

