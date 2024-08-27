package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import static com.jiang.mall.domain.entity.Propertie.AdminRoleId;

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

//    @GetMapping({"", "/", "/index", "/index.html"})
//    public String index(Model, HttpServletRequest request) {
//        User user = userService.getUserInfo((Integer) request.getSession().getAttribute("UserId"));
//
//        // 将用户对象添加到模型中，以便在视图中访问
//        model.addAttribute("user", user);
//
//        // 返回视图名称，Thymeleaf会自动渲染这个视图
//        request.setAttribute("path", "index");
//        return "user/index";
//    }


//    @GetMapping({"/login", "/login.html"})
//    public String login() {
//        return "user/login";
//    }
//    @GetMapping({"/register_new"})
//    public String register_new() {
//        return "user/register_new";
//    }

//    @PostMapping("/register")
//    public String register(@RequestParam("username") String username,
//                           @RequestParam("password") String password,
//                           @RequestParam("confirmPassword") String confirmPassword,
//                           @RequestParam("email") String email,
//                           @RequestParam("phone") String phone,
//                           @RequestParam("firstName") String firstName,
//                           @RequestParam("lastName") String lastName,
//                           @RequestParam("birthday") String birthDate,
//                           @RequestParam("captcha") String captcha,
//                           HttpSession session) {
//        if (!StringUtils.hasText(captcha)) {
//            session.removeAttribute("captcha");
//            session.setAttribute("errorMsg", "验证码不能为空");
//            return "user/login";
//        }
//        // 获取session中的验证码
//        ShearCaptcha storedVerCode = (ShearCaptcha) session.getAttribute("captcha");
//
//        // 判断验证码
//        if (!storedVerCode.verify(captcha)) {
//            session.removeAttribute("captcha");
//            session.setAttribute("errorMsg", "验证码错误");
//            return "user/register";
//        }
//        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)||!StringUtils.hasText(email)) {
//            session.setAttribute("errorMsg", "请输入完整的注册信息");
//            return "user/register";
//        }
//        if (StringUtils.hasText(phone) && !phone.matches(regex_phone)){
//            session.setAttribute("errorMsg", "手机号格式不正确");
//            return "user/register";
//        }
//        if (!password.equals(confirmPassword)) {
//            session.setAttribute("errorMsg", "两次密码输入不一致");
//            return "user/register";
//        }
//
//        if (userService.queryByUserName(username)) {
//            session.setAttribute("errorMsg", "用户名已存在");
//            return "user/register";
//        }
//        if (userService.queryByEmail(email)) {
//            session.setAttribute("errorMsg", "邮箱已存在");
//            return "user/register";
//        }
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(password);
//        user.setEmail(email);
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        try {
//            LocalDate localDate = LocalDate.parse(birthDate, formatter);
//            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            user.setBirthDate(date);
//        } catch (DateTimeParseException e) {
//            session.setAttribute("errorMsg", "日期格式不正确，请使用yyyy-MM-dd");
//            return "user/register";
//        }
//        user.setPhone(phone);
//        if (!userService.register(user)) {
//            session.setAttribute("errorMsg", "注册失败");
//            return "user/register";
//        }else {
//            session.removeAttribute("errorMsg");
//            return "/user/login";
//        }
//    }

//    @GetMapping({"/register", "/register.html"})
//    public String getRegistrationPage(@SessionAttribute(name = "UserId", required = false) String UserId) {
//        if (UserId != null) {
//            // 如果session中有UserId，表示用户已完成第一步，显示第二步
//            return "/user/register_step2";
//        } else {
//            // 如果session中没有UserId，显示第一步
//            return "/user/register_step1";
//        }
//    }
//    @GetMapping({"/register_step1", "/register_step1.html,","registerStep1"})
//    public String getRegister_step1(@SessionAttribute(name = "UserId", required = false) String UserId) {
//        if (UserId != null) {
//            // 如果session中有UserId，表示用户已完成第一步，显示第二步
//            return "/user/register_step2";
//        } else {
//            // 如果session中没有UserId，显示第一步
//            return "/user/register_step1";
//        }
//    }
//
//    @GetMapping({"/register_step2", "/register_step2.html","/registerStep2"})
//    public String getRegister_step2(@SessionAttribute(name = "UserId", required = false) String UserId) {
//        if (UserId != null) {
//            // 如果session中有UserId，表示用户已完成第一步，显示第二步
//            return "/user/register_step2";
//        } else {
//            // 如果session中没有UserId，显示第一步
//            return "/user/register_step1";
//        }
//    }


//    @PostMapping("/registerStep1")
//    public String registerStep1(@RequestParam("username") String username,
//                                             @RequestParam("password") String password,
//                                             @RequestParam("confirmPassword") String confirmPassword,
//                                             @RequestParam("email") String email,
//                                             @RequestParam("captcha") String captcha,
//                                             HttpSession session) {
//        if (!StringUtils.hasText(captcha)) {
//            session.removeAttribute("captcha");
//            session.setAttribute("errorMsg", "验证码不能为空");
//            return "/user/register_step1";
//        }
//        if (StringUtils.hasText(email) && !email.matches(regex_email)){
//            session.setAttribute("errorMsg", "邮箱格式不正确");
//            return "/user/register_step1";
//        }
//
//        // 获取session中的验证码
//        ShearCaptcha storedVerCode = (ShearCaptcha) session.getAttribute("captcha");
//        if (storedVerCode==null) {
//            return "/user/register_step1";
//        }
//        // 判断验证码
//        if (!storedVerCode.verify(captcha)) {
//            session.removeAttribute("captcha");
//            session.setAttribute("errorMsg", "验证码错误");
//            return "/user/register_step1";
//        }
//        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)||!StringUtils.hasText(email)) {
//            session.setAttribute("errorMsg", "请输入完整的注册信息");
//            return "/user/register_step1";
//        }
//        if (!password.equals(confirmPassword)) {
//            session.setAttribute("errorMsg", "两次密码输入不一致");
//            return "/user/register_step1";
//        }
//
//        if (userService.queryByEmail(email)) {
//            session.setAttribute("errorMsg", "邮箱已存在");
//            return "/user/register_step1";
//        }
//
//        if (userService.queryByUserName(username)) {
//            session.setAttribute("errorMsg", "用户名已存在");
//            return "/user/register_step1";
//        }
//
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(password);
//        user.setEmail(email);
//        int UserId = userService.registerStep(user);
//        if (UserId>0) {
//            session.removeAttribute("errorMsg");
//            session.removeAttribute("captcha");
//            session.setAttribute("UserId",UserId);
//            System.out.println(UserId);
//            return "/user/register_step2";
//        }else {
//            session.setAttribute("errorMsg", "注册失败");
//            return "/user/register_step1";
//        }
//    }

//    @PostMapping("/registerStep2")
//    public String registerStep2(@RequestParam("phone") String phone,
//                                @RequestParam("firstName") String firstName,
//                                @RequestParam("lastName") String lastName,
//                                @RequestParam("birthday") String birthDate,
//                                HttpSession session) {
//        // 获取session中的账号id
//        int UserId = (int) session.getAttribute("UserId");
//
//        if (StringUtils.hasText(phone) && !phone.matches(regex_phone)){
//            session.setAttribute("errorMsg", "手机号格式不正确");
//            return "/user/register_step2";
//        }
//
//        User user = new User();
//        user.setId(UserId);
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        try {
//            LocalDate localDate = LocalDate.parse(birthDate, formatter);
//            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            user.setBirthDate(date);
//        } catch (DateTimeParseException e) {
//            session.setAttribute("errorMsg", "日期格式不正确，请使用yyyy-MM-dd");
//            return "user/register_step2";
//        }
//        user.setPhone(phone);
//        if (userService.registerStep(user)>0) {
//            session.removeAttribute("errorMsg");
//            session.removeAttribute("UserId");
//            return "redirect:user/login";
//        }else {
//            session.setAttribute("errorMsg", "用户个人信息保存失败");
//            return "/user/register_step2";
//        }
//    }
//
    @PostMapping(value = "/login")
    public ResponseResult login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("captcha") String captcha,
                        HttpSession session) {
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }
        // 获取session中的验证码
        String captchaCode = session.getAttribute("captcha").toString();

        // 判断验证码
        if (!captchaCode.toLowerCase().equals(captcha)) {
            session.removeAttribute("captcha");
            return ResponseResult.failResult("验证码错误");
        }
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ResponseResult.failResult("用户名或密码不能为空");
        }

        User user = userService.login(username, password);
        if (user != null) {
            session.removeAttribute("captcha");
            session.setAttribute("UserId", user.getId());
            session.setAttribute("UserName", user.getUsername());
            session.setAttribute("UserRole", user.getRoleId());
            session.setAttribute("UserEmail", user.getEmail());
            session.setAttribute("UserPhone", user.getPhone());
            session.setAttribute("UserFirstName", user.getFirstName());
            session.setAttribute("UserLastName", user.getLastName());
            session.setAttribute("UserBirthDate", user.getBirthDate());
            if (user.getRoleId() >= AdminRoleId) {
                session.setAttribute("UserIsAdmin", true);
            } else {
                session.setAttribute("UserIsAdmin", false);
            }
            session.setAttribute("UserIsLogin",true);
            //session过期时间设置为7200秒
            session.setMaxInactiveInterval(60 * 60 * 2);
            return ResponseResult.okResult();
        } else {
            return ResponseResult.failResult("用户不存在或用户名或密码错误");
        }
    }
