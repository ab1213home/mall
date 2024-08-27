package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *作者： 蒋神 HJL
 * @since 2024-08-05
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    //登录
    @PostMapping("/login")
    public ResponseResult login(String phone, String password, String verifyCode, HttpSession session) {
//      1.检查手机号、密码及验证码是否完整
        if (phone.isEmpty() || password.isEmpty()) {
            return ResponseResult.failResult("手机号或密码不能为空");
        }
        if (verifyCode.isEmpty()) {
            return ResponseResult.failResult("验证码不能为空");
        }

//      2.验证手机号格式正确性。
        if (!phone.matches("^\\d{11}$")) {
//            ^\d{11}$ 十一位整数
            return ResponseResult.failResult("请输入正确的手机号");
        }

//      3.校验用户输入的验证码与会话中存储的验证码一致
        String captchaCode = session.getAttribute("verifyCode").toString();
        if (!verifyCode.toLowerCase().equals(captchaCode)) {
            return ResponseResult.failResult("验证码错误");
        }
//      4.调用userService.login方法验证用户信息。
        User user = userService.login(phone, password);

//      5.若登录成功，将用户ID存入会话，并返回用户信息；否则返回失败信息
        if (user != null) {
            // 用户登录后,往session里保存用户Id,用来后续判断用户是否已登录
            session.setAttribute("userId", user.getId());

            // 使用UserVo重新封装user
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return ResponseResult.okResult(userVo);
        } else {
            return ResponseResult.failResult("登陆失败");
        }
    }
    /*
    注册
     */
    @PostMapping("/reg")
    public ResponseResult register(String phone, String password, String password2, String verifyCode, HttpSession session){
//        检查手机号和密码是否为空，如果为空则返回提示“手机号或密码不能为空”。
        if (phone.isEmpty() || password.isEmpty()) {
            return ResponseResult.failResult("手机号或密码不能为空");
        }

//        检查验证码是否为空，如果为空则返回提示“验证码不能为空”。
        if (verifyCode.isEmpty()) {
            return ResponseResult.failResult("验证码不能为空");
        }

//        检查手机号格式是否正确（11位数字），如果不符合则返回提示“请输入正确的手机号”。
        if (!phone.matches("^\\d{11}$")) {
            return ResponseResult.failResult("请输入正确的手机号");
        }

//        检查两次输入的密码是否一致，如果不一致则返回提示“确认密码不一致”。
        if (!password.equals(password2)){
            return ResponseResult.failResult("确认密码不一致");
        }

//        从session中获取正确的验证码并转为小写。
        String captchaCode = session.getAttribute("verifyCode").toString();

//        比较用户输入的验证码与正确的验证码，如果不匹配则返回提示“验证码错误”。
        if (!verifyCode.toLowerCase().equals(captchaCode)) {
            return ResponseResult.failResult("验证码错误");
        }

//        调用userService.userExists(phone)方法检查该手机号是否已注册，如果已注册则返回提示“手机号已注册”,
//        如果注册失败，则返回注册失败的提示。
        if (userService.userExists(phone)){
            return ResponseResult.failResult("手机号已注册");
        }
        int result = userService.register(phone, password);
        if(result == 1){
            return ResponseResult.okResult();
        }else {
            return ResponseResult.failResult("注册失败");
        }
    }


//    函数用于处理HTTP GET请求"/logout"。它接收一个HTTP会话参数session，
//    移除其中的"userId"信息，然后返回一个表示操作成功的响应结果。
    @GetMapping("/logout")
    public ResponseResult logout(HttpSession session){
        session.removeAttribute("userId");
        return ResponseResult.okResult();
    }


//    检查用户是否已登录。
    @GetMapping("/isLogin")
    public ResponseResult islogin(HttpSession session){
//        检查userId：从会话中获取名为userId的对象。
        Object userId = session.getAttribute("userId");
//        如果userId为空，函数返回一个失败的结果，并附带“未登录”的消息。
//        如果userId非空，函数调用getUser方法，传入userId（转换为整数）作为参数。
        if (userId == null){
            return  ResponseResult.failResult("未登录");
        }else {
            return this.getUser(Integer.valueOf(userId.toString()));
        }
    }


//    通过HTTP GET请求提供分页查询用户列表的服务，
//    允许客户端通过网页URL指定或使用默认值来确定查询的页码和每页显示数量，
//    并将查询结果以封装好的形式返回给客户端。
    @GetMapping("/admin/list")
    public ResponseResult getUserList(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        return userService.getUserList(pageNum,pageSize);
    }


//    当客户端发送GET请求到/admin/{id}这个URL，
//    并提供一个具体的id时，服务器会调用userService.getUser(id)方法，从数据库中查找对应ID的用户信息，
//    并将查询结果封装成ResponseResult对象返回给客户端。
    @GetMapping("/admin/{id}")
    public ResponseResult getUser(@PathVariable("id") Integer id){
        return userService.getUser(id);
    }


//    理用户信息更新的请求，将请求体中的用户信息更新到系统中。
    @PostMapping("/admin/update")
    public ResponseResult updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }



//    通过POST请求访问"/admin/delete"路径，接收一个包含多个整数ID的请求体，
//    然后调用userService对象的deleteUser方法，传入接收到的ID列表，
//    并返回deleteUser方法的返回结果。
    @PostMapping("/admin/delete")
    public ResponseResult deleteUser(@RequestBody List<Integer> ids){
        return userService.deleteUser(ids);
    }

}