//
//    @PostMapping("/modify/password")
//    public String modifyPassword(@RequestParam("UserId") Integer UserId,
//                                  @RequestParam("oldPassword") String oldPassword,
//                                  @RequestParam("newPassword") String newPassword) {
//        return userService.modifyPassword(UserId, oldPassword, newPassword)? "redirect:/user/index" : "redirect:/user/login";
//    }
//
//    @PostMapping("/modify/info")
//    public String modifyUserInfo(@RequestParam("UserId") Integer UserId,
//                                  @RequestBody User userInfo) {
//        userInfo.setId(UserId);
//        return userService.modifyUserInfo(userInfo)? "redirect:/user/index" : "redirect:/user/login";
//    }
//
//    @PostMapping("/modify/lock")
//    public String lockUser(@RequestParam("UserId") Integer UserId) {
//        return userService.lockUser(UserId)? "redirect:/user/login" : "redirect:/user/index";
//    }
//
//
//    @PostMapping("/modify/self-lock")
//    @ResponseBody
//    public String selfLock(@RequestParam("UserId") Integer UserId, HttpSession session) {
//		User user = userService.getUserInfo(UserId);
//		User adminUser = userService.getUserInfo((Integer) session.getAttribute("UserId"));
//		if (adminUser.getRoleId() >user.getRoleId()){
//			return userService.lockUser(UserId)? "用户锁定成功！" : "用户锁定失败！";
//		}else {
//			return "权限不足，修改失败！";
//		}
//    }

    //登录
//    @PostMapping("/login")
//    public ResponseResult login(String phone, String password, String verifyCode, HttpSession session) {
////      1.检查手机号、密码及验证码是否完整
//        if (phone.isEmpty() || password.isEmpty()) {
//            return ResponseResult.failResult("手机号或密码不能为空");
//        }
//        if (verifyCode.isEmpty()) {
//            return ResponseResult.failResult("验证码不能为空");
//        }
//
////      2.验证手机号格式正确性。
//        if (!phone.matches("^\\d{11}$")) {
////            ^\d{11}$ 十一位整数
//            return ResponseResult.failResult("请输入正确的手机号");
//        }
//
////      3.校验用户输入的验证码与会话中存储的验证码一致
//        String captchaCode = session.getAttribute("verifyCode").toString();
//        if (!verifyCode.toLowerCase().equals(captchaCode)) {
//            return ResponseResult.failResult("验证码错误");
//        }
////      4.调用userService.login方法验证用户信息。
//        User user = userService.login(phone, password);
//
////      5.若登录成功，将用户ID存入会话，并返回用户信息；否则返回失败信息
//        if (user != null) {
//            // 用户登录后,往session里保存用户Id,用来后续判断用户是否已登录
//            session.setAttribute("userId", user.getId());
//
//            // 使用UserVo重新封装user
//            UserVo userVo = new UserVo();
//            BeanUtils.copyProperties(user, userVo);
//            return ResponseResult.okResult(userVo);
//        } else {
//            return ResponseResult.failResult("登陆失败");
//        }
//    }
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
